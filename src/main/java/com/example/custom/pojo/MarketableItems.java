/**
  * Copyright 2024 bejson.com 
  */
package com.example.custom.pojo;

import lombok.Data;

import java.util.List;

/**
 * Auto-generated: 2024-02-23 9:2:41
 *
 * @author bejson.com (i@bejson.com)
 * @website http://www.bejson.com/java2pojo/
 */
@Data
public class MarketableItems {

    private List<Nodes> nodes;
    private int totalCount;
    private String __typename;
    public void setNodes(List<Nodes> nodes) {
         this.nodes = nodes;
     }
     public List<Nodes> getNodes() {
         return nodes;
     }

    public void setTotalCount(int totalCount) {
         this.totalCount = totalCount;
     }
     public int getTotalCount() {
         return totalCount;
     }

    public void set__typename(String __typename) {
         this.__typename = __typename;
     }
     public String get__typename() {
         return __typename;
     }

}