package com.example.custom.pojo.sellDate;

import lombok.Data;

import java.util.List;

/**
 * @author index
 * @version 1.0
 * Create by 2024-06-29 20:46
 */
@Data

public class SellDateMarketableItem {

    private List<PriceHistory> priceHistory;

    public void setPriceHistory(List<PriceHistory> priceHistory) {
        this.priceHistory = priceHistory;
    }
    public List<PriceHistory> getPriceHistory() {
        return priceHistory;
    }


}
