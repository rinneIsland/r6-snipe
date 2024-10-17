package com.example.custom.pojo.itemPrice;

import lombok.Data;

/**
 * @author index
 * @version 1.0
 * Create by 2024-05-19 17:29
 */
@Data
public class ItemHisPrice {
        private long averagePrice;
        private DataPoints dataPoints;



        public void setAveragePrice(long averagePrice) {
            this.averagePrice = averagePrice;
        }
        public long getAveragePrice() {
            return averagePrice;
        }

        public void setDataPoints(DataPoints dataPoints) {
            this.dataPoints = dataPoints;
        }
        public DataPoints getDataPoints() {
            return dataPoints;
        }

    }
