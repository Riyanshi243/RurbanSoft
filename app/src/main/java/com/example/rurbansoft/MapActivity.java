package com.example.rurbansoft;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.List;

public class MapActivity extends AppCompatActivity implements GoogleMap.InfoWindowAdapter, OnMapReadyCallback
{
    private static final String TAG = "MapActivity";
    private GoogleMap mMap;

    private FusedLocationProviderClient mFusedLocationProviderClient;
    private Boolean mLocationPermissionGranted = false;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;
    private static final String FINE_LOCATION = android.Manifest.permission.ACCESS_FINE_LOCATION;
    private static final String COARSE_LOCATION = android.Manifest.permission.ACCESS_COARSE_LOCATION;
    private static final float DEFAULT_ZOOM = 15f;
    FirebaseAuth fAuth;
    FirebaseUser fUser;
    FirebaseFirestore fStore;
    String userId;
    int counter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        //getLocationPermission();
        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        fUser=fAuth.getCurrentUser();
    }

//    private void getLocationPermission() {
//        Log.d(TAG, "getLocationPermission: getting location permission");
//        String[] permission = {android.Manifest.permission.ACCESS_FINE_LOCATION,
//                android.Manifest.permission.ACCESS_COARSE_LOCATION};
//
//        if (ContextCompat.checkSelfPermission(this.getApplicationContext(), FINE_LOCATION) == PackageManager.PERMISSION_GRANTED)
//        {
//            if (ContextCompat.checkSelfPermission(this.getApplicationContext(), COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED)
//            {
//                mLocationPermissionGranted = true;
//                initMap();
//            }
//            else
//            {
//                ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
//            }
//        }
//        else
//        {
//            ActivityCompat.requestPermissions(this, permission, LOCATION_PERMISSION_REQUEST_CODE);
//        }
//    }
//
//    private void initMap() {
//        Log.d(TAG, "initMap: Intialising Map");
//        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
//        mapFragment.getMapAsync(MapActivity.this);
//    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();
        Log.d(TAG, "onMapReady: Map is Ready");
        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        // code for setting map to a point
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(20.5937, 78.9629));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(3);
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
            //mMap.setMyLocationEnabled(false);
            mMap.getUiSettings().setCompassEnabled(true);
            mMap.setInfoWindowAdapter(this);
        }

    }

    private void getLocationFromDataBase() {
        userId=fAuth.getCurrentUser().getUid();
        Query df =  fStore.collection("users").document(userId).collection("WorkItem");
        df.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> workItems = queryDocumentSnapshots.getDocuments();
                int i=1;
                int j=0;
                for(DocumentSnapshot wi:workItems)
                {
                    counter++;
                    LatLng coordinate = new LatLng(Double.valueOf(wi.get("Latitude").toString()), Double.valueOf(wi.get("Longitude").toString()));
                    Marker marker = mMap.addMarker(new MarkerOptions().position(coordinate).title(String.valueOf(i++)));
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                    j++;
                }
                AlertDialog.Builder a_builder = new AlertDialog.Builder(MapActivity.this);
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
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("fetch","fetching data"+e.getMessage());
            }
        });

    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
        //prepareInfoView(marker);
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
        //return prepareInfoView(marker);
    }



//    //prepare InfoView programmatically
//    private View prepareInfoView(Marker marker){
//
//        LinearLayout infoView = new LinearLayout(MapActivity.this);
//        LinearLayout.LayoutParams infoViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        infoView.setOrientation(LinearLayout.HORIZONTAL);
//        infoView.setLayoutParams(infoViewParams);
//
//        ImageView infoImageView = new ImageView(MapActivity.this);
//
//        DatabaseHelper myDB = new DatabaseHelper(this);
//        //long i = myDB.getCount();
//        // long id = Long.getLong( marker.getTitle());
//        String id = marker.getTitle();
//        Cursor cursor = myDB.getImage(id);
//
//        //---------------------------//
//        String state = cursor.getString(cursor.getColumnIndex("State"));
//        String district = cursor.getString(cursor.getColumnIndex("DISTRICT"));
//        String cluster= cursor.getString(cursor.getColumnIndex("CLUSTER"));
//        String gp = cursor.getString(cursor.getColumnIndex("GP"));
//        String components = cursor.getString(cursor.getColumnIndex("COMPONENTS"));
//        String sub_components = cursor.getString(cursor.getColumnIndex("SUB_COMPONENTS"));
//        String phase = cursor.getString(cursor.getColumnIndex("PHASE"));
//        String comment = cursor.getString(cursor.getColumnIndex("COMMENT"))+marker.getTitle();
//        byte[] img = cursor.getBlob(cursor.getColumnIndex("IMAGE"));
//
//        // Get the dimensions of the bitmap
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//
//        Bitmap bitmap = BitmapFactory.decodeByteArray(img, 0, img.length, bmOptions);
//
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//
//        int targetW = photoW / 4; //image to reduce to 1/10 of original
//        int targetH = photoH / 4;
//
//        // Determine how much to scale down the image
//        //int scaleFactor = Math.min(photoW/targetW, photoH/targetH);
//        bitmap = BitmapFactory.decodeByteArray(img, 0, img.length);
//        Bitmap photo = Bitmap.createScaledBitmap(bitmap, targetW, targetH, false);
//        //---------------------------//
//
//        infoImageView.setLayoutParams(infoViewParams);
//        infoImageView.setImageBitmap(photo);
//        infoView.addView(infoImageView);
//
//        LinearLayout subInfoView = new LinearLayout(MapActivity.this);
//        LinearLayout.LayoutParams subInfoViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        subInfoView.setOrientation(LinearLayout.VERTICAL);
//        subInfoView.setLayoutParams(subInfoViewParams);
//
//        TextView subInfoState = new TextView(MapActivity.this);
//        subInfoState.setText("State " + state);
//        TextView subInfoDistrict= new TextView(MapActivity.this);
//        subInfoDistrict.setText("District: " + district);
//        TextView subInfoCluster = new TextView(MapActivity.this);
//        subInfoCluster.setText("Cluster: " + cluster);
//
//        TextView subInfoGp = new TextView(MapActivity.this);
//        subInfoGp.setText("GP: " + gp);
//
//        TextView subInfoComponents = new TextView(MapActivity.this);
//        subInfoComponents.setText("Components: " + components);
//
//        TextView subInfoSubComponents= new TextView(MapActivity.this);
//        subInfoSubComponents.setText("Sub_Components: " + sub_components);
//
//        TextView subInfoPhase= new TextView(MapActivity.this);
//        subInfoPhase.setText("Phase: " + phase);
//
//        TextView subInfoComment= new TextView(MapActivity.this);
//        subInfoComment.setText("Remark: " + comment);
//
//        subInfoView.addView(subInfoState);
//        subInfoView.addView(subInfoDistrict);
//        subInfoView.addView(subInfoCluster);
//        subInfoView.addView(subInfoGp);
//        subInfoView.addView(subInfoComponents);
//        subInfoView.addView(subInfoSubComponents);
//        subInfoView.addView(subInfoPhase);
//        subInfoView.addView(subInfoComment);
//        infoView.addView(subInfoView);
//
//        return infoView;
//    }
}
