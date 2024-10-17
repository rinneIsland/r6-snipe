package com.example.custom.pojo.itemDate;

import lombok.Data;

/**
 * @author index
 * @version 1.0
 * Create by 2024-07-18 10:48
 */
@Data
public class ItemJsonRootBean {
    private ItemData data;
    public void setData(ItemData data) {
        this.data = data;
    }
    public ItemData getData() {
        return data;
    }
}
