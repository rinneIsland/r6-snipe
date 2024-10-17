/**
  * Copyright 2024 bejson.com 
  */
package com.example.custom.pojo.sell;

import lombok.Data;

import java.util.List;

/**
 * Auto-generated: 2024-02-23 9:2:41
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class Item {


    private String itemId;
    private String name;
    private List<String> tags;
    public void setItemId(String itemId) {
         this.itemId = itemId;
     }
     public String getItemId() {
         return itemId;
     }
    public void setName(String name) {
         this.name = name;
     }
     public String getName() {
         return name;
     }
    public void setTags(List<String> tags) {
        this.tags = tags;
    }
    public List<String> getTags() {
        return tags;
    }

}