package com.ohuseynov.app.facematch;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ohuseynov.app.facematch.model.User;
import com.ohuseynov.app.facematch.utils.FaceOverlayView;
import com.ohuseynov.app.facematch.utils.myUtils;
import com.squareup.picasso.Picasso;

import java.io.InputStream;

public class MyProfile extends AppCompatActivity {

    private ImageView mpimage;
    private EditText mpStatus;
    private User user;
    private myUtils utils;
    private FirebaseUser us;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_profile);
        Initt();
        listener();


        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(0);
    }
//------------------end on Create
    private void listener() {
        mpStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(
                        new ContextThemeWrapper(MyProfile.this, R.style.AppTheme));
                builder.setTitle("Enter a Status");

                final EditText input = new EditText(
                        new ContextThemeWrapper(MyProfile.this, R.style.FullWidthEditText)
                );
                input.setHint("Enter a Satatus");
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (!TextUtils.isEmpty(input.getText().toString())){
                            mpStatus.setText( input.getText().toString());
                        }
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                final AlertDialog dg=builder.create();
                dg.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        Button btt= dg.getButton(DialogInterface.BUTTON_POSITIVE);
                        Button bt2t= dg.getButton(DialogInterface.BUTTON_NEGATIVE);

                        btt.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        btt.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        bt2t.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                        bt2t.setTextColor(getResources().getColor(R.color.colorPrimary));

                    }
                });
                dg.show();
            }
        });

        mpimage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent nf=new Intent(MyProfile.this,ImageAnaylyze.class);
                startActivity(nf);
            }
        });
    }

    private void Setters() {
        if (!TextUtils.isEmpty(user.getPhotoUrl())){
            Picasso.with(getApplicationContext()).load(user.getPhotoUrl()).into(mpimage);
        }

        mpStatus.setText(user.getUserStatus());
    }

    private void Initt() {
        mpimage=(ImageView) findViewById(R.id.mpImage);
        mpStatus=(EditText) findViewById(R.id.mpStatus);
        utils=new myUtils();
        us= FirebaseAuth.getInstance().getCurrentUser();
        tapUser();

    }

    private void tapUser() {
        FirebaseDatabase.getInstance().getReference("users").child("users")
                .child(myUtils.getMIdFromMail(us.getEmail())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                user=dataSnapshot.getValue(User.class);
                Setters();
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
