package com.ohuseynov.app.facematch;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.ohuseynov.app.facematch.utils.FaceOverlayView;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class ImageAnaylyze extends AppCompatActivity {
    private FaceOverlayView mFaceOverlayView;
    private TextView info;
    int PICK_IMAGE=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_anaylyze);

        mFaceOverlayView = (FaceOverlayView) findViewById( R.id.face_overlay );
        info = (TextView) findViewById(R.id.Info);

        InputStream stream = getResources().openRawResource( R.raw.ads);
        Bitmap bitmap = BitmapFactory.decodeStream(stream);

        mFaceOverlayView.setInfov(info);
        mFaceOverlayView.setBitmap(bitmap);

        mFaceOverlayView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setType("image/*");
                intent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
            }
        });
        ActionBar actionBar=getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setElevation(0);
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE && resultCode == Activity.RESULT_OK) {
            if (data == null) {
                //Display an error
                return;
            }

            try {
                InputStream inputStream = getApplicationContext().getContentResolver().openInputStream(data.getData());
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                mFaceOverlayView.setBitmap(bitmap);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }
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
