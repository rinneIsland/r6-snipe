package com.example.custom.pojo.itemPrice;

import lombok.Data;

import java.util.List;

/**
 * @author index
 * @version 1.0
 * Create by 2024-05-19 17:31
 */
@Data
public class DataPoints {
        private List<Hour> hour;
        private List<Day> day;
        public void setHour(List<Hour> hour) {
            this.hour = hour;
        }
        public List<Hour> getHour() {
            return hour;
        }

        public void setDay(List<Day> day) {
            this.day = day;
        }
        public List<Day> getDay() {
            return day;
        }
 }
