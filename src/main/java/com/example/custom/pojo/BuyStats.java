/**
  * Copyright 2024 bejson.com 
  */
package com.example.custom.pojo;

import lombok.Data;

/**
 * Auto-generated: 2024-02-23 9:2:41
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class BuyStats {

    private int highestPrice;
    private int activeCount;
    public void setHighestPrice(int highestPrice) {
         this.highestPrice = highestPrice;
     }
     public int getHighestPrice() {
         return highestPrice;
     }

    public void setActiveCount(int activeCount) {
         this.activeCount = activeCount;
     }
     public int getActiveCount() {
         return activeCount;
     }

}