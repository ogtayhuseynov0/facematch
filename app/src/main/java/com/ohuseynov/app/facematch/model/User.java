package com.ohuseynov.app.facematch.model;

import java.util.Map;

/**
 * Created by ogtay on 6/7/17.
 */

public class User {
    public String userName;
    public String userEmail;
    public String initials;
    public String userStatus;
    public String photoUrl;

    public User() {
    }

    public User(String u_name, String u_email, String status, String url) {
        this.userName = u_name;
        this.userEmail = u_email;
        setInitials();
        this.initials = getInitials();
        if (status.equals(""))
            this.userStatus = defStatus();
        else
            this.userStatus = status;
        this.photoUrl = url;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getInitials() {
        return initials;
    }

    public void setInitials() {
        String ini[] = new String[2];
        ini[0] = String.valueOf(userName.split(" ")[0].charAt(0));
        ini[1] = String.valueOf(userName.split(" ")[1].charAt(0));
        this.initials = " " + ini[0].toUpperCase() + ini[1].toUpperCase() + " ";
    }

    public String getUserStatus() {
        return userStatus;
    }

    public void setUserStatus(String userStatus) {
        this.userStatus = userStatus;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    public String defStatus() {
        return "I am available!";
    }
    public String tomyStringI(){
        return getUserName()+" "+getUserEmail()+" ";
    }
}
