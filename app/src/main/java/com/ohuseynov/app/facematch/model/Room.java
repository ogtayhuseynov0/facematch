package com.ohuseynov.app.facematch.model;

/**
 * Created by ogtay on 6/19/17.
 */

public class Room {
    private String from;
    private String to;

    public Room() {
    }

    public Room(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }
}

