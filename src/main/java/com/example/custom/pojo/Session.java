package com.example.custom.pojo;

import java.util.Date;

/**
 * @author index
 * @version 1.0
 * Create by 2024-05-14 12:29
 */
public class Session {
    private String ticket;
    private String expiration;
    public void setTicket(String ticket) {
        this.ticket = ticket;
    }
    public String getTicket() {
        return ticket;
    }

    public void setExpiration(String expiration) {
        this.expiration = expiration;
    }
    public String getExpiration() {
        return expiration;
    }
}
