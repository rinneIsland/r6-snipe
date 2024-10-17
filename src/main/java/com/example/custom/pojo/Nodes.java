/**
 * Copyright 2024 bejson.com
 */
package com.example.custom.pojo;

import com.example.custom.pojo.buy.Item;
import com.example.custom.pojo.buy.MarketData;
import lombok.Builder;
import lombok.Data;

/**
 * Auto-generated: 2024-02-23 9:2:41
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
@Builder
public class Nodes {

    private MarketData marketData;

    public Nodes(MarketData marketData, Item item) {
        this.marketData = marketData;
        this.item = item;
    }

    private Item item;

    public Nodes() {
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    public void setMarketData(MarketData marketData) {
        this.marketData = marketData;
    }

    public MarketData getMarketData() {
        return marketData;
    }


}