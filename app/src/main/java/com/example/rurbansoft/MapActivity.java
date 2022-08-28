package com.example.rurbansoft;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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
import androidx.core.content.FileProvider;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestListener;
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
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Target;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class MapActivity extends AppCompatActivity implements GoogleMap.InfoWindowAdapter, OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener
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
    String timeStamp;
    Uri ex;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        getLocationPermission();
        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        fUser=fAuth.getCurrentUser();
        progressDialog=new ProgressDialog(this);
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
        mapFragment.getMapAsync(MapActivity.this);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        progressDialog.setMessage("Please wait data is loaded");
        progressDialog.setTitle("SEARCHING RECORDS");
        progressDialog.setCanceledOnTouchOutside(false);

        // code for setting map to a point
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(20.5937, 78.9629));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(6);
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
            mMap.setOnInfoWindowClickListener(this);
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
                int j[]={0};
                String state_, district_, cluster_, gp_, component_, sub_component_, phase_, workStatus_,latitude_,longitude_;

                for(DocumentSnapshot wi:workItems)
                {

                    counter++;
                    timeStamp=wi.get("timeStamp").toString();
                    LatLng coordinate = new LatLng(Double.valueOf(wi.get("Latitude").toString()), Double.valueOf(wi.get("Longitude").toString()));

                    latitude_= wi.get("Latitude").toString();
                    longitude_=wi.get("Longitude").toString();

                    state_ = wi.get("State").toString();
                    district_ = wi.get("District").toString();
                    cluster_= wi.get("Cluster").toString();
                    gp_ = wi.get("Gram Panchayat").toString();
                    component_ = wi.get("Component").toString();
                    sub_component_ = wi.get("Sub_component").toString();
                    phase_ = wi.get("Phase").toString();
                    workStatus_= wi.get("WorkStatus").toString();

                    String infoMarker="Latitude: "+latitude_+"\nLongitude: "+longitude_+"\n\nState: "+state_+"\nDistrict: "+district_+"\nCluster: "+cluster_+"\nGram Panchayat: "+gp_+"\nComponent: "+component_+"\nSub_Component: "+sub_component_+"\nPhase: "+phase_+"\nWorkStatus: "+workStatus_;
                    Marker marker = mMap.addMarker(new MarkerOptions().position(coordinate).title(timeStamp).snippet(infoMarker));
                    StorageReference storageReference = FirebaseStorage.getInstance().getReference(userId).child(timeStamp);

                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            Bitmap bitmap[]=new Bitmap[1];
                            Thread thread = new Thread() {
                                public void run() {
                                    try {
                                        bitmap[0] = Picasso.get().load(uri).resize(100,100).get();
//
//                                        Log.e("bit" ,bitmap[0]+"");
//                                        Log.e("LatLong",wi.get("Latitude").toString());
//                                        Log.e("Marker", marker+"abs");

                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            };
                            thread.start();
                            while(bitmap[0]==null){
                                Log.e("null","still null");
                            }
                            j[0]++;

                            marker.setIcon(BitmapDescriptorFactory.fromBitmap(bitmap[0]));

                            if(j[0]==workItems.size())
                            {
                                progressDialog.dismiss();
                                Toast.makeText(MapActivity.this, "Map is Ready", Toast.LENGTH_SHORT).show();
                                AlertDialog.Builder a_builder = new AlertDialog.Builder(MapActivity.this);
                                a_builder.setMessage(j[0] + " Valid Record Found ")
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
                        }
                    });
                }

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("fetch","fetching data"+e.getMessage());
            }
        });
        progressDialog.show();
    }

    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View mkar=getLayoutInflater().inflate(R.layout.info_marker,null);

        TextView time=mkar.findViewById(R.id.time);
        TextView data=mkar.findViewById(R.id.data);

        time.setText(marker.getTitle());
        data.setText(marker.getSnippet());

        return mkar;

    }



