package com.example.custom.pojo.sellDate;

import lombok.Data;

/**
 * @author index
 * @version 1.0
 * Create by 2024-06-29 20:46
 */
@Data

public class PriceHistory {

    private int averagePrice;
    private int highestPrice;
    private int itemsCount;

    public void setAveragePrice(int averagePrice) {
        this.averagePrice = averagePrice;
    }
    public int getAveragePrice() {
        return averagePrice;
    }


    public void setHighestPrice(int highestPrice) {
        this.highestPrice = highestPrice;
    }
    public int getHighestPrice() {
        return highestPrice;
    }

    public void setItemsCount(int itemsCount) {
        this.itemsCount = itemsCount;
    }
    public int getItemsCount() {
        return itemsCount;
    }
}
