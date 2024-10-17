package com.example.custom.pojo.itemPrice;

import lombok.Data;

/**
 * @author index
 * @version 1.0
 * Create by 2024-05-19 17:31
 */
@Data
public class Hour {
    private long lowestSale;
    private long highestSale;
    private long avgPrice;

    public void setLowestSale(long lowestSale) {
        this.lowestSale = lowestSale;
    }

    public long getLowestSale() {
        return lowestSale;
    }

    public void setHighestSale(long highestSale) {
        this.highestSale = highestSale;
    }

    public long getHighestSale() {
        return highestSale;
    }

    public void setAvgPrice(long avgPrice) {
        this.avgPrice = avgPrice;
    }

    public long getAvgPrice() {
        return avgPrice;
    }

}
