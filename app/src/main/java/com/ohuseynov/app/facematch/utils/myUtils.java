package com.ohuseynov.app.facematch.utils;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ohuseynov.app.facematch.model.ChatUser;
import com.ohuseynov.app.facematch.model.User;

import java.util.List;

/**
 * Created by ogtay on 6/13/17.
 */

public class myUtils {
    private  static FirebaseDatabase db;
    private FirebaseUser user;
    private User currUser;
    private static User urs;
    private static String[] reps={
        ".", "$", "#", "\\[", "\\]", "/","\\[|\\]","/"
    };

    public myUtils() {
        init();
//        getCurrentUser();
    }

    private void init() {
        db=getDatabase();
        user= FirebaseAuth.getInstance().getCurrentUser();
    }

    public String cleanString(String str){
        String asd="";
        for (String rep : reps) {
            asd=str.replaceAll(rep, "");
        }
        return  asd;
    }
    public String getUserMailId(){
        return user.getEmail().replace(".", "").replaceAll("@", "");
    }

    public static String getMIdFromMail(String user){
        return user.replace(".", "").replaceAll("@", "");
    }


    public void getCurrentUser(){

        DatabaseReference mref=db.getReference("users").child("users").child(getMIdFromMail(user.getEmail()));

        mref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                myUtils.this.currUser=dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
    public User getmyUser(){
        return this.currUser;
    }
    public void setUSer(User h){
        this.currUser=h;
    }


    public void addLOcalChat(List<ChatUser> list){
        for (int i=0;i<5;i++){
//            list.add(new ChatUser("Nicat Kamal"+Integer.toString(i),"Hello World",getUserMailId()));
        }
    }

    public static FirebaseDatabase getDatabase() {
        if (db == null) {
            db = FirebaseDatabase.getInstance();
            db.setPersistenceEnabled(true);
        }
        return db;
    }

    public static User getUser(String mailid){

        DatabaseReference rf=db.getReference("users").child("users").child(mailid);
        rf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                urs=dataSnapshot.getValue(User.class);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("asdsa","failed");

            }
        });

        return urs;
    }

    public static User getUrs() {
        return urs;
    }
}
