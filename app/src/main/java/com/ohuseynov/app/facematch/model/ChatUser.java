package com.ohuseynov.app.facematch.model;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ohuseynov.app.facematch.utils.myUtils;

import java.io.Serializable;

/**
 * Created by ogtay on 6/13/17.
 */

public class ChatUser{
    private String Dname;
    private String Dinit;
    private String lastMsg;
    private String mailId;
    private myUtils f;

    public ChatUser() {
    }

    public ChatUser(String dname, String mailId) {
        this.Dname = dname;
        setDinitaa();
        this.Dinit=getDinit();
        this.mailId = mailId;
        f=new myUtils();
        LatMsg(f.getUserMailId(),mailId);
    }

    public String getDname() {
        return Dname;
    }

    public void setDname(String dname) {
        Dname = dname;
    }

    public String getLastMsg() {
        return lastMsg;
    }

    public void setLastMsg(String lastMsg) {
        this.lastMsg = lastMsg;
    }

    public String getMailId() {
        return mailId;
    }

    public void setMailId(String mailId) {
        this.mailId = mailId;
    }

    public String getDinit() {
        return Dinit;
    }

    public void setDinitaa() {
        String ini[]=new String[2];
        if(Dname.contains(" ")) {
            ini[0] = String.valueOf(Dname.split(" ")[0].charAt(0));
            ini[1] = String.valueOf(Dname.split(" ")[1].charAt(0));
        }else{
            ini[0]="A";
            ini[1]="A";
        }
        this.Dinit= " "+ini[0].toUpperCase()+ini[1].toUpperCase()+" ";
    }


    public String userToString()
    {
        return "Name: "+ Dname+"\n message: "+lastMsg+"\n  mailid: "+mailId;
    }
    private void LatMsg(final String userName, final String mIdFromMail) {
        Query a=FirebaseDatabase.getInstance().getReference("users").child("chats");
        a.keepSynced(true);
        a.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot ss: dataSnapshot.getChildren()){
                    Room sm=ss.getValue(Room.class);
                    if (sm.getFrom().equals(userName) && sm.getTo().equals(mIdFromMail) || sm.getTo().equals(userName)&& sm.getFrom().equals(mIdFromMail)){
                        Query s= FirebaseDatabase.getInstance().getReference("users").child("chats").child(ss.getKey()).child("msgs").orderByKey()
                                .limitToLast(1);
                        s.keepSynced(true);
                        s.addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                                lastMsg=dataSnapshot.getValue(Msg.class).getMsg();
                            }

                            @Override
                            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onChildRemoved(DataSnapshot dataSnapshot) {

                            }

                            @Override
                            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

}
