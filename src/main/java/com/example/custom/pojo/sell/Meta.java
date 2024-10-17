/**
  * Copyright 2024 bejson.com 
  */
package com.example.custom.pojo.sell;


import com.example.custom.pojo.MarketableItems;
import lombok.Data;

/**
 * Auto-generated: 2024-02-23 9:2:41
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class Meta {

    private MarketableItems marketableItems;

    public void setMarketableItems(MarketableItems marketableItems) {
         this.marketableItems = marketableItems;
     }
     public MarketableItems getMarketableItems() {
         return marketableItems;
     }
}