package com.example.custom.pojo;

import lombok.Builder;
import lombok.Data;

/**
 * @author index
 * @version 1.0
 * Create by 2024/3/23 10:58
 */
@Data
@Builder
public class PriceEntry {
    String item;
    String saved_at;
    public int price;
    public PriceEntry(String item, String saved_at, int price) {
        // 默认构造函数的实现
        this.item = item;
        this.saved_at = saved_at;
        this.price = price;
    }
}
