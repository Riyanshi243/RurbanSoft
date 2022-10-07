package com.example.rurbansoft;

import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    String DB_PATH = null;
    private static String DB_NAME = "mData";
    private SQLiteDatabase myDataBase;
    private final Context myContext;
    public static final String TABLE_cluster_master = "cluster_master";
    public static final String TABLE_component_master = "component_master";

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, 1);
        this.myContext = context;
        this.DB_PATH = "/data/data/" + context.getPackageName() + "/" + "databases/";
        Log.e("Path 1", DB_PATH);
    }


   // get Data DB query
    public List<String> getData(int id, String str){
        String selectQuery = null;
        List<String> name = new ArrayList<String>();

        switch(id) {
            case 1:// Select State Query
                selectQuery = "SELECT  Distinct state_name FROM " + TABLE_cluster_master +" ORDER BY state_name";
                break;
            case 2://select district query
                selectQuery = "SELECT DISTINCT district_name FROM " + TABLE_cluster_master +" WHERE state_name = '"+ str +"' ORDER BY district_name";
                break;
            case 3:
                selectQuery = "SELECT DISTINCT cluster_name FROM " + TABLE_cluster_master +" WHERE district_name = '"+ str +"' ORDER BY cluster_name";
                break;
            case 4:
                selectQuery = "SELECT DISTINCT gp_name FROM " + TABLE_cluster_master +" WHERE cluster_name = '"+ str +"' ORDER BY gp_name";
                break;
            case 5:
                selectQuery = "SELECT DISTINCT component_name FROM " + TABLE_component_master +"  ORDER BY component_name";
                break;
            case 6:
                selectQuery = "SELECT DISTINCT sub_component_name FROM " + TABLE_component_master +" WHERE component_name= '"+ str +"' ORDER BY sub_component_name";
                break;
            default:
                Toast.makeText(myContext, "Try again later!!", Toast.LENGTH_SHORT).show();
        }
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        // looping through all rows and adding to list
        if (cursor.moveToFirst()) {
            do {
                name.add(cursor.getString(0));
            } while (cursor.moveToNext());
        }

        cursor.close();
        db.close();
        return name;
    }


    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        if (dbExist) {
        } else {
            this.getReadableDatabase();
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = DB_PATH + DB_NAME;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    private void copyDataBase() throws IOException {
        InputStream myInput = myContext.getAssets().open(DB_NAME);
        String outFileName = DB_PATH + DB_NAME;
        OutputStream myOutput = new FileOutputStream(outFileName);
        byte[] buffer = new byte[10];
        int length;
        while ((length = myInput.read(buffer)) > 0) {
            myOutput.write(buffer, 0, length);
        }
        myOutput.flush();
        myOutput.close();
        myInput.close();

    }

    public void openDataBase() throws SQLException {
        String myPath = DB_PATH + DB_NAME;
        myDataBase = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);

    }

    @Override
    public synchronized void close() {
        if (myDataBase != null)
            myDataBase.close();
        super.close();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion)
            try {
                copyDataBase();
            } catch (IOException e) {
                e.printStackTrace();

            }
    }

}
