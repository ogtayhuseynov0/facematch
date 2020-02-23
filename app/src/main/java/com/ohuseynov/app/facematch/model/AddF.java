package com.ohuseynov.app.facematch.model;

/**
 * Created by ogtay on 6/16/17.
 */

public class AddF {
    public String to;
    public String from;
    public boolean accepted=false;
    public AddF(){

    }

    public AddF(String to, String from,boolean acc) {
        this.to = to;
        this.from = from;
        this.accepted=acc;
    }
}
