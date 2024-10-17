package com.example.custom.pojo.sellDate;

import lombok.Data;

/**
 * @author index
 * @version 1.0
 * Create by 2024-06-29 20:44
 */
@Data

public class SellDateDetail {
    private SellDateGame game;
    public void setGame(SellDateGame game) {
        this.game = game;
    }
    public SellDateGame getGame() {
        return game;
    }

}