//    private View prepareInfoView(Marker marker){
//
//        LinearLayout infoView = new LinearLayout(MapActivity.this);
//        infoView.setClickable(false);
//
//        LinearLayout.LayoutParams infoViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        infoView.setOrientation(LinearLayout.HORIZONTAL);
//        infoView.setLayoutParams(infoViewParams);
//
//        TextView InfoState = new TextView(MapActivity.this);
//        ImageView infoImageView = new ImageView(MapActivity.this);
//        infoView.addView(InfoState);
////        ImageView img=new ImageView(MapActivity.this);
////        Glide.with(MapActivity.this)
////                .load(R.drawable.nic_logo)
////                .error(R.drawable.ic_baseline_camera_alt_24)
////                .placeholder(R.drawable.ic_launcher_background)
////                .into(img);
////       // img.setImageResource(R.drawable.nic_logo);
////        Log.e("ex", ex+" ");
////        infoView.addView(img);
//
//
//        String timeUnique = marker.getTitle();
//        infoImageView.setLayoutParams(infoViewParams);
//        final String[] state = new String[1];
//        final String[] district = new String[1];
//        final String[] cluster = new String[1];
//        final String[] gp = new String[1];
//        final String[] component = new String[1];
//        final String[] sub_component = new String[1];
//        final String[] phase = new String[1];
//        final String[] workStatus = new String[1];
//
//        DocumentReference allWorkItem=fStore.collection("users").document(userId).collection("WorkItem").document(timeUnique);
//
//        allWorkItem.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
//            @Override
//            public void onSuccess(DocumentSnapshot documentSnapshot) {
//                if(documentSnapshot.exists())
//                {
//                    state[0] = documentSnapshot.get("State").toString();
//                    district[0] = documentSnapshot.get("District").toString();
//                    cluster[0] = documentSnapshot.get("Cluster").toString();
//                    gp[0] = documentSnapshot.get("Gram Panchayat").toString();
//                    component[0] = documentSnapshot.get("Component").toString();
//                    sub_component[0] = documentSnapshot.get("Sub_component").toString();
//                    phase[0] = documentSnapshot.get("Phase").toString();
//                    workStatus[0] = documentSnapshot.get("WorkStatus").toString();
//
//                    StorageReference storageReference = FirebaseStorage.getInstance().getReference(userId).child(timeUnique);
//                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
//                        @Override
//                        public void onSuccess(Uri uri) {
//                            ex=uri;
//                            infoImageView.setImageResource(R.drawable.nic_logo);
//                            Toast.makeText(MapActivity.this,"set image ",Toast.LENGTH_SHORT).show();
//
//
//                        }
//                    }).addOnFailureListener(new OnFailureListener() {
//                        @Override
//                        public void onFailure(@NonNull Exception e) {
//                            Toast.makeText(MapActivity.this,"No image "+e.getMessage(),Toast.LENGTH_SHORT).show();
//
//                        }
//                    });
//
//                }
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Toast.makeText(MapActivity.this,"Something went Wrong "+e.getMessage(),Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        infoView.addView(infoImageView);
//
//        LinearLayout subInfoView = new LinearLayout(MapActivity.this);
//        LinearLayout.LayoutParams subInfoViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//        subInfoView.setOrientation(LinearLayout.VERTICAL);
//        subInfoView.setLayoutParams(subInfoViewParams);
//
//
//        TextView subInfoState = new TextView(MapActivity.this);
//        subInfoState.setText("State " + state[0]);
//        TextView subInfoDistrict= new TextView(MapActivity.this);
//        subInfoDistrict.setText("District: " + district[0]);
//        TextView subInfoCluster = new TextView(MapActivity.this);
//        subInfoCluster.setText("Cluster: " + cluster[0]);
//        TextView subInfoGp = new TextView(MapActivity.this);
//        subInfoGp.setText("GP: " + gp[0]);
//        TextView subInfoComponents = new TextView(MapActivity.this);
//        subInfoComponents.setText("Components: " + component[0]);
//        TextView subInfoSubComponents= new TextView(MapActivity.this);
//        subInfoSubComponents.setText("Sub_Components: " + sub_component[0]);
//        TextView subInfoPhase= new TextView(MapActivity.this);
//        subInfoPhase.setText("Phase: " + phase[0]);
//        TextView subInfoComment= new TextView(MapActivity.this);
//        subInfoComment.setText("WorkStatus: " + workStatus[0]);
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

    @Override
    public void onInfoWindowClick(@NonNull Marker marker) {

        Intent intent = new Intent(MapActivity.this, MarkerInfoMap.class);
        intent.putExtra("timeStamp", marker.getTitle());
        startActivity(intent);

    }



}