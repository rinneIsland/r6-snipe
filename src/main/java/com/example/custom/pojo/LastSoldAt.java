/**
  * Copyright 2024 bejson.com 
  */
package com.example.custom.pojo;
import lombok.Data;

import java.util.Date;

/**
 * Auto-generated: 2024-02-23 9:2:41
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class LastSoldAt {
    private int price;
    private String performedAt;
    public void setPrice(int price) {
         this.price = price;
     }
     public int getPrice() {
         return price;
     }
     public void setPerformedAt(String performedAt) {
         this.performedAt = performedAt;
     }
     public String getPerformedAt() {
         return performedAt;
     }
}