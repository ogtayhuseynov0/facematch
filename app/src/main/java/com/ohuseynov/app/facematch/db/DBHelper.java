package com.ohuseynov.app.facematch.db;

/**
 * Created by ogtay on 6/11/17.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import com.ohuseynov.app.facematch.model.User;

public class DBHelper extends SQLiteOpenHelper {
    private static final String TABLE="users";
    private static final String CNAME="d_name";
    private static final String CSTATUS="d_status";
    private static final String CEMAIL="d_email";
    private static final String CIMAGE="d_imname";
    Context mContext;

    public DBHelper(Context context) {
        super(context, TABLE, null, 1);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        try {
            /* There you can create tables */
            db.execSQL("CREATE TABLE IF NOT EXISTS "+TABLE+" ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + "'"+CNAME+"' TEXT,"
                    + "'"+CEMAIL+"' TEXT,"
                    + "'"+CSTATUS+"'  TEXT,"
                    + "'"+CIMAGE+"' TEXT);");
        } catch (Exception ex) {
            Toast.makeText(mContext,
                    "Error in DBHelper.onCreate: " + ex.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        try {
            /* There you should drop all created tables
               (fires on DB upgrade and next recreates all back) */
            db.execSQL("DROP TABLE IF EXISTS "+TABLE+";");
        } catch (Exception ex) {
            Toast.makeText(mContext,
                    "Error in DBHelper.onUpgrade: " + ex.getMessage(),
                    Toast.LENGTH_SHORT).show();
        }
    }

    public boolean addUser(User user){
        SQLiteDatabase db=this.getWritableDatabase();
        ContentValues cv= new ContentValues();
        cv.put(CNAME,user.getUserName());
        cv.put(CEMAIL,user.getUserEmail());
        cv.put(CSTATUS,user.getUserStatus());
        cv.put(CIMAGE,user.getPhotoUrl());

        long res=db.insert(TABLE,null,cv);

        return res == 1;
    }

    public Cursor getUser(String email){
        SQLiteDatabase db= this.getWritableDatabase();
        return db.rawQuery("SELECT * FROM "+TABLE+" WHERE "+CEMAIL+"="+email+";",null);
    }

    public void UpdateUser(User user){
        SQLiteDatabase db=this.getWritableDatabase();
        db.execSQL("UPDATE "+TABLE+" SET "+CNAME+" = "+user.getUserName()+" "+CSTATUS+"="+user.getUserStatus()+" "
    +CIMAGE+" = "+user.getPhotoUrl()+" WHERE "+CEMAIL+" = "+user.getUserEmail());

    }
}