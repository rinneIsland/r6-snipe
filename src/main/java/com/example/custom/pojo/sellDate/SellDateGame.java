package com.example.custom.pojo.sellDate;

import lombok.Data;

/**
 * @author index
 * @version 1.0
 * Create by 2024-06-29 20:45
 */
@Data

public class SellDateGame {
    private String id;
    private SellDateMarketableItem marketableItem;

    public void setId(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }

    public void setMarketableItem(SellDateMarketableItem marketableItem) {
        this.marketableItem = marketableItem;
    }
    public SellDateMarketableItem getMarketableItem() {
        return marketableItem;
    }


}
