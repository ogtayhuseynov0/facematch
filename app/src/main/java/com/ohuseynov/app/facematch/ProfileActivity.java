package com.ohuseynov.app.facematch;

import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ohuseynov.app.facematch.model.AddF;
import com.ohuseynov.app.facematch.model.ChatUser;
import com.ohuseynov.app.facematch.model.User;
import com.ohuseynov.app.facematch.takers.DownloadImageTask;
import com.ohuseynov.app.facematch.utils.myUtils;

public class ProfileActivity extends AppCompatActivity {
    private FirebaseUser a;
    private ImageView m;
    private FloatingActionButton add,send;
    private FirebaseDatabase db;
    private DatabaseReference ref;
    private myUtils my;
    private FirebaseUser fuser;
    private String mud,fird;
    private RelativeLayout rl;
    Snackbar snackbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.profile_layout);
        INIT();
        LoadUser();
        myListeners();


        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(0);
    }

    private void myListeners() {

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                add.setClickable(false);
                ref.push().setValue(new AddF(mud,fird,false))
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                         snackbar = Snackbar
                                .make(rl, "Message request has been sent!!", Snackbar.LENGTH_INDEFINITE)
                                .setAction("Dismiss", new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        snackbar.dismiss();
                                    }
                                });

                        snackbar.show();
                        add.setImageResource(R.drawable.ic_done);
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        add.setClickable(true);
                        Log.e("profileactvity",e.getMessage());
                    }
                });
            }
        });
    }

    private void INIT() {
        rl=(RelativeLayout) findViewById(R.id.r_l) ;
        my=new myUtils();
        my.getCurrentUser();
        fuser=FirebaseAuth.getInstance().getCurrentUser();
        add=(FloatingActionButton) findViewById(R.id.add_u);
        fird=myUtils.getMIdFromMail(fuser.getEmail());
        mud=myUtils.getMIdFromMail(getIntent().getStringExtra("p_email"));
        db= FirebaseDatabase.getInstance();
        ref=db.getReference("users").child("user_req");

        DatabaseReference ref=db.getReference("users").child("user_req");
        ref.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot data:dataSnapshot.getChildren()) {
                    if (data.child("to").getValue(String.class).equals(mud)){
                        boolean df=data.child("from").getValue(String.class).equals(fird);
                        if(df){
                            add.setImageResource(R.drawable.ic_done);
                            add.setClickable(false);
                            break;
                        }

                    }else if(data.child("to").getValue(String.class).equals(fird)){
                        boolean df2=data.child("from").getValue(String.class).equals(mud);
                        if (df2){
                            add.setImageResource(R.drawable.ic_done);
                            add.setClickable(false);
                            break;
                        }

                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void LoadUser() {
        m=(ImageView) findViewById(R.id.user_profile_photo);
        a= FirebaseAuth.getInstance().getCurrentUser();

        if(a.getPhotoUrl()!=null) {
            Bitmap bm;

            DownloadImageTask dm= new DownloadImageTask((ImageView) findViewById(R.id.user_profile_photo));
            dm.execute(a.getPhotoUrl().toString());
            bm=dm.bm();
            if(bm!=null){
                m.setImageBitmap(bm);
            }
        }else{

        }
        ((TextView) findViewById(R.id.user_profile_name)).setText(getIntent().getStringExtra("p_name"));
        ((TextView) findViewById(R.id.user_profile_short_bio)).setText(getIntent().getStringExtra("p_email"));
        ((TextView) findViewById(R.id.p_status)).setText(getIntent().getStringExtra("p_sts"));

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
