package com.example.rurbansoft;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.rurbansoft.databinding.ActivityMapsBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Text;

import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.InfoWindowAdapter {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;
    FirebaseAuth fAuth;
    FirebaseUser fUser;
    FirebaseFirestore fStore;
    String userId, infoMarker;
    String state, district, cluster, gp, component, sub_component, phase, workStatus, timeStamp;
    int counter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();
        fUser = fAuth.getCurrentUser();
        userId = fUser.getUid();

    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        Toast.makeText(this, "Map is Ready", Toast.LENGTH_SHORT).show();

        mMap = googleMap;
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setInfoWindowAdapter(this);
        // code for setting map to a point
        CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(20.5937, 78.9629));
        CameraUpdate zoom = CameraUpdateFactory.zoomTo(6);
        mMap.moveCamera(center);
        mMap.animateCamera(zoom);

        Query df = fStore.collection("users").document(userId).collection("WorkItem");
        df.get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<DocumentSnapshot> workItems = queryDocumentSnapshots.getDocuments();
                int i = 1;
                int j = 0;
                for (DocumentSnapshot wi : workItems) {
                    counter++;
                    state = wi.get("State").toString();
                    district = wi.get("District").toString();
                    cluster= wi.get("Cluster").toString();
                    gp = wi.get("Gram Panchayat").toString();
                    component = wi.get("Component").toString();
                    sub_component = wi.get("Sub_component").toString();
                    phase = wi.get("Phase").toString();
                    workStatus= wi.get("WorkStatus").toString();
                    timeStamp=wi.get("timeStamp").toString();
                    infoMarker="State: "+state+"\nDistrict: "+district+"\nCluster: "+cluster;

                    LatLng coordinate = new LatLng(Double.valueOf(wi.get("Latitude").toString()), Double.valueOf(wi.get("Longitude").toString()));
                    Marker marker = mMap.addMarker(new MarkerOptions().position(coordinate));
                    marker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
                    j++;
                    marker.showInfoWindow();
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
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("fetch", "fetching data" + e.getMessage());
            }
        });

    }

    @Nullable
    @Override
    public View getInfoWindow(@NonNull Marker marker) {
        return null;
        //return prepareInfoView(marker);
    }

    @Nullable
    @Override
    public View getInfoContents(@NonNull Marker marker) {
        //return null;
        return prepareInfoView(marker);
    }
    private View prepareInfoView(Marker marker) {
        LinearLayout infoView = new LinearLayout(MapsActivity.this);
        LinearLayout.LayoutParams infoViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        infoView.setOrientation(LinearLayout.HORIZONTAL);
        infoView.setLayoutParams(infoViewParams);

        ImageView infoImageView = new ImageView(MapsActivity.this);

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;


        LinearLayout subInfoView = new LinearLayout(MapsActivity.this);
        LinearLayout.LayoutParams subInfoViewParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        subInfoView.setOrientation(LinearLayout.VERTICAL);
        subInfoView.setLayoutParams(subInfoViewParams);

        TextView subInfoState = new TextView(MapsActivity.this);
        subInfoState.setText("State " + state);
        TextView subInfoDistrict= new TextView(MapsActivity.this);
        subInfoDistrict.setText("District: " + district);
        TextView subInfoCluster = new TextView(MapsActivity.this);
        subInfoCluster.setText("Cluster: " + cluster);

        TextView subInfoGp = new TextView(MapsActivity.this);
        subInfoGp.setText("GP: " + gp);

        TextView subInfoComponents = new TextView(MapsActivity.this);
        subInfoComponents.setText("Components: " + component);

        TextView subInfoSubComponents= new TextView(MapsActivity.this);
        subInfoSubComponents.setText("Sub_Components: " + sub_component);

        TextView subInfoPhase= new TextView(MapsActivity.this);
        subInfoPhase.setText("Phase: " + phase);

        TextView subInfoComment= new TextView(MapsActivity.this);
        subInfoComment.setText("WorkStatus: " + workStatus);

        subInfoView.addView(subInfoState);
        subInfoView.addView(subInfoDistrict);
        subInfoView.addView(subInfoCluster);
        subInfoView.addView(subInfoGp);
        subInfoView.addView(subInfoComponents);
        subInfoView.addView(subInfoSubComponents);
        subInfoView.addView(subInfoPhase);
        subInfoView.addView(subInfoComment);
        infoView.addView(subInfoView);

        return infoView;

    }


}
