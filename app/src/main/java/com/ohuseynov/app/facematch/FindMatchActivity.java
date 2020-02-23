package com.ohuseynov.app.facematch;

import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ohuseynov.app.facematch.model.User;
import com.ohuseynov.app.facematch.takers.FindAdapter;
import com.ohuseynov.app.facematch.utils.myUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FindMatchActivity extends AppCompatActivity {
    FindAdapter adapter;
    RecyclerView rc;
    List<User> reslt;
    List<User> reslt2;
    FirebaseDatabase DB;
    DatabaseReference ref;
    myUtils my;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_match);
        DB= FirebaseDatabase.getInstance();
        my=new myUtils();
        ref= DB.getReference("users").child("users");
        reslt=new ArrayList<>();
        reslt2=new ArrayList<>();

        rc= (RecyclerView) findViewById(R.id.f_match);
        rc.setHasFixedSize(true);

        LinearLayoutManager lm=new LinearLayoutManager(this);
        lm.setOrientation(LinearLayoutManager.VERTICAL);
        rc.setLayoutManager(lm);
        adapter=new FindAdapter(reslt2,getApplicationContext());
        rc.setAdapter(adapter);


        updateList();


        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void updateList() {
        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

                User user=dataSnapshot.getValue(User.class);
                if(!user.getUserEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())) {
                    reslt.add(user);
//                    adapter.notifyDataSetChanged();
                }else{
                    my.setUSer(user);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_xml,menu);
        MenuItem item= (MenuItem) menu.findItem(R.id.fin_match);
        SearchView searchView=(SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(final String query) {
                String aquery=query.toLowerCase();
                ArrayList<User> aa=new ArrayList<User>();
                if (query.equals("q")){
                    Toast.makeText(FindMatchActivity.this,"asasa",Toast.LENGTH_SHORT).show();
                }
                for (User u:reslt ){
                    if (u.getUserName().toLowerCase().contains(aquery)){
                        aa.add(u);
                    }
                }
                adapter.setAdatper(aa);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                String aquery=newText.toLowerCase();
                ArrayList<User> aa=new ArrayList<User>();
                if (aquery.equals("")){
//                    aa.addAll(reslt);
                }else {
                    for (User u : reslt) {
                        if (u.getUserName().toLowerCase().contains(aquery)) {
                            aa.add(u);
                        }
                    }
                }
                adapter.setAdatper(aa);
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

}