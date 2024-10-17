package com.example.custom.pojo.itemDate;

import lombok.Data;

/**
 * @author index
 * @version 1.0
 * Create by 2024-07-18 10:49
 */
@Data
public class ItemGame {

    private MarketableItem marketableItem;


    public void setMarketableItem(MarketableItem marketableItem) {
        this.marketableItem = marketableItem;
    }
    public MarketableItem getMarketableItem() {
        return marketableItem;
    }

}
