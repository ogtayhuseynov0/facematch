package com.ohuseynov.app.facematch.model;

import com.google.firebase.database.Exclude;

/**
 * Created by ogtay on 6/15/17.
 */

public class Msg {
   private String from;
   private String to;
    @Exclude
   private String date;
    @Exclude
   private String ago;
    private String msg;

    public Msg(String msg, String from,String to) {
        this.msg = msg;
        this.from=from;
        this.to=to;
    }

    public Msg() {
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
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

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAgo() {
        return ago;
    }

    public void setAgo(String ago) {
        this.ago = ago;
    }
}
