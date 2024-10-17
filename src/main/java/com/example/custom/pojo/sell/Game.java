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
public class Game {

    private Viewer viewer;
    public void setViewer(Viewer viewer) {
         this.viewer = viewer;
     }
     public Viewer getViewer() {
         return viewer;
     }
}