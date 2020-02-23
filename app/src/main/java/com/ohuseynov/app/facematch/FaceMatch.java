package com.ohuseynov.app.facematch;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.hardware.camera2.params.Face;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringDef;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ohuseynov.app.facematch.model.AddF;
import com.ohuseynov.app.facematch.model.ChatUser;
import com.ohuseynov.app.facematch.model.Msg;
import com.ohuseynov.app.facematch.model.Room;
import com.ohuseynov.app.facematch.model.User;
import com.ohuseynov.app.facematch.takers.ChatAdapter;
import com.ohuseynov.app.facematch.takers.DownloadImageTask;
import com.ohuseynov.app.facematch.takers.ImageSaver;
import com.ohuseynov.app.facematch.takers.RequestAdapter;
import com.ohuseynov.app.facematch.utils.myUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ExecutionException;

public class FaceMatch extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    TextView name, email, testV;
    ImageView image;
    NavigationView navigationView;
    FloatingActionButton fab;
    public GoogleApiClient mGoogleApiClient;
    TabLayout tabl;
    LinearLayout lr1, lr2;
    FirebaseUser user;
    myUtils myU;
    List<ChatUser> users;
    ChatAdapter adapter;
    RecyclerView rc, req_m;
    FirebaseDatabase db;
    DatabaseReference ref;
    RequestAdapter radapter;
    List<User> reqs;
    ProgressDialog pd;
    String last="",UserMailID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_match);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        mInit();
        google();
        LoadUserInf();
        CheckandAddUser();
        chatLoad();
        reqLoad();
        keepChatUpdated();


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i= new Intent(FaceMatch.this,FindMatchActivity.class);
                startActivity(i);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
    }

    private void keepChatUpdated() {
        final DatabaseReference ref = db.getReference("users").child("chats");
        ref.keepSynced(true);
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Log.e("asdas",dataSnapshot.getKey());
                Room r = dataSnapshot.getValue(Room.class);
                swapChatUsers(r);
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

    private void swapChatUsers(final Room r) {
        String chagID="";
        if (r.getFrom().equals(UserMailID)){
            chagID=r.getTo();
        }else
            chagID=r.getFrom();
        int toind=0;
        int from =-1;
        ChatUser a=null;
        Iterator<ChatUser> it=users.iterator();
        while (it.hasNext()){
            ChatUser us=it.next();
            if (us.getMailId().equals(chagID)){
                from=users.indexOf(us);
                a=us;
            }
        }
        while (users.indexOf(a)!=toind){
            int i = users.indexOf(a);
            Collections.swap(users, i, i - 1);
            Log.e("face33",i+""+" "+(i-1)+" "+a.getDname());
            adapter.notifyDataSetChanged();
        }
    }

    private void reqLoad() {
        reqs = new ArrayList<>();
        addReqs();
        req_m.setHasFixedSize(true);
        LinearLayoutManager lm2 = new LinearLayoutManager(this);
        lm2.setOrientation(LinearLayoutManager.VERTICAL);
        req_m.setLayoutManager(lm2);
        radapter = new RequestAdapter(reqs, getApplicationContext());
        req_m.setAdapter(radapter);
    }

    private void chatLoad() {
        users = new ArrayList<>();
        rc.setHasFixedSize(true);
        LinearLayoutManager lm = new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        rc.setLayoutManager(lm);
        adapter = new ChatAdapter(users, getApplicationContext());
        rc.setAdapter(adapter);

    }

    private void addReqs() {
        Query RF = db.getReference("users").child("user_req").orderByKey().limitToLast(100);
        RF.keepSynced(true);
        RF.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                AddF add = dataSnapshot.getValue(AddF.class);
                if (add.to.equals(UserMailID) && !add.accepted) {
                    addReqUser(add.from);
                } else if (add.to.equals(UserMailID) && add.accepted) {
                    addChatUser(add.from);
                } else if (add.from.equals(UserMailID) && add.accepted) {
                    addChatUser(add.to);
                }

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                AddF d = dataSnapshot.getValue(AddF.class);
                if (d.accepted) {
                    if (d.to.equals(UserMailID)) {
                        addChatUser(d.from);
                    } else if (d.from.equals(UserMailID)) {
                        addChatUser(d.to);
                    }
                    RemoeU(d.from);
                } else {
                    addReqUser(d.from);
                    if (d.to.equals(UserMailID)) {
                        removeChatUser(d.from);
                    } else if (d.from.equals(UserMailID)) {
                        removeChatUser(d.to);
                    }
                }
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                AddF d = dataSnapshot.getValue(AddF.class);
                if (d.to.equals(UserMailID)) {
                    removeChatUser(d.from);
                } else if (d.from.equals(UserMailID)) {
                    removeChatUser(d.to);
                }
                RemoeU(d.from);
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void removeChatUser(String from) {
        Iterator<ChatUser> it = users.iterator();
        while (it.hasNext()) {
            ChatUser us = it.next();
            if (us.getMailId().equals(from)) {
                adapter.notifyItemRemoved(users.indexOf(us));
                it.remove();//TODO: BAX
            }
        }
    }

    private void addChatUser(String from) {
        DatabaseReference rf = db.getReference("users").child("users")
                .child(from);
        rf.keepSynced(true);
        rf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User u = dataSnapshot.getValue(User.class);
                users.add(new ChatUser(u.getUserName(), myUtils.getMIdFromMail(u.getUserEmail())));
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("asdsa", "failed");

            }
        });
    }

    private void addReqUser(String from) {
        DatabaseReference rf = db.getReference("users").child("users")
                .child(from);
        rf.keepSynced(true);
        rf.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                reqs.add(dataSnapshot.getValue(User.class));
                radapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("asdsa", "failed");

            }
        });
    }


    private void CheckandAddUser() {
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference myRef = database.getReference("users").child("users");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(UserMailID)) {

                } else {
                    User myUser = new User(user.getDisplayName(), user.getEmail(), "", "");
                    myRef.child(UserMailID).setValue(myUser);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    private void LoadUserInf() {
        ImageSaver ims = new ImageSaver(getApplicationContext());
        name.setText(user.getDisplayName());
        email.setText(user.getEmail());
        if (user.getPhotoUrl() != null) {
            Bitmap bm1 = ims.setDirectoryName("images").setFileName(user.getPhotoUrl().getLastPathSegment()).load();
            if (bm1 == null) {
                try {
//                    save image
                    Bitmap bm = new DownloadImageTask(image).execute(user.getPhotoUrl().toString()).get();
                    ims.setDirectoryName("images").setFileName(user.getPhotoUrl().getLastPathSegment()).save(bm);
                    image.setImageBitmap(bm);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } catch (ExecutionException e) {
                    e.printStackTrace();
                }
            } else {
                image.setImageBitmap(bm1);
            }
        }


    }

    private void google() {
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("480791607419-lf4etfamp5avnjrq86md4gul8hgv73e7.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        Toast.makeText(getApplicationContext(), "Connection Lost", Toast.LENGTH_LONG).show();
                    }
                })
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

    }

    private void mInit() {
        myU = new myUtils();
        UserMailID=myU.getUserMailId();
        db = myUtils.getDatabase();
        ref = db.getReference("users").child("users").child(UserMailID).child("chat");
        user = FirebaseAuth.getInstance().getCurrentUser();
        View hed = navigationView.getHeaderView(0);
        name = (TextView) hed.findViewById(R.id.uName);
        email = (TextView) hed.findViewById(R.id.uEmail);
        image = (ImageView) hed.findViewById(R.id.uImage);
        fab = (FloatingActionButton) findViewById(R.id.fab);
        tabl = (TabLayout) findViewById(R.id.tabl);
        lr1 = (LinearLayout) findViewById(R.id.tab1l);
        lr2 = (LinearLayout) findViewById(R.id.tab2l);

        rc = (RecyclerView) findViewById(R.id.rvMain);
        req_m = (RecyclerView) findViewById(R.id.r_list);

        tabl.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab.getPosition() == 0) {
                    lr1.setVisibility(View.VISIBLE);
                    lr2.setVisibility(View.GONE);
                } else if (tab.getPosition() == 1) {
                    lr1.setVisibility(View.GONE);
                    lr2.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.face_match, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        int settings_id = R.id.action_settings;
        //noinspection SimplifiableIfStatement
        if (id == settings_id) {

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            myU.getCurrentUser();
            User us=myU.getmyUser();
            Intent gall = new Intent(FaceMatch.this,MyProfile.class);

//            TODO: user Obyecti yaratib onu myProfile otur
            startActivity(gall);

        } else if (id == R.id.log_Out) {
            Back();
        } else if (id == R.id.find_match) {
            Intent find = new Intent(FaceMatch.this, FindMatchActivity.class);
            startActivity(find);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void mToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    private void Back() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(Status status) {
                        if (status.isSuccess()) {
                            FirebaseAuth.getInstance().signOut();
                            Intent back = new Intent(FaceMatch.this, MainActivity.class);
                            startActivity(back);
                        }
                    }
                });
    }


    public void RemoeU(String from) {
        for (Iterator<User> iter = reqs.iterator(); iter.hasNext(); ) {
            User uu = iter.next();
            if (myUtils.getMIdFromMail(uu.getUserEmail()).equals(from)) {
                radapter.notifyItemRemoved(reqs.indexOf(iter));
                iter.remove();
            }
        }
    }

}
