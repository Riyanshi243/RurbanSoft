package com.example.rurbansoft;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewWorkItems extends AppCompatActivity {

    private DBHelper myDB;
    LinearLayout ll;
    String Name="", phno="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_work_items);


//        Name = getIntent().getExtras().getString("Name");
//        phno = getIntent().getExtras().getString("Phno");

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
        @SuppressLint("Range") int sync_val  = cursor.getInt(cursor.getColumnIndex("SYNC"));

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

        LinearLayout subIcons = new LinearLayout(ViewWorkItems.this);
        LinearLayout.LayoutParams subIconsParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        subIcons.setOrientation(LinearLayout.HORIZONTAL);
        subIcons.setLayoutParams(subIconsParams);

        Button delete =new Button(ViewWorkItems.this);
        Drawable deleteIcon= getResources().getDrawable(R.drawable.roundedbutton);
        delete.setBackground(deleteIcon);
        delete.setText("DELETE");

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder a_builder = new AlertDialog.Builder(ViewWorkItems.this);
                a_builder.setMessage("Are you sure you want to delete this workItem? WorkItem will be deleted from the local database only..")
                        .setTitle("Delete WorkItem")
                        .setCancelable(false)
                        .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                myDB.deleteItem(TIME);
                                Toast.makeText(ViewWorkItems.this, "Deleted the Item", Toast.LENGTH_SHORT).show();
                                Intent i = new Intent(ViewWorkItems.this, ViewWorkItems.class);
                                ViewWorkItems.this.finish();
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


        String latitude=String.valueOf(lat);
        String longitude=String.valueOf(lon);

        ByteArrayOutputStream mBOS = new ByteArrayOutputStream();
        rotatedBitmap.compress(Bitmap.CompressFormat.PNG,10, mBOS);
        byte[] mBytImg = mBOS.toByteArray();

        String mStrImg = Base64.encodeToString(mBytImg, Base64.DEFAULT);

        Button sync =new Button(ViewWorkItems.this);
        Drawable syncIcon= getResources().getDrawable(R.drawable.roundedbutton);
        sync.setBackground(syncIcon);
        if(sync_val==1)
        {
            sync.setText("SYNCED");
            sync.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    AlertDialog.Builder a_builder = new AlertDialog.Builder(ViewWorkItems.this);
                    a_builder.setMessage("This workItem is already synced on server!!")
                            .setTitle("Sync Status!")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    dialogInterface.cancel();
                                }
                            });
                    a_builder.show();
                }
            });
        }
        else
        {
            sync.setText("SYNC");
            sync.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final ProgressDialog progressDialog = new ProgressDialog(ViewWorkItems.this);
                    progressDialog.setMessage("Saving WorkItem...");
                    progressDialog.show();
                    HttpsTrustManager.allowAllSSL();
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerConnect.SERVER_URL_WorkItem,
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {
                                    progressDialog.dismiss();
                                    try {
                                        JSONObject obj = new JSONObject(response);
                                        if (!obj.getBoolean("error")) {
                                            AlertDialog.Builder a_builder = new AlertDialog.Builder(ViewWorkItems.this);
                                            a_builder.setMessage("Data Synced to server")
                                                    .setTitle("Sync Success!")
                                                    .setCancelable(false)
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            dialogInterface.cancel();
                                                        }
                                                    });
                                            a_builder.show();
                                            myDB.updateSyncStatus(id,1);
                                            sync.setText("SYNCED");

                                        } else {
                                            AlertDialog.Builder a_builder = new AlertDialog.Builder(ViewWorkItems.this);
                                            a_builder.setMessage("Data could not be Synced to server, Please Try again later!!")
                                                    .setTitle("Sync Failure!")
                                                    .setCancelable(false)
                                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialogInterface, int i) {
                                                            dialogInterface.cancel();
                                                        }
                                                    });
                                            a_builder.show();
                                        }
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            },
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    progressDialog.dismiss();
                                    Log.e("msg", " " + error);AlertDialog.Builder a_builder = new AlertDialog.Builder(ViewWorkItems.this);
                                    a_builder.setMessage("Data could not be Synced to server, Please Try again later!!")
                                            .setTitle("Sync Failure!")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.cancel();
                                                }
                                            });
                                    a_builder.show();
                                }
                            }) {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<>();
                            params.put("UserName", Name);
                            params.put("UserPhoneNumber",phno );
                            params.put("State", state);
                            params.put("District",district);
                            params.put("Cluster",cluster);
                            params.put("GP",gp);
                            params.put("Components",components);
                            params.put("SubComponents",sub_components);
                            params.put("Status",status);
                            params.put("Phase",phase);
                            params.put("Latitude",latitude);
                            params.put("Longitude",longitude);
                            params.put("Image", mStrImg);
                            params.put("DateTime",TIME);
                            return params;
                        }
                    };

                    VolleySingleton.getInstance(ViewWorkItems.this).addToRequestQueue(stringRequest);

                }
            });
        }

        subIcons.addView(delete);
        subIcons.addView(sync);
        subIcons.setPadding(0,10,0,10);

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
        subInfoView.addView(subIcons);
        infoView.addView(subInfoView);
        infoView.setPadding(0,10,0,40);

        ll.addView(infoView);
    }
}