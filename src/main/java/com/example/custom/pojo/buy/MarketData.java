/**
  * Copyright 2024 bejson.com 
  */
package com.example.custom.pojo.buy;
import com.example.custom.pojo.BuyStats;
import com.example.custom.pojo.LastSoldAt;
import com.example.custom.pojo.SellStats;
import lombok.Data;

import java.util.List;

/**
 * Auto-generated: 2024-02-23 9:2:41
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class MarketData {

    private List<SellStats> sellStats;
    private List<LastSoldAt> lastSoldAt;
    private List<BuyStats> buyStats;

    public void setSellStats(List<SellStats> sellStats) {
         this.sellStats = sellStats;
     }
     public List<SellStats> getSellStats() {
         return sellStats;
     }

    public void setBuyStats(List<BuyStats> buyStats) {
         this.buyStats = buyStats;
     }
     public List<BuyStats> getBuyStats() {
         return buyStats;
     }

    public void setLastSoldAt(List<LastSoldAt> lastSoldAt) {
         this.lastSoldAt = lastSoldAt;
     }
     public List<LastSoldAt> getLastSoldAt() {
         return lastSoldAt;
     }
}