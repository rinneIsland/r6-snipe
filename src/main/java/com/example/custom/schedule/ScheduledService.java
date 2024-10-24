package com.example.custom.schedule;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.custom.pojo.*;
import com.example.custom.pojo.buy.*;
import com.example.custom.pojo.itemDate.ItemJsonRootBean;
import com.example.custom.pojo.itemPrice.Day;
import com.example.custom.pojo.itemPrice.Hour;
import com.example.custom.pojo.itemPrice.ItemHisPrice;
import com.example.custom.pojo.sell.JsonRootBeanSell;
import com.example.custom.pojo.sellDate.PriceHistory;
import com.example.custom.pojo.sellDate.SellDate;
import com.zjiecode.wxpusher.client.WxPusher;
import com.zjiecode.wxpusher.client.bean.Message;
import com.zjiecode.wxpusher.client.bean.MessageResult;
import com.zjiecode.wxpusher.client.bean.Result;
import net.minidev.json.writer.JsonReader;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.util.EntityUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author index
 * @version 1.0
 * Create by 2022/9/3 21:44
 */
@Component
public class ScheduledService {
    private final CloseableHttpClient httpClient;

    public ScheduledService(CloseableHttpClient httpClient) {
        this.httpClient = httpClient;
    }

    public static final Logger LOGGER = LoggerFactory.getLogger(ScheduledService.class);
    private static final List<Nodes> switchNum = null;
    public static String expirationTime = null;
    public static String orderKey = null;
    public static boolean hightSpeed = false;
    private String exitItemId = null;
    private Instant highSpeedTime;
    public static Map<String, String> sellTradeMap = new HashMap<String, String>();
    private int waitTime = 0;
    public static List<Nodes> beforItem;
    public static List<String> lowerItemList = new LinkedList<String>();
    public static List<String> minLowerItemList = new LinkedList<String>();

    //晚上高频监控模式
    @Scheduled(cron = "* * 0,1,2,3,4,5,6,7,18,19,21,22,23 * * ?")
    public void scheduled5() {
        if (orderKey == null || orderKey.isEmpty()) {
            createdCreds();
        }

        Instant instant = Instant.parse(expirationTime);

        // 获取当前时间的Instant对象
        Instant now = Instant.now();
        // 比较给定时间与当前时间
        // 计算给定时间与当前时间之间的差值
        Duration duration = Duration.between(now, instant);
        // 如果给定时间在当前时间之前，或者距离当前时间不足30分钟
        if (duration.getSeconds() <= 1800) {
            createdCreds();
        }
        CloseableHttpResponse httpGetResponse = null;
        try {
            HttpPost httpPost = new HttpPost("https://public-ubiservices.ubi.com/v1/profiles/me/uplay/graphql");
            httpPost.setHeader("ubi-appid", "80a4a0e8-8797-440f-8f4c-eaba87d0fdda");
            httpPost.setHeader("ubi-sessionid", "c5ba85bd-7d79-453e-b841-c064e111abec");
            httpPost.setHeader("ubi-localecode", "zh-TW");

            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                    "(KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36 Edg/85.0.564.70");
            httpPost.setHeader("authorization", orderKey);
            String jsonBody = "{\"operationName\":\"GetSellableItems\",\"variables\":{\"withOwnership\":false,\"spaceId\":\"0d2ae42d-4c27-4cb7-af6c-2099062302bb\",\"limit\":30,\"offset\":0,\"filterBy\":{\"types\":[],\"tags\":[]},\"sortBy\":{\"field\":\"ACTIVE_COUNT\",\"orderType\":\"Sell\",\"direction\":\"ASC\",\"paymentItemId\":\"9ef71262-515b-46e8-b9a8-b6b6ad456c67\"}},\"query\":\"query GetSellableItems($spaceId: String!, $limit: Int!, $offset: Int, $filterBy: MarketableItemFilter, $sortBy: MarketableItemSort) {\\n  game(spaceId: $spaceId) {\\n    viewer {\\n      meta {\\n        marketableItems(\\n          limit: $limit\\n          offset: $offset\\n          filterBy: $filterBy\\n          sortBy: $sortBy\\n          withMarketData: true\\n        ) {\\n          nodes {\\n            ...MarketableItemFragment\\n          }\\n        }\\n      }\\n    }\\n  }\\n}\\n\\nfragment MarketableItemFragment on MarketableItem {\\n  item {\\n    ...SecondaryStoreItemFragment\\n  }\\n  marketData {\\n    ...MarketDataFragment\\n  }\\n}\\n\\nfragment SecondaryStoreItemFragment on SecondaryStoreItem {\\n  itemId\\n  name\\n  tags\\n}\\n\\nfragment MarketDataFragment on MarketableItemMarketData {\\n  sellStats {\\n    lowestPrice\\n    highestPrice\\n    activeCount\\n  }\\n  buyStats {\\n    lowestPrice\\n    highestPrice\\n    activeCount\\n  }\\n  lastSoldAt {\\n    price\\n    performedAt\\n  }\\n}\"}";
            StringEntity entity1 = new StringEntity(jsonBody);
            httpPost.setEntity(entity1);
            httpPost.setHeader("Content-Type", "application/json");

            httpGetResponse = httpClient.execute(httpPost);

            HttpEntity entity = httpGetResponse.getEntity();

            String result = EntityUtils.toString(entity);

            JsonRootBeanSell jsonRootBean = com.alibaba.fastjson.JSONObject.parseObject(result, JsonRootBeanSell.class);
            List<Nodes> nodes = jsonRootBean.getData().getGame().getViewer().getMeta().getMarketableItems().getNodes();

            StringBuilder stringBuilder = new StringBuilder();
            nodes.stream().filter(a -> {
                String itemId = a.getItem().getItemId();
                List<SellStats> sellStats = a.getMarketData().getSellStats();
                int sellNum = sellStats != null ? sellStats.get(0).getActiveCount() : 0;
                int hSellPrice = sellStats != null ? sellStats.get(0).getHighestPrice() : 0;
                List<LastSoldAt> lastSoldAt = a.getMarketData().getLastSoldAt();
                int lastPrice = lastSoldAt != null ? lastSoldAt.get(0).getPrice() : 0;
                List<BuyStats> buyStats = a.getMarketData().getBuyStats();
                int buyNum = buyStats != null ? buyStats.get(0).getActiveCount() : 0;
                int buyPrice = buyStats != null ? buyStats.get(0).getHighestPrice() : 120;
                if ("0b90d119-4a3e-4c24-8bfd-ad6d52638458".equals(itemId) || buyNum > 100) {
                    return false;
                }
//                if (monitorItemId !=null && monitorItemId.equals(itemId)){
//                    return false;
//                }
                int prePrice = 69999;

                if (!"HEAVY METTLE".equals(a.getItem().getName()) && !lowerItemList.contains(itemId)) {
                    if (sellNum <= 4) {
                        if (hSellPrice > 10000) {
                            prePrice = hSellPrice - 1000;
                        } else {
                            if (a.getItem().getTags().contains("rarity_rare")) {
                                prePrice = 34999;
                            } else if (a.getItem().getTags().contains("rarity_uncommon")) {
                                prePrice = 24000;
                            }
                        }
                        if (sellNum <= 1) {
                            try {
                                if (hSellPrice < 10000 || sellNum == 0) {
                                    Thread.sleep(1200);
                                } else {
                                    Thread.sleep(800);
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            createdSell(itemId, orderKey, prePrice);
                        }
                        if (buyNum > 10 && buyPrice <= 2000 && buyPrice >= 100) {
                            try {
                                if (sellNum >= 4) {
                                    Thread.sleep(1500);
                                } else {
                                    Thread.sleep(1000);
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            createdSell(itemId, orderKey, prePrice);
                        }
                        if (sellTradeMap.get(itemId) == null) {
                            String newTradeId = getNewSellTradeId();
                            if (newTradeId != null) {
                                cancelOrder(newTradeId);
                            }
                        }

                        if (exitItemId != null && exitItemId.equals(itemId)) {
                            return false;
                        }
                        exitItemId = itemId;
                        lowerItemList.add(itemId);
                        LOGGER.info("开启监控模式" + JSONObject.toJSONString(a));
                        LOGGER.info("开启监控模式" + JSONObject.toJSONString(beforItem));
                    }
                }

                int sellPrice = sellStats != null ? sellStats.get(0).getLowestPrice() : 0;

                if (sellPrice < buyPrice && buyNum > 10) {
                    if (hSellPrice > 10000) {
                        prePrice = hSellPrice - 1000;
                    } else {
                        if (a.getItem().getTags().contains("rarity_rare")) {
                            prePrice = 34000;
                        } else if (a.getItem().getTags().contains("rarity_uncommon")) {
                            prePrice = 20000;
                        }
                    }
                    try {
                        if (sellNum >= 3) {
                            Thread.sleep(1500);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String tradeId = sellTradeMap.get(itemId);
                    // 记录结束时间
                    if (tradeId != null) {
                        updateOrder(tradeId, String.valueOf(prePrice));
                    } else {
                        createdSell(itemId, orderKey, prePrice);
                    }

                }
                if (beforItem != null && beforItem.size() != 0) {
                    for (Nodes nodes1 : beforItem) {
                        if (nodes1.getItem().getItemId().equals(itemId)) {
                            List<SellStats> bSellStats = nodes1.getMarketData().getSellStats();
                            int bSellPrice = bSellStats != null ? bSellStats.get(0).getLowestPrice() : 0;
                            int bSellNum = bSellStats != null ? bSellStats.get(0).getActiveCount() : 0;
                            if (bSellNum > sellNum && sellNum <= 5) {
                                LOGGER.info(a.toString());
                            }
                            if (bSellNum != sellNum &&buyNum < 3 && sellNum <= 1) {
                                return true;
                            }
                            return (bSellNum != sellNum && sellPrice >= 9000);
                        }
                    }
                    return sellPrice >= 9000;
                }
                return false;
            }).forEach(nodes1 -> {
                LOGGER.info("低速模式发现订单");
                List<SellStats> sellStats = nodes1.getMarketData().getSellStats();
                int sellPrice = sellStats != null ? sellStats.get(0).getLowestPrice() : 10000;
                int sellNum = sellStats != null ? sellStats.get(0).getActiveCount() : 0;
                List<LastSoldAt> lastSoldAt = nodes1.getMarketData().getLastSoldAt();
                int lastPrice = lastSoldAt != null ? lastSoldAt.get(0).getPrice() : 0;
                stringBuilder.append(nodes1);

                String numberStr = String.valueOf(sellPrice);
                StringBuilder modifiedNumber = new StringBuilder(numberStr);
                boolean modified = false;

                for (int i = 1; i < modifiedNumber.length(); i++) {
                    if (modifiedNumber.charAt(i) != '0') {
                        modifiedNumber.setCharAt(i, (char) (modifiedNumber.charAt(i) - 1));
                        modified = true;
                        break;
                    }
                }
                int orderSellPrice = sellPrice - 100;
                if (modified) {
                    orderSellPrice = Integer.parseInt(modifiedNumber.toString());
                }
                if (orderSellPrice<2000){
                    orderSellPrice = 9999;
                }
                String tradeId = sellTradeMap.get(nodes1.getItem().getItemId());
                try {

                    if (tradeId == null) {
//                        Thread.sleep(3000);
                            if (sellNum==1) {
                                Thread.sleep(600);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                boolean createdSell = false;
                if (tradeId != null) {
                    createdSell = updateOrder(tradeId, String.valueOf(orderSellPrice));
                } else {
                    createdSell = createdSell(nodes1.getItem().getItemId(), orderKey, orderSellPrice);
                }
                if (createdSell) {
                    nodes1.getMarketData().getSellStats().get(0).setLowestPrice(orderSellPrice);
                }
                LOGGER.info(stringBuilder.toString());
            });
            beforItem = nodes;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //白天低频监控模式
    @Scheduled(cron = "0/3 * 8,9,10,11,12,13,14,15,16,17,20 * * ? ")
    public void scheduled2() {
        if (hightSpeed) {
            return;
        }
        if (orderKey == null || orderKey.isEmpty()) {
            createdCreds();
        }

        Instant instant = Instant.parse(expirationTime);

        // 获取当前时间的Instant对象
        Instant now = Instant.now();
        // 比较给定时间与当前时间
        // 计算给定时间与当前时间之间的差值
        Duration duration = Duration.between(now, instant);
        // 如果给定时间在当前时间之前，或者距离当前时间不足30分钟
        if (duration.getSeconds() <= 1800) {
            createdCreds();
        }
        CloseableHttpResponse httpGetResponse = null;
        try {
            // 通过址默认配置创建一个httpClient实例
            HttpPost httpPost = new HttpPost("https://public-ubiservices.ubi.com/v1/profiles/me/uplay/graphql");
            // 设置请求头信息，鉴权
            httpPost.setHeader("ubi-appid", "80a4a0e8-8797-440f-8f4c-eaba87d0fdda");
            httpPost.setHeader("ubi-sessionid", "c5ba85bd-7d79-453e-b841-c064e111abec");
            httpPost.setHeader("ubi-localecode", "zh-TW");

            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                    "(KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36 Edg/85.0.564.70");
            httpPost.setHeader("authorization", orderKey);
            String jsonBody = "{\"operationName\":\"GetSellableItems\",\"variables\":{\"withOwnership\":false,\"spaceId\":\"0d2ae42d-4c27-4cb7-af6c-2099062302bb\",\"limit\":30,\"offset\":0,\"filterBy\":{\"types\":[],\"tags\":[]},\"sortBy\":{\"field\":\"ACTIVE_COUNT\",\"orderType\":\"Sell\",\"direction\":\"ASC\",\"paymentItemId\":\"9ef71262-515b-46e8-b9a8-b6b6ad456c67\"}},\"query\":\"query GetSellableItems($spaceId: String!, $limit: Int!, $offset: Int, $filterBy: MarketableItemFilter, $sortBy: MarketableItemSort) {\\n  game(spaceId: $spaceId) {\\n    viewer {\\n      meta {\\n        marketableItems(\\n          limit: $limit\\n          offset: $offset\\n          filterBy: $filterBy\\n          sortBy: $sortBy\\n          withMarketData: true\\n        ) {\\n          nodes {\\n            ...MarketableItemFragment\\n          }\\n        }\\n      }\\n    }\\n  }\\n}\\n\\nfragment MarketableItemFragment on MarketableItem {\\n  item {\\n    ...SecondaryStoreItemFragment\\n  }\\n  marketData {\\n    ...MarketDataFragment\\n  }\\n}\\n\\nfragment SecondaryStoreItemFragment on SecondaryStoreItem {\\n  itemId\\n  name\\n  tags\\n}\\n\\nfragment MarketDataFragment on MarketableItemMarketData {\\n  sellStats {\\n    lowestPrice\\n    highestPrice\\n    activeCount\\n  }\\n  buyStats {\\n    lowestPrice\\n    highestPrice\\n    activeCount\\n  }\\n  lastSoldAt {\\n    price\\n    performedAt\\n  }\\n}\"}";
            StringEntity entity1 = new StringEntity(jsonBody);
            httpPost.setEntity(entity1);
            // 设置请求头，指定内容类型为JSON
            httpPost.setHeader("Content-Type", "application/json");

            httpGetResponse = httpClient.execute(httpPost);
            // 从响应对象中获取响应内容
            // 通过返回对象获取返回数据
            HttpEntity entity = httpGetResponse.getEntity();
            // 通过EntityUtils中的toString方法将结果转换为字符串
            String result = EntityUtils.toString(entity);

            JsonRootBeanSell jsonRootBean = com.alibaba.fastjson.JSONObject.parseObject(result, JsonRootBeanSell.class);
            List<Nodes> nodes = jsonRootBean.getData().getGame().getViewer().getMeta().getMarketableItems().getNodes();
            // 定义日期时间格式
            // 使用流进行排序
            StringBuilder stringBuilder = new StringBuilder();
            nodes.stream().filter(a -> {
                String itemId = a.getItem().getItemId();
                List<SellStats> sellStats = a.getMarketData().getSellStats();
                int sellNum = sellStats != null ? sellStats.get(0).getActiveCount() : 0;
                int hSellPrice = sellStats != null ? sellStats.get(0).getHighestPrice() : 0;
                List<LastSoldAt> lastSoldAt = a.getMarketData().getLastSoldAt();
                int lastPrice = lastSoldAt != null ? lastSoldAt.get(0).getPrice() : 0;
                List<BuyStats> buyStats = a.getMarketData().getBuyStats();
                int buyNum = buyStats != null ? buyStats.get(0).getActiveCount() : 0;
                int buyPrice = buyStats != null ? buyStats.get(0).getHighestPrice() : 120;
                if ("0b90d119-4a3e-4c24-8bfd-ad6d52638458".equals(itemId) || "f61d7ad6-27df-4802-9b8e-57d80356db87".equals(itemId) || "5faae5dc-13dc-49f1-84a0-a93786833d76".equals(itemId) || buyNum > 100 || "SHADOW LEGACY".equals(a.getItem().getName())) {
                    return false;
                }
//                if (monitorItemId !=null && monitorItemId.equals(itemId)){
//                    return false;
//                }
                int prePrice = 69999;

                if (!"HEAVY METTLE".equals(a.getItem().getName()) && !"4a4a1d41-d476-4bf2-9dab-14086d8b4459".equals(itemId)) {
                    if (sellNum <= 4) {
                        highSpeedTime = Instant.now();
                        if (hSellPrice > 10000) {
                            prePrice = hSellPrice - 1000;
                        } else {
                            if (a.getItem().getTags().contains("rarity_rare")) {
                                prePrice = 34999;
                            } else if (a.getItem().getTags().contains("rarity_uncommon")) {
                                prePrice = 24000;
                            }
                        }
                        if (sellNum <= 1) {
                            try {
                                if (hSellPrice < 10000 || sellNum == 0) {
                                    Thread.sleep(1000);
                                } else if (prePrice > 80000) {
                                    Thread.sleep(800);
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            createdSell(itemId, orderKey, prePrice);
                        }
                        hightSpeed = true;
                        if (sellTradeMap.get(itemId) == null) {
                            String newTradeId = getNewSellTradeId();
                            if (newTradeId != null) {
                                cancelOrder(newTradeId);
                            }
                        }
                        if (buyNum > 10 && buyPrice <= 5000 && buyPrice >= 100) {
                            try {
                                if (sellNum >= 3) {
                                    Thread.sleep(1000);
                                } else {
                                    Thread.sleep(400);
                                }
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            createdSell(itemId, orderKey, prePrice);
                        }
                        if (exitItemId != null && exitItemId.equals(itemId)) {
                            return false;
                        }
                        LOGGER.info(String.valueOf(highSpeedTime));
                        exitItemId = itemId;
                        LOGGER.info("开启监控模式" + JSONObject.toJSONString(a));
                        LOGGER.info("开启监控模式" + JSONObject.toJSONString(beforItem));
                    }
                }


                int sellPrice = sellStats != null ? sellStats.get(0).getLowestPrice() : 0;
                if (sellPrice < buyPrice && buyNum > 10) {
                    if (hSellPrice > 10000) {
                        prePrice = hSellPrice - 1000;
                    } else {
                        if (a.getItem().getTags().contains("rarity_rare")) {
                            prePrice = 34000;
                        } else if (a.getItem().getTags().contains("rarity_uncommon")) {
                            prePrice = 20000;
                        }
                    }
                    try {
                        if (sellNum >= 3) {
                            Thread.sleep(1500);
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    String tradeId = sellTradeMap.get(itemId);
                    // 记录结束时间
                    if (tradeId != null) {
                        updateOrder(tradeId, String.valueOf(prePrice));
                    } else {
                        createdSell(itemId, orderKey, prePrice);
                    }
                    LOGGER.info("超量订单" + a);

                }
                if (beforItem != null && beforItem.size() != 0) {
                    for (Nodes nodes1 : beforItem) {
                        if (nodes1.getItem().getItemId().equals(itemId)) {
                            List<SellStats> bSellStats = nodes1.getMarketData().getSellStats();
                            int bSellPrice = bSellStats != null ? bSellStats.get(0).getLowestPrice() : 0;
                            int bSellNum = bSellStats != null ? bSellStats.get(0).getActiveCount() : 0;

                            return (bSellPrice != sellPrice && sellPrice >= 10000);
                        }
                    }
                    return sellPrice >= 9000;
                }
                return false;
            }).forEach(nodes1 -> {
                LOGGER.info("低速模式发现订单");
                List<SellStats> sellStats = nodes1.getMarketData().getSellStats();
                int sellPrice = sellStats != null ? sellStats.get(0).getLowestPrice() : 10000;
                List<LastSoldAt> lastSoldAt = nodes1.getMarketData().getLastSoldAt();
                int lastPrice = lastSoldAt != null ? lastSoldAt.get(0).getPrice() : 0;
                stringBuilder.append(nodes1);

                String numberStr = String.valueOf(sellPrice);
                StringBuilder modifiedNumber = new StringBuilder(numberStr);
                boolean modified = false;

                for (int i = 1; i < modifiedNumber.length(); i++) {
                    if (modifiedNumber.charAt(i) != '0') {
                        modifiedNumber.setCharAt(i, (char) (modifiedNumber.charAt(i) - 1));
                        modified = true;
                        break;
                    }
                }

                if (!modified) {
                    // 如果首字符后都是0，则首字符减1
                    modifiedNumber.setCharAt(0, (char) (modifiedNumber.charAt(0) - 1));
                }

                int orderSellPrice = Integer.parseInt(modifiedNumber.toString());
                String tradeId = sellTradeMap.get(nodes1.getItem().getItemId());
                if (orderSellPrice == 0) {
                    orderSellPrice = sellPrice - 1000;
                }
                try {

                    if (tradeId == null) {
//                        Thread.sleep(3000);

                        if (lastPrice > 6000) {
                            LOGGER.info("低速模式延迟订单");
                            Thread.sleep(5000);
                        } else {
                            Thread.sleep(1300);
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                boolean createdSell = false;
                boolean newSell = false;

                if (tradeId != null) {
                    createdSell = updateOrder(tradeId, String.valueOf(orderSellPrice));
                } else {
                    createdSell = createdSell(nodes1.getItem().getItemId(), orderKey, orderSellPrice);
                    newSell = true;
                }
                if (createdSell) {
                    nodes1.getMarketData().getSellStats().get(0).setLowestPrice(orderSellPrice);
                }
                LOGGER.info(nodes1.toString());
            });

            beforItem = nodes;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Scheduled(cron = "0/1 * * * * ?  ")
    public void scheduled3() {
        if (!hightSpeed) {
            return;
        }
        if (orderKey == null || orderKey.isEmpty()) {
            createdCreds();
        }

        Instant instant = Instant.parse(expirationTime);

        // 获取当前时间的Instant对象
        Instant now = Instant.now();
        // 比较给定时间与当前时间
        // 计算给定时间与当前时间之间的差值
        Duration duration = Duration.between(now, instant);
        // 如果给定时间在当前时间之前，或者距离当前时间不足30分钟
        if (duration.getSeconds() <= 1800) {
            createdCreds();
        }
        CloseableHttpResponse httpGetResponse = null;
        com.alibaba.fastjson.JSONObject resultJson = null;
        try {
            // 通过址默认配置创建一个httpClient实例
            HttpPost httpPost = new HttpPost("https://public-ubiservices.ubi.com/v1/profiles/me/uplay/graphql");
            // 设置请求头信息，鉴权
            httpPost.setHeader("ubi-appid", "80a4a0e8-8797-440f-8f4c-eaba87d0fdda");
            httpPost.setHeader("ubi-sessionid", "c5ba85bd-7d79-453e-b841-c064e111abec");
            httpPost.setHeader("ubi-localecode", "zh-TW");

            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                    "(KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36 Edg/85.0.564.70");
            httpPost.setHeader("authorization", orderKey);
            String jsonBody = "{\"operationName\":\"GetSellableItems\",\"variables\":{\"withOwnership\":false,\"spaceId\":\"0d2ae42d-4c27-4cb7-af6c-2099062302bb\",\"limit\":10,\"offset\":0,\"filterBy\":{\"types\":[],\"tags\":[]},\"sortBy\":{\"field\":\"ACTIVE_COUNT\",\"orderType\":\"Sell\",\"direction\":\"ASC\",\"paymentItemId\":\"9ef71262-515b-46e8-b9a8-b6b6ad456c67\"}},\"query\":\"query GetSellableItems($spaceId: String!, $limit: Int!, $offset: Int, $filterBy: MarketableItemFilter, $sortBy: MarketableItemSort) {\\n  game(spaceId: $spaceId) {\\n    viewer {\\n      meta {\\n        marketableItems(\\n          limit: $limit\\n          offset: $offset\\n          filterBy: $filterBy\\n          sortBy: $sortBy\\n          withMarketData: true\\n        ) {\\n          nodes {\\n            ...MarketableItemFragment\\n          }\\n        }\\n      }\\n    }\\n  }\\n}\\n\\nfragment MarketableItemFragment on MarketableItem {\\n  item {\\n    ...SecondaryStoreItemFragment\\n  }\\n  marketData {\\n    ...MarketDataFragment\\n  }\\n}\\n\\nfragment SecondaryStoreItemFragment on SecondaryStoreItem {\\n  itemId\\n  name\\n  tags\\n}\\n\\nfragment MarketDataFragment on MarketableItemMarketData {\\n  sellStats {\\n    lowestPrice\\n    highestPrice\\n    activeCount\\n  }\\n  buyStats {\\n    lowestPrice\\n    highestPrice\\n    activeCount\\n  }\\n  lastSoldAt {\\n    price\\n    performedAt\\n  }\\n}\"}";
            StringEntity entity1 = new StringEntity(jsonBody);
            httpPost.setEntity(entity1);
            // 设置请求头，指定内容类型为JSON
            httpPost.setHeader("Content-Type", "application/json");

            httpGetResponse = httpClient.execute(httpPost);
            // 从响应对象中获取响应内容
            // 通过返回对象获取返回数据
            HttpEntity entity = httpGetResponse.getEntity();
            // 通过EntityUtils中的toString方法将结果转换为字符串
            String result = EntityUtils.toString(entity);
            JsonRootBeanSell jsonRootBean = com.alibaba.fastjson.JSONObject.parseObject(result, JsonRootBeanSell.class);
            List<Nodes> nodes = jsonRootBean.getData().getGame().getViewer().getMeta().getMarketableItems().getNodes();
            // 定义日期时间格式
            // 使用流进行排序
            StringBuilder stringBuilder = new StringBuilder();
            // 记录结束时间
            nodes.stream().filter(a -> {
                List<SellStats> sellStats = a.getMarketData().getSellStats();
                List<BuyStats> buyStats = a.getMarketData().getBuyStats();
                int sellPrice = sellStats != null ? sellStats.get(0).getLowestPrice() : 0;
                int buyPrice = buyStats != null ? buyStats.get(0).getHighestPrice() : 0;
                int buyNum = buyStats != null ? buyStats.get(0).getActiveCount() : 0;
                int sellNum = sellStats != null ? sellStats.get(0).getActiveCount() : 0;

                if ("0b90d119-4a3e-4c24-8bfd-ad6d52638458".equals(a.getItem().getItemId()) || "76981f24-7a54-4a3a-b9c9-ad41e9e17cb9".equals(a.getItem().getItemId()) || buyNum > 100) {
                    return false;
                }
                if (beforItem != null && beforItem.size() != 0) {
                    for (Nodes nodes1 : beforItem) {
                        if (nodes1.getItem().getItemId().equals(a.getItem().getItemId())) {
                            List<SellStats> bSellStats = nodes1.getMarketData().getSellStats();
                            int bSellPrice = bSellStats != null ? bSellStats.get(0).getLowestPrice() : 0;
                            int bSellNum = bSellStats != null ? bSellStats.get(0).getActiveCount() : 0;
                            if (sellNum != bSellNum) {
                                LOGGER.info(a.toString());
                            }
                            if (sellNum < bSellNum && buyNum < 3 && sellNum <= 1) {
                                return true;
                            }
                            return (sellNum != bSellNum && sellPrice > 9000);
                        }
                    }
                    return sellPrice >= 16000;
                }
                return false;
            }).forEach(nodes1 -> {
                List<SellStats> sellStats = nodes1.getMarketData().getSellStats();
                int sellPrice = sellStats != null ? sellStats.get(0).getLowestPrice() : 10000;
                int sellNum = sellStats != null ? sellStats.get(0).getActiveCount() : 0;

                stringBuilder.append(nodes1);

                String numberStr = String.valueOf(sellPrice);
                StringBuilder modifiedNumber = new StringBuilder(numberStr);
                boolean modified = false;

                for (int i = 1; i < modifiedNumber.length(); i++) {
                    if (modifiedNumber.charAt(i) != '0') {
                        modifiedNumber.setCharAt(i, (char) (modifiedNumber.charAt(i) - 1));
                        modified = true;
                        break;
                    }
                }
                int orderSellPrice = sellPrice - 100;
                if (modified) {
                    orderSellPrice = Integer.parseInt(modifiedNumber.toString());
                }
                if (orderSellPrice<2000){
                    orderSellPrice = 9999;
                }
                String tradeId = sellTradeMap.get(nodes1.getItem().getItemId());

                    if (tradeId==null){
                        try {
                            if (sellNum==1){
                                Thread.sleep(700);
                            }
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }

                boolean createdSell = false;
                // 记录结束时间
                if (tradeId != null) {
                    createdSell = updateOrder(tradeId, String.valueOf(orderSellPrice));
                } else {
                    createdSell = createdSell(nodes1.getItem().getItemId(), orderKey, orderSellPrice);

                }

                if (createdSell) {
                    nodes1.getMarketData().getSellStats().get(0).setLowestPrice(orderSellPrice);
                }
                LOGGER.info(nodes1.toString());
                LOGGER.info(beforItem.toString());
                LOGGER.info("高速模式发现订单");
            });
            hightSpeed = nodes.stream()
                    .filter(a -> {
                        List<BuyStats> buyStats = a.getMarketData().getBuyStats();
                        int buyNum = buyStats != null ? buyStats.get(0).getActiveCount() : 0;
                        return !a.getItem().getName().equals("HEAVY METTLE") && !a.getItem().getName().equals("SHADOW LEGACY") && buyNum < 80;
                    })
                    .anyMatch(a -> {
                        List<SellStats> sellStats = a.getMarketData().getSellStats();
                        return sellStats == null || sellStats.get(0).getActiveCount() <= 4;
                    });
            if (!hightSpeed) {
                LOGGER.info("退出高速模式" + hightSpeed + JSONObject.toJSONString(nodes));
                if (minLowerItemList != null) {
                    minLowerItemList.clear();
                }
            }
            // 如果给定时间在当前时间之前，或者距离当前时间不足30分钟
            if (duration.getSeconds() >= 1800) {
                LOGGER.info("超时退出高速模式" + JSONObject.toJSONString(nodes));
                hightSpeed = false;
                if (minLowerItemList != null) {
                    minLowerItemList.clear();
                }
            }

            beforItem = nodes;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //输入F12控制台获取的cookie，打开市场随便打开一个请求authorization所对应的值
    public String getOrderCookie() {
        InputStream inputStream = JsonReader.class.getClassLoader().getResourceAsStream("data.json");
        String authorization = "";
        if (inputStream != null) {
            try {
                // 将 InputStream 转换为 InputStreamReader，指定 UTF-8 编码
                InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);

                // 将 JSON 文件的内容读成字符串
                StringBuilder jsonStringBuilder = new StringBuilder();
                int i;
                while ((i = reader.read()) != -1) {
                    jsonStringBuilder.append((char) i);
                }

                // 关闭 reader
                reader.close();

                // 将 JSON 字符串解析为 Map<String, String>
                Map<String, String> map = JSON.parseObject(jsonStringBuilder.toString(), Map.class);
                authorization = map.get("TOKEN");
                waitTime = Integer.parseInt(map.get("WaitTime"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("文件未找到");
        }
        return authorization;
    }

    public boolean createdSell(String itemId, String orderKey, int sellPrice) {
        CloseableHttpResponse httpGetResponse = null;
        // 通过址默认配置创建一个httpClient实例
        String jsonBody2 = "[{\"operationName\":\"CreateSellOrder\",\"variables\":{\"spaceId\":\"0d2ae42d-4c27-4cb7-af6c-2099062302bb\",\"tradeItems\":[{\"itemId\":\"" + itemId + "\",\"quantity\":1}],\"paymentOptions\":[{\"paymentItemId\":\"9ef71262-515b-46e8-b9a8-b6b6ad456c67\",\"price\":" + sellPrice + "}]},\"query\":\"mutation CreateSellOrder($spaceId: String!, $tradeItems: [TradeOrderItem!]!, $paymentOptions: [PaymentItem!]!) {\\n  createSellOrder(\\n    spaceId: $spaceId\\n    tradeItems: $tradeItems\\n    paymentOptions: $paymentOptions\\n  ) {\\n    trade {\\n      ...TradeFragment\\n    }\\n  }\\n}\\n\\nfragment TradeFragment on Trade {\\n  tradeId\\n  createdAt\\n  tradeItems {\\n    item {\\n      ...SecondaryStoreItemFragment\\n    }\\n  }\\n  paymentOptions {\\n    price\\n  }\\n}\\n\\nfragment SecondaryStoreItemFragment on SecondaryStoreItem {\\n  itemId\\n  name\\n}\"}]";
        HttpPost httpPost1 = new HttpPost("https://public-ubiservices.ubi.com/v1/profiles/me/uplay/graphql");
        httpPost1.setHeader("ubi-appid", "80a4a0e8-8797-440f-8f4c-eaba87d0fdda");
        httpPost1.setHeader("ubi-sessionid", "79cbfdee-e477-4958-8424-9199b870b412");
        httpPost1.setHeader("ubi-localecode", "zh-TW");
        httpPost1.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36 Edg/85.0.564.70");
        httpPost1.setHeader("authorization", orderKey);
        httpPost1.setHeader("Content-Type", "application/json");
        String result = null;
        try {
            StringEntity entity3 = new StringEntity(jsonBody2);
            httpPost1.setEntity(entity3);
            httpGetResponse = httpClient.execute(httpPost1);
            HttpEntity entity2 = httpGetResponse.getEntity();
            // 通过EntityUtils中的toString方法将结果转换为字符串
            result = EntityUtils.toString(entity2);
            String patternString = "\"tradeId\":\"([^\"]+)\"";
            Pattern pattern = Pattern.compile(patternString);
            // 创建匹配器
            Matcher matcher = pattern.matcher(result);
            LOGGER.info(result);
            LOGGER.info("创建售单");
            // 查找匹配项
            if (matcher.find()) {
                sellTradeMap.put(itemId, matcher.group(1));
                return true;
            } else {
                return false;
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void cancelOrder(String tradeId) {
        LOGGER.info("取消订单");
        CloseableHttpResponse httpGetResponse = null;
        // 通过址默认配置创建一个httpClient实例
        String jsonBody2 = "[{\"operationName\":\"CancelOrder\",\"variables\":{\"spaceId\":\"0d2ae42d-4c27-4cb7-af6c-2099062302bb\",\"tradeId\":\"" + tradeId + "\"},\"query\":\"mutation CancelOrder($spaceId: String!, $tradeId: String!) {\\n  cancelOrder(spaceId: $spaceId, tradeId: $tradeId) {\\n    trade {\\n      ...TradeFragment\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\\nfragment TradeFragment on Trade {\\n  id\\n  tradeId\\n  state\\n  category\\n  createdAt\\n  expiresAt\\n  lastModifiedAt\\n  failures\\n  tradeItems {\\n    id\\n    item {\\n      ...SecondaryStoreItemFragment\\n      ...SecondaryStoreItemOwnershipFragment\\n      __typename\\n    }\\n    __typename\\n  }\\n  payment {\\n    id\\n    item {\\n      ...SecondaryStoreItemQuantityFragment\\n      __typename\\n    }\\n    price\\n    transactionFee\\n    __typename\\n  }\\n  paymentOptions {\\n    id\\n    item {\\n      ...SecondaryStoreItemQuantityFragment\\n      __typename\\n    }\\n    price\\n    transactionFee\\n    __typename\\n  }\\n  paymentProposal {\\n    id\\n    item {\\n      ...SecondaryStoreItemQuantityFragment\\n      __typename\\n    }\\n    price\\n    __typename\\n  }\\n  viewer {\\n    meta {\\n      id\\n      tradesLimitations {\\n        ...TradesLimitationsFragment\\n        __typename\\n      }\\n      __typename\\n    }\\n    __typename\\n  }\\n  __typename\\n}\\n\\nfragment SecondaryStoreItemFragment on SecondaryStoreItem {\\n  id\\n  assetUrl\\n  itemId\\n  name\\n  tags\\n  type\\n  viewer {\\n    meta {\\n      id\\n      isReserved\\n      __typename\\n    }\\n    __typename\\n  }\\n  __typename\\n}\\n\\nfragment SecondaryStoreItemOwnershipFragment on SecondaryStoreItem {\\n  viewer {\\n    meta {\\n      id\\n      isOwned\\n      quantity\\n      __typename\\n    }\\n    __typename\\n  }\\n  __typename\\n}\\n\\nfragment SecondaryStoreItemQuantityFragment on SecondaryStoreItem {\\n  viewer {\\n    meta {\\n      id\\n      quantity\\n      __typename\\n    }\\n    __typename\\n  }\\n  __typename\\n}\\n\\nfragment TradesLimitationsFragment on UserGameTradesLimitations {\\n  id\\n  buy {\\n    resolvedTransactionCount\\n    resolvedTransactionPeriodInMinutes\\n    activeTransactionCount\\n    __typename\\n  }\\n  sell {\\n    resolvedTransactionCount\\n    resolvedTransactionPeriodInMinutes\\n    activeTransactionCount\\n    resaleLocks {\\n      itemId\\n      expiresAt\\n      __typename\\n    }\\n    __typename\\n  }\\n  __typename\\n}\"}]";
        HttpPost httpPost1 = new HttpPost("https://public-ubiservices.ubi.com/v1/profiles/me/uplay/graphql");
        httpPost1.setHeader("ubi-appid", "80a4a0e8-8797-440f-8f4c-eaba87d0fdda");
        httpPost1.setHeader("ubi-sessionid", "79cbfdee-e477-4958-8424-9199b870b412");
        httpPost1.setHeader("ubi-localecode", "zh-TW");
        httpPost1.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36 Edg/85.0.564.70");
        httpPost1.setHeader("authorization", orderKey);
        httpPost1.setHeader("Content-Type", "application/json");
        String result = null;
        try {
            StringEntity entity3 = new StringEntity(jsonBody2);
            httpPost1.setEntity(entity3);
            httpGetResponse = httpClient.execute(httpPost1);
            HttpEntity entity2 = httpGetResponse.getEntity();
            // 通过EntityUtils中的toString方法将结果转换为字符串
            result = EntityUtils.toString(entity2);
        } catch (IOException e) {
            e.printStackTrace();
        }
        LOGGER.info(result);

    }

    public boolean updateOrder(String tradeId, String price) {
        LOGGER.info("更新订单" + price);

        CloseableHttpResponse httpGetResponse = null;
        // 通过址默认配置创建一个httpClient实例
        String jsonBody2 = "[{\"operationName\":\"UpdateSellOrder\",\"variables\":{\"spaceId\":\"0d2ae42d-4c27-4cb7-af6c-2099062302bb\",\"tradeId\":\"" + tradeId + "\",\"paymentOptions\":[{\"paymentItemId\":\"9ef71262-515b-46e8-b9a8-b6b6ad456c67\",\"price\":" + price + "}]},\"query\":\"mutation UpdateSellOrder($spaceId: String!, $tradeId: String!, $paymentOptions: [PaymentItem!]!) {\\n  updateSellOrder(\\n    spaceId: $spaceId\\n    tradeId: $tradeId\\n    paymentOptions: $paymentOptions\\n  ) {\\n    trade {\\n      ...TradeFragment\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\\nfragment TradeFragment on Trade {\\n  id\\n  tradeId\\n  state\\n  category\\n  createdAt\\n  expiresAt\\n  lastModifiedAt\\n  failures\\n  tradeItems {\\n    id\\n    item {\\n      ...SecondaryStoreItemFragment\\n      ...SecondaryStoreItemOwnershipFragment\\n      __typename\\n    }\\n    __typename\\n  }\\n  payment {\\n    id\\n    item {\\n      ...SecondaryStoreItemQuantityFragment\\n      __typename\\n    }\\n    price\\n    transactionFee\\n    __typename\\n  }\\n  paymentOptions {\\n    id\\n    item {\\n      ...SecondaryStoreItemQuantityFragment\\n      __typename\\n    }\\n    price\\n    transactionFee\\n    __typename\\n  }\\n  paymentProposal {\\n    id\\n    item {\\n      ...SecondaryStoreItemQuantityFragment\\n      __typename\\n    }\\n    price\\n    __typename\\n  }\\n  viewer {\\n    meta {\\n      id\\n      tradesLimitations {\\n        ...TradesLimitationsFragment\\n        __typename\\n      }\\n      __typename\\n    }\\n    __typename\\n  }\\n  __typename\\n}\\n\\nfragment SecondaryStoreItemFragment on SecondaryStoreItem {\\n  id\\n  assetUrl\\n  itemId\\n  name\\n  tags\\n  type\\n  viewer {\\n    meta {\\n      id\\n      isReserved\\n      __typename\\n    }\\n    __typename\\n  }\\n  __typename\\n}\\n\\nfragment SecondaryStoreItemOwnershipFragment on SecondaryStoreItem {\\n  viewer {\\n    meta {\\n      id\\n      isOwned\\n      quantity\\n      __typename\\n    }\\n    __typename\\n  }\\n  __typename\\n}\\n\\nfragment SecondaryStoreItemQuantityFragment on SecondaryStoreItem {\\n  viewer {\\n    meta {\\n      id\\n      quantity\\n      __typename\\n    }\\n    __typename\\n  }\\n  __typename\\n}\\n\\nfragment TradesLimitationsFragment on UserGameTradesLimitations {\\n  id\\n  buy {\\n    resolvedTransactionCount\\n    resolvedTransactionPeriodInMinutes\\n    activeTransactionCount\\n    __typename\\n  }\\n  sell {\\n    resolvedTransactionCount\\n    resolvedTransactionPeriodInMinutes\\n    activeTransactionCount\\n    resaleLocks {\\n      itemId\\n      expiresAt\\n      __typename\\n    }\\n    __typename\\n  }\\n  __typename\\n}\"}]";
        HttpPost httpPost1 = new HttpPost("https://public-ubiservices.ubi.com/v1/profiles/me/uplay/graphql");
        httpPost1.setHeader("ubi-appid", "80a4a0e8-8797-440f-8f4c-eaba87d0fdda");
        httpPost1.setHeader("ubi-sessionid", "79cbfdee-e477-4958-8424-9199b870b412");
        httpPost1.setHeader("ubi-localecode", "zh-TW");
        httpPost1.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                "(KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36 Edg/85.0.564.70");
        httpPost1.setHeader("authorization", orderKey);
        httpPost1.setHeader("Content-Type", "application/json");
        String result = null;
        try {
            StringEntity entity3 = new StringEntity(jsonBody2);
            httpPost1.setEntity(entity3);
            httpGetResponse = httpClient.execute(httpPost1);
            HttpEntity entity2 = httpGetResponse.getEntity();
            // 通过EntityUtils中的toString方法将结果转换为字符串
            result = EntityUtils.toString(entity2);
            String patternString = "\"tradeId\":\"([^\"]+)\"";
            Pattern pattern = Pattern.compile(patternString);
            // 创建匹配器
            Matcher matcher = pattern.matcher(result);

            // 查找匹配项
            return matcher.find();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public void createdCreds() {
        CloseableHttpResponse httpGetResponse = null;
        JSONObject resultJson = null;
        if (orderKey == null) {
            orderKey = getOrderCookie();
        }
        try {

            // 通过址默认配置创建一个httpClient实例
            // 创建httpGet远程连接实例
            // 设置请求头信息，鉴权
            HttpPost httpPost = new HttpPost("https://public-ubiservices.ubi.com/v3/profiles/sessions");

            httpPost.setHeader("ubi-appid", "80a4a0e8-8797-440f-8f4c-eaba87d0fdda");
            httpPost.setHeader("ubi-sessionid", "b9b7f866-14ba-4680-baab-553d106b9450");
            httpPost.setHeader("ubi-localecode", "zh-TW");
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                    "(KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36 Edg/85.0.564.70");
            httpPost.setHeader("authorization", orderKey);
            String jsonBody = "{\"operationName\":\"GetMarketableItems\",\"variables\":{\"withOwnership\":true,\"spaceId\":\"0d2ae42d-4c27-4cb7-af6c-2099062302bb\",\"limit\":350,\"offset\":0,\"filterBy\":{\"types\":[\"CharacterHeadgear\",\"CharacterUniform\",\"WeaponSkin\",\"WeaponAttachmentSkinSet\"],\"tags\":[[\"rarity_superrare\",\"rarity_legendary\",\"rarity_rare\"]]},\"sortBy\":{\"field\":\"LAST_TRANSACTION_PRICE\",\"direction\":\"DESC\",\"paymentItemId\":\"9ef71262-515b-46e8-b9a8-b6b6ad456c67\"}},\"query\":\"query GetMarketableItems($spaceId: String!, $limit: Int!, $offset: Int, $filterBy: MarketableItemFilter, $withOwnership: Boolean = true, $sortBy: MarketableItemSort) {\\n  game(spaceId: $spaceId) {\\n    id\\n    marketableItems(\\n      limit: $limit\\n      offset: $offset\\n      filterBy: $filterBy\\n      sortBy: $sortBy\\n      withMarketData: true\\n    ) {\\n      nodes {\\n        ...MarketableItemFragment\\n        __typename\\n      }\\n      totalCount\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\\nfragment MarketableItemFragment on MarketableItem {\\n  item {\\n    ...SecondaryStoreItemFragment\\n    ...SecondaryStoreItemOwnershipFragment @include(if: $withOwnership)\\n    __typename\\n  }\\n  marketData {\\n    ...MarketDataFragment\\n    __typename\\n  }\\n}\\n\\nfragment SecondaryStoreItemFragment on SecondaryStoreItem {\\n  itemId\\n  name\\n}\\n\\nfragment SecondaryStoreItemOwnershipFragment on SecondaryStoreItem {\\n  __typename\\n}\\n\\nfragment MarketDataFragment on MarketableItemMarketData {\\n  id\\n  sellStats {\\n    id\\n    paymentItemId\\n    lowestPrice\\n    highestPrice\\n    activeCount\\n  }\\n  buyStats {\\n    id\\n    paymentItemId\\n    lowestPrice\\n    highestPrice\\n    activeCount\\n    __typename\\n  }\\n  lastSoldAt {\\n    id\\n    paymentItemId\\n    price\\n    performedAt\\n    __typename\\n  }\\n  __typename\\n}\\n\"}";
            // 设置请求头，指定内容类型为JSON
            httpPost.setHeader("Content-Type", "application/json");
            StringEntity stringEntityentity = new StringEntity(jsonBody);
            httpPost.setEntity(stringEntityentity);
            // 设置请求头，指定内容类型为JSON
            httpGetResponse = httpClient.execute(httpPost);
            // 从响应对象中获取响应内容
            // 通过返回对象获取返回数据
            HttpEntity entity = httpGetResponse.getEntity();
            // 通过EntityUtils中的toString方法将结果转换为字符串
            String result = EntityUtils.toString(entity);
            Session session = JSONObject.parseObject(result, Session.class);
            if (session != null && session.getTicket() != null) {
                orderKey = "ubi_v1 t=" + session.getTicket();
                expirationTime = session.getExpiration();
            } else {
                LOGGER.error("更新Ticket失败");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getNewSellTradeId() {
        CloseableHttpResponse httpGetResponse = null;
        JSONObject resultJson = null;
        String tradeId = "";
        try {
            HttpPost httpPost = new HttpPost("https://public-ubiservices.ubi.com/v1/profiles/me/uplay/graphql");
            // 设置请求头信息，鉴权
            httpPost.setHeader("ubi-appid", "80a4a0e8-8797-440f-8f4c-eaba87d0fdda");
            httpPost.setHeader("ubi-sessionid", "21137330-b231-4ce6-a183-2cc3a1982929");
            httpPost.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 " +
                    "(KHTML, like Gecko) Chrome/85.0.4183.121 Safari/537.36 Edg/85.0.564.70");
            httpPost.setHeader("authorization", orderKey);
            // 执行get请求得到返回对象
            String jsonBody = "[{\"operationName\":\"GetTransactionsPending\",\"variables\":{\"spaceId\":\"0d2ae42d-4c27-4cb7-af6c-2099062302bb\",\"offset\":0,\"limit\":40},\"query\":\"query GetTransactionsPending($spaceId: String!, $limit: Int!, $offset: Int) {\\n  game(spaceId: $spaceId) {\\n    viewer {\\n      meta {\\n        trades(\\n          limit: $limit\\n          offset: $offset\\n          filterBy: {states: [Created]}\\n          sortBy: {field: LAST_MODIFIED_AT}\\n        ) {\\n          nodes {\\n            ...TradeFragment\\n          }\\n          __typename\\n        }\\n        __typename\\n      }\\n      __typename\\n    }\\n    __typename\\n  }\\n}\\n\\nfragment TradeFragment on Trade {\\n  tradeId\\n  category\\n}\\n\"}]";
            StringEntity entity1 = new StringEntity(jsonBody);
            httpPost.setEntity(entity1);
            // 设置请求头，指定内容类型为JSON
            httpPost.setHeader("Content-Type", "application/json");

            httpGetResponse = httpClient.execute(httpPost);
            // 从响应对象中获取响应内容
            // 通过返回对象获取返回数据
            HttpEntity entity = httpGetResponse.getEntity();
            // 通过EntityUtils中的toString方法将结果转换为字符串
            String result = EntityUtils.toString(entity);
            // 定义正则表达式来匹配 "tradeId" 和 "category"
            // 正则表达式提取第一个 category 为 "Sell" 的 tradeId
            String regex = "tradeId\":\"([^\"]+)\",\"category\":\"Sell";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(result);

            // 查找第一个匹配的 "tradeId"
            if (matcher.find()) {
                if (matcher.find()) {
                    tradeId = matcher.group(1);
                }
            }
            return tradeId;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
