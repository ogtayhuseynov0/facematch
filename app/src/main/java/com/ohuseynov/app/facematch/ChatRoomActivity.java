package com.ohuseynov.app.facematch;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ohuseynov.app.facematch.model.Msg;
import com.ohuseynov.app.facematch.model.Room;
import com.ohuseynov.app.facematch.takers.MsgAdaptor;
import com.ohuseynov.app.facematch.utils.myUtils;

import java.util.ArrayList;
import java.util.List;

public class ChatRoomActivity extends AppCompatActivity {
    private Intent got;
    List<Msg> res;
    MsgAdaptor adaptor;
    RecyclerView rc;
    EditText msg;
    FloatingActionButton send;
    boolean isme=true;
    int max=2;
    myUtils utils;
    String mID,toID;
    DatabaseReference refo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_room);
        got= getIntent();
        utils=new myUtils();
        res= new ArrayList<>();
        mID=utils.getUserMailId();
        toID=got.getStringExtra("mailid");
        Load();
        addData();
        msg.callOnClick();
        rc=(RecyclerView) findViewById(R.id.room_tc);
        rc.setHasFixedSize(true);


        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setOrientation(LinearLayoutManager.VERTICAL);
        manager.setStackFromEnd(true);
        rc.setLayoutManager(manager);
        adaptor = new MsgAdaptor(res,getApplicationContext(),mID,toID);
        rc.setAdapter(adaptor);
        msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (res.size()!=0) {
                    rc.scrollTo(0,res.size()-1);
                }
            }
        });
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(got.getStringExtra("name"));
        actionBar.setElevation(0);

    }

    private void Load() {
        final DatabaseReference refp=FirebaseDatabase.getInstance().getReference("users").child("chats");
        refp.keepSynced(true);
        refp.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Room rom=dataSnapshot.getValue(Room.class);
                if (rom.getTo().equals(mID)&& rom.getFrom().equals(toID)|| rom.getFrom().equals(mID) &&rom.getTo().equals(toID)){
                    loadMsgs(refp,dataSnapshot);
                }
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

    private void loadMsgs(DatabaseReference ref,DataSnapshot dataSnapshot) {
        DatabaseReference ERC= ref.child(dataSnapshot.getKey()).child("msgs");
        ERC.keepSynced(true);
        ERC.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                Msg d=dataSnapshot.getValue(Msg.class);
                res.add(d);
                adaptor.notifyDataSetChanged();
                if (res.size()!=0) {
                    rc.smoothScrollToPosition(res.size() - 1);
                }
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

    private void addData() {
        msg=(EditText) findViewById(R.id.msg);
        send=(FloatingActionButton) findViewById(R.id.send_mm);

        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String msgs=msg.getText().toString().trim();
                if(msgs.equals("") || msgs.isEmpty()){
                    msg.setError("Cann not be empty");
                }else {
                    Msg msgss=new Msg(msgs,mID,toID);
                    sendMessage(msgss);
                    msg.setText("");
                }
            }
        });

    }

    private void sendMessage(final Msg msgss) {
        refo=FirebaseDatabase.getInstance().getReference("users").child("chats");
        refo.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(final DataSnapshot dataSnapshot, String s) {
                Room rom=dataSnapshot.getValue(Room.class);
                if (rom.getTo().equals(mID)&& rom.getFrom().equals(toID)|| rom.getFrom().equals(mID) &&rom.getTo().equals(toID)){
                    refo.child(dataSnapshot.getKey()).child("msgs").push().setValue(msgss);
                }
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId()==android.R.id.home){
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);


    }
}
