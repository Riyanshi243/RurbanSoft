package com.example.rurbansoft;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

public class MapsActivity extends AppCompatActivity implements GoogleMap.InfoWindowAdapter, OnMapReadyCallback
{
    private static final String TAG = "MapActivity";
    private GoogleMap mMap;

    private Boolean mLocationPermissionGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        getLocationPermission();
    }

    private void getLocationPermission() {
        Log.d(TAG, "getLocationPermission: getting location permission");
        String[] permission = {android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
        {
            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
            {
                mLocationPermissionGranted = true;
                initMap();
            }
            else
            {
                ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
            }
        }
        else
        {
            ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
        }
    }

    private void initMap() {
        Log.d(TAG, "initMap: Intialising Map");
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: Map is Ready");
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // code for setting map to a point
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(20.5937, 78.9629));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(5);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);
        if (mLocationPermissionGranted)
        {
            getLocationFromDataBase();
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                return;
            }
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.setInfoWindowAdapter(this);
        }

    }

    private void getLocationFromDataBase() {
        DBHelper myDB = new DBHelper(this);
        List<LatLng> lls = myDB.getLatLng();
        int i=1;
        int j=0;
        for (LatLng ll : lls) {
            LatLng coordinate = new LatLng(ll.latitude, ll.longitude);

            Log.e("latlong", coordinate.toString());

            Marker marker = mMap.addMarker(new MarkerOptions().position(coordinate).title(String.valueOf(i++)));
            marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            j++;
        }
        AlertDialog.Builder a_builder = new AlertDialog.Builder(MapsActivity.this);
        a_builder.setMessage(j + " Valid Record Found ")
                .setTitle("Records Found")
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        a_builder.show();
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        //return null;
        return prepareInfoView(marker);
    }

    private View prepareInfoView(Marker marker){

        LinearLayout infoView = new LinearLayout(MapsActivity.this);
        LinearLayout.LayoutParams infoViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        infoView.setOrientation(LinearLayout.HORIZONTAL);
        infoView.setLayoutParams(infoViewParams);

        ImageView infoImageView = new ImageView(MapsActivity.this);

        DBHelper myDB = new DBHelper(this);
        String id = marker.getTitle();
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

        int targetW = photoW / 4; //image to reduce to 1/10 of original
        int targetH = photoH / 4;

        bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
        Matrix matrix = new Matrix();

        matrix.postRotate(90);
        Bitmap photo = Bitmap.createScaledBitmap(bitmap, targetW, targetH, false);
        Bitmap rotatedBitmap = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true);

        infoImageView.setLayoutParams(infoViewParams);
        infoImageView.setImageBitmap(rotatedBitmap);
        infoImageView.setPadding(10, 10, 20, 10);
        infoView.addView(infoImageView);

        LinearLayout subInfoView = new LinearLayout(MapsActivity.this);
        LinearLayout.LayoutParams subInfoViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        subInfoView.setOrientation(LinearLayout.VERTICAL);
        subInfoView.setLayoutParams(subInfoViewParams);

        TextView subInfoTime = new TextView(MapsActivity.this);
        subInfoTime.setText("TimeStamp: " + TIME);
        TextView subInfoState = new TextView(MapsActivity.this);
        subInfoState.setText("State: " + state);
        TextView subInfoDistrict= new TextView(MapsActivity.this);
        subInfoDistrict.setText("District: " + district);
        TextView subInfoCluster = new TextView(MapsActivity.this);
        subInfoCluster.setText("Cluster: " + cluster);
        TextView subInfoGp = new TextView(MapsActivity.this);
        subInfoGp.setText("GP: " + gp);
        TextView subInfoComponents = new TextView(MapsActivity.this);
        subInfoComponents.setText("Components: " + components);
        TextView subInfoSubComponents= new TextView(MapsActivity.this);
        subInfoSubComponents.setText("Sub Components: " + sub_components);
        TextView subInfoPhase= new TextView(MapsActivity.this);
        subInfoPhase.setText("Phase: " + phase);
        TextView subInfoStatus= new TextView(MapsActivity.this);
        subInfoStatus.setText("Status: " + status);
        TextView subInfoLat= new TextView(MapsActivity.this);
        subInfoLat.setText("Latitude: " + lat);
        TextView subInfoLon= new TextView(MapsActivity.this);
        subInfoLon.setText("Longitude: " + lon);

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
        infoView.addView(subInfoView);

        return infoView;
    }
}
