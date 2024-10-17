/**
  * Copyright 2024 bejson.com 
  */
package com.example.custom.pojo.sell;

import lombok.Data;

/**
 * Auto-generated: 2024-02-23 9:2:41
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class Viewer {

    private Meta meta;
    public void setMeta(Meta meta) {
         this.meta = meta;
     }
     public Meta getMeta() {
         return meta;
     }
}