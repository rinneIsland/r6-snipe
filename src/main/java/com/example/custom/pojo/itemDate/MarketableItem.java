package com.example.custom.pojo.itemDate;

import com.example.custom.pojo.buy.MarketData;
import lombok.Data;

/**
 * @author index
 * @version 1.0
 * Create by 2024-07-18 10:50
 */
@Data
public class MarketableItem {
    private MarketData marketData;
    public void setMarketData(MarketData marketData) {
        this.marketData = marketData;
    }
    public MarketData getMarketData() {
        return marketData;
    }


}
