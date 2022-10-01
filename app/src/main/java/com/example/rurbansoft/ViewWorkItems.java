package com.example.rurbansoft;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

public class ViewWorkItems extends AppCompatActivity {

    private DBHelper myDB;
    LinearLayout ll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_work_items);

        myDB = new DBHelper(this);
        ll=findViewById(R.id.ll);


        List<LatLng> lls = myDB.getLatLng();
        int i=1;

        for (LatLng ll : lls) {
            LatLng coordinate = new LatLng(ll.latitude, ll.longitude);
            Log.e("latlong", coordinate.toString());
            prepareInfoView(String.valueOf(i++));

        }
    }
    private void prepareInfoView(String i){

        LinearLayout infoView = new LinearLayout(ViewWorkItems.this);
        LinearLayout.LayoutParams infoViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        infoView.setOrientation(LinearLayout.HORIZONTAL);
        infoView.setLayoutParams(infoViewParams);

        ImageView infoImageView = new ImageView(ViewWorkItems.this);

        DBHelper myDB = new DBHelper(this);
        String id = i;
        Cursor cursor = myDB.getImage(id);

        //---------------------------//
        @SuppressLint("Range") String state = cursor.getString(cursor.getColumnIndex("STATE"));
        @SuppressLint("Range") String district = cursor.getString(cursor.getColumnIndex("DISTRICT"));
        @SuppressLint("Range") String cluster= cursor.getString(cursor.getColumnIndex("CLUSTER"));
        @SuppressLint("Range") String gp = cursor.getString(cursor.getColumnIndex("GP"));
        @SuppressLint("Range") String components = cursor.getString(cursor.getColumnIndex("COMPONENTS"));
        @SuppressLint("Range") String sub_components = cursor.getString(cursor.getColumnIndex("SUB_COMPONENTS"));
        @SuppressLint("Range") String phase = cursor.getString(cursor.getColumnIndex("PHASE"));
        @SuppressLint("Range") String status = cursor.getString(cursor.getColumnIndex("STATUS"));
        @SuppressLint("Range") byte[] img = cursor.getBlob(cursor.getColumnIndex("IMAGE"));
        @SuppressLint("Range") String TIME  = cursor.getString(cursor.getColumnIndex("DATE_TIME"));
        @SuppressLint("Range") double lat  = cursor.getDouble(cursor.getColumnIndex("LATITUDE"));
        @SuppressLint("Range") double lon  = cursor.getDouble(cursor.getColumnIndex("LONGITUDE"));


        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;

        Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length, bmOptions);

        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        int targetW = photoW / 3; //image to reduce to 1/10 of original
        int targetH = photoH / 3;

        bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
        Matrix matrix = new Matrix();

        matrix.postRotate(90);
        Bitmap photo = Bitmap.createScaledBitmap(bitmap, targetW, targetH, false);
        Bitmap rotatedBitmap = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true);

        infoImageView.setLayoutParams(infoViewParams);
        infoImageView.setImageBitmap(rotatedBitmap);
        infoImageView.setPadding(10, 10, 20, 10);
        infoView.addView(infoImageView);

        LinearLayout subInfoView = new LinearLayout(ViewWorkItems.this);
        LinearLayout.LayoutParams subInfoViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        subInfoView.setOrientation(LinearLayout.VERTICAL);
        subInfoView.setLayoutParams(subInfoViewParams);

        TextView subInfoTime = new TextView(ViewWorkItems.this);
        subInfoTime.setText("TimeStamp: " + TIME);
        TextView subInfoState = new TextView(ViewWorkItems.this);
        subInfoState.setText("State: " + state);
        TextView subInfoDistrict= new TextView(ViewWorkItems.this);
        subInfoDistrict.setText("District: " + district);
        TextView subInfoCluster = new TextView(ViewWorkItems.this);
        subInfoCluster.setText("Cluster: " + cluster);
        TextView subInfoGp = new TextView(ViewWorkItems.this);
        subInfoGp.setText("GP: " + gp);
        TextView subInfoComponents = new TextView(ViewWorkItems.this);
        subInfoComponents.setText("Components: " + components);
        TextView subInfoSubComponents= new TextView(ViewWorkItems.this);
        subInfoSubComponents.setText("Sub Components: " + sub_components);
        TextView subInfoPhase= new TextView(ViewWorkItems.this);
        subInfoPhase.setText("Phase: " + phase);
        TextView subInfoStatus= new TextView(ViewWorkItems.this);
        subInfoStatus.setText("Status: " + status);
        TextView subInfoLat= new TextView(ViewWorkItems.this);
        subInfoLat.setText("Latitude: " + lat);
        TextView subInfoLon= new TextView(ViewWorkItems.this);
        subInfoLon.setText("Longitude: " + lon);

        Button delete =new Button(ViewWorkItems.this);
        delete.setText("DELETE");

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder a_builder = new AlertDialog.Builder(ViewWorkItems.this);
                a_builder.setMessage("Are you sure you want to delete this workItem?")
                        .setTitle("Delete WorkItem")
                        .setCancelable(false)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                myDB.deleteItem(TIME);
                                Toast.makeText(ViewWorkItems.this, "Deleted the Item", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(ViewWorkItems.this, LandingUser.class);
                                startActivity(i);
                            }
                        }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                dialogInterface.cancel();
                            }
                        });
                a_builder.show();

            }
        });


        subInfoView.addView(subInfoTime);
        subInfoView.addView(subInfoState);
        subInfoView.addView(subInfoDistrict);
        subInfoView.addView(subInfoCluster);
        subInfoView.addView(subInfoGp);
        subInfoView.addView(subInfoComponents);
        subInfoView.addView(subInfoSubComponents);
        subInfoView.addView(subInfoPhase);
        subInfoView.addView(subInfoStatus);
        subInfoView.addView(subInfoLat);
        subInfoView.addView(subInfoLon);
        subInfoView.addView(delete);
        infoView.addView(subInfoView);
        infoView.setPadding(0,10,0,40);

        ll.addView(infoView);
    }
}