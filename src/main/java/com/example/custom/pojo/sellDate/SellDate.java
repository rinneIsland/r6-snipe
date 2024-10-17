package com.example.custom.pojo.sellDate;

import lombok.Data;

import java.util.List;

/**
 * @author index
 * @version 1.0
 * Create by 2024-06-29 20:43
 */
@Data

public class SellDate {

    private List<SellDateDetail> data;
    public void setData(List<SellDateDetail> data) {
        this.data = data;
    }
    public List<SellDateDetail> getData() {
        return data;
    }
}
