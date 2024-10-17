package com.example.custom.pojo.itemDate;

import com.example.custom.pojo.buy.Game;
import lombok.Data;

/**
 * @author index
 * @version 1.0
 * Create by 2024-07-18 10:49
 */
@Data
public class ItemData {

    private ItemGame game;
    public void setGame(ItemGame game) {
        this.game = game;
    }
    public ItemGame getGame() {
        return game;
    }
}
