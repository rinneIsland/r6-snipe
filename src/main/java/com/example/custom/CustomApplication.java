package com.example.custom;

import com.example.custom.schedule.ScheduledService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;
@EnableScheduling
@SpringBootApplication

public class CustomApplication {
    public static final Logger LOGGER = LoggerFactory.getLogger(CustomApplication.class);

    public static void main(String[] args) {
        LOGGER.info("版本1.0");
        SpringApplication.run(CustomApplication.class, args);
    }

}
