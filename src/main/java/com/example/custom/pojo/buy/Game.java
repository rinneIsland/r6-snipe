/**
  * Copyright 2024 bejson.com 
  */
package com.example.custom.pojo.buy;

import com.example.custom.pojo.MarketableItems;
import lombok.Data;

/**
 * Auto-generated: 2024-02-23 9:2:41
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class Game {

    private String id;
    private MarketableItems marketableItems;
    public void setId(String id) {
         this.id = id;
     }
     public String getId() {
         return id;
     }

}