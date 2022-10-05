package com.example.rurbansoft;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;
import java.util.List;

public class DBHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "RurbanSoftdb.db";
    private static final int DB_VERSION = 1;
    private static final String TABLE_User = "login";
    public static final String TABLE_WorkItem = "WorkItem";

    public static final String COL_1 = "ID";
    public static final String COL_2 = "STATE";
    public static final String COL_3 = "DISTRICT";
    public static final String COL_4 = "CLUSTER";
    public static final String COL_5 = "GP";
    public static final String COL_6 = "COMPONENTS";
    public static final String COL_7 = "SUB_COMPONENTS";
    public static final String COL_8 = "PHASE";
    public static final String COL_9 = "LATITUDE";
    public static final String COL_10 = "LONGITUDE";
    public static final String COL_12 = "STATUS";
    public static final String COL_13 = "DATE_TIME";
    public static final String COL_14 = "IMAGE";
    public static final String COL_15 = "SYNC";//boolean --> 0 for not synced and 1 for synced to server...

    public static final String col_1 = "phNo";
    public static final String col_2 = "password";
    public static final String col_3 = "Name";
    public static final String col_4 = "Desig";
    public static final String col_5 = "Email";
    public static final String col_6 = "Sync";



    public DBHelper(Context context) {
        super(context,DB_NAME, null, DB_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + TABLE_WorkItem + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, STATE TEXT, DISTRICT TEXT, CLUSTER TEXT,  GP TEXT," +
                " COMPONENTS TEXT, SUB_COMPONENTS TEXT, STATUS TEXT, PHASE TEXT,LATITUDE DOUBLE," +
                " LONGITUDE DOUBLE, IMAGE blob, DATE_TIME TEXT, SYNC INTEGER)");

        db.execSQL("create table " + TABLE_User + "(id INTEGER PRIMARY KEY AUTOINCREMENT, Name TEXT, Desig TEXT, phNo TEXT, email TEXT," +
                " password TEXT, Sync INTEGER)");

    }
    //function is called when register button is pressed
    public boolean RegisterUser(String Name , String Desig, String phNo, String email, String password, int sync){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(col_3, Name);
        contentValues.put(col_4, Desig);
        contentValues.put(col_1, phNo);
        contentValues.put(col_5, email);
        contentValues.put(col_2, password);
        contentValues.put(col_6, sync);
        long result = db.insert(TABLE_User, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;

    }

    //function called when login/submit button is pressed
    public boolean checkLogin(String phno, String pass){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =db.rawQuery("SELECT id FROM "+ TABLE_User +" where phNo = "+ phno +" AND password = '"+ pass +"'",null);
        int CursorCount = cursor.getCount();
        cursor.close();
        db.close();
        if(CursorCount > 0)
            return true;
        else
            return false;
    }

    public Cursor getUserDetails(String phno, String pass){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor =db.rawQuery("SELECT * FROM "+ TABLE_User +" where phNo = "+ phno +" AND password = '"+ pass +"'",null);
        if(cursor.moveToFirst())
        {
            return cursor;
        }
        else
            Log.d("TAG", "getImage: Error in function");
        return null;
    }

    //function for inserting the details of the workItem
    public boolean insertData(String State, String District, String Cluster, String gp, String Component, String SubComponent, String Phase, double lat, double lon, String status , String data_time, byte[] image, int sync)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2, State);
        contentValues.put(COL_3, District);
        contentValues.put(COL_4, Cluster);
        contentValues.put(COL_5, gp);
        contentValues.put(COL_6, Component);
        contentValues.put(COL_7, SubComponent);
        contentValues.put(COL_8, Phase);
        contentValues.put(COL_9, lat);
        contentValues.put(COL_10, lon);
        contentValues.put(COL_12, status);
        contentValues.put(COL_13, data_time);
        contentValues.put(COL_14, image);
        contentValues.put(COL_15, sync);

        long result = db.insert(TABLE_WorkItem, null, contentValues);
        if (result == -1)
            return false;
        else
            return true;
    }
    public void updateSyncStatus(String id, int sync)
    {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("UPDATE "+ TABLE_WorkItem+" SET "+COL_15+"="+sync +" WHERE "+COL_1+" = "+id);
    }
    public List<LatLng> getLatLng() {
        SQLiteDatabase db = this.getReadableDatabase();
        List<LatLng>  latLngList = new ArrayList<LatLng>();
        String value = "0.0";
        String selectQuery = "SELECT  * FROM " + TABLE_WorkItem +" where LATITUDE != '"+ value +"'" ;
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst())
        {
            do
            {
                double x,y;
                x = Double.parseDouble(cursor.getString(9));
                y = Double.parseDouble(cursor.getString(10));
                LatLng ll= new LatLng(x,y);
                // Adding contact to list
                latLngList.add(ll);
            }while(cursor.moveToNext());

        }
        return latLngList;
    }

    //getData is basically a getImage function
    public Cursor getImage(String id){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM "+TABLE_WorkItem+" WHERE "+COL_1+" = "+id, null);
        if(cursor.moveToFirst())
        {
            return cursor;
        }
        else
            Log.d("TAG", "getImage: Error in getImage function");
        return null;
    }

    public void deleteItem(String timeStamp) {

        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_WorkItem, "DATE_TIME=?", new String[]{timeStamp});
        String tempTable="tempTable";
        db.execSQL("create table " + tempTable + "(ID INTEGER PRIMARY KEY AUTOINCREMENT, STATE TEXT, DISTRICT TEXT, CLUSTER TEXT,  GP TEXT," +
                " COMPONENTS TEXT, SUB_COMPONENTS TEXT, STATUS TEXT, PHASE TEXT,LATITUDE DOUBLE," +
                " LONGITUDE DOUBLE, IMAGE blob, DATE_TIME TEXT, SYNC INTEGER)");

        db.execSQL("INSERT INTO "+ tempTable+ "(STATE, DISTRICT,CLUSTER, GP, COMPONENTS, SUB_COMPONENTS, STATUS, PHASE, LATITUDE, LONGITUDE, IMAGE, DATE_TIME,SYNC ) " +
                "SELECT STATE, DISTRICT, CLUSTER, GP, COMPONENTS, SUB_COMPONENTS, STATUS, PHASE, LATITUDE, LONGITUDE, IMAGE, DATE_TIME, SYNC FROM "+ TABLE_WorkItem);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WorkItem);
        db.execSQL("ALTER TABLE " + tempTable+"  RENAME TO "+ TABLE_WorkItem);

        db.close();
    }

    public ArrayList<AllUsers> readUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM " + TABLE_User, null);
        ArrayList<AllUsers> userArrayList = new ArrayList<>();

        if (cursor.moveToFirst()) {
            do {
                userArrayList.add(new AllUsers(cursor.getString(1),cursor.getString(2),cursor.getString(3),cursor.getString(4),cursor.getInt(6)));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return userArrayList;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        // this method is called to check if the table exists already.
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_User);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_WorkItem);
        onCreate(db);

    }
}
