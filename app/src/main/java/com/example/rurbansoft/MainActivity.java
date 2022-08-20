package com.example.rurbansoft;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    EditText state, district, cluster, gp, component, sub_component, phase, workStatus;
    Button saveItem;
    ProgressDialog progressDialog;
    FirebaseAuth fAuth;
    FirebaseUser fUser;
    FirebaseFirestore fStore;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        state=findViewById(R.id.state);
        district=findViewById(R.id.district);
        cluster=findViewById(R.id.cluster);
        gp=findViewById(R.id.gp);
        component=findViewById(R.id.component);
        sub_component=findViewById(R.id.sub_component);
        phase=findViewById(R.id.phase);
        workStatus=findViewById(R.id.workStatus);

        saveItem=findViewById(R.id.saveItem);
        progressDialog=new ProgressDialog(this);
        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        fUser=fAuth.getCurrentUser();

        saveItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveWorkItem();
            }
        });
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }
    }

    private void saveWorkItem() {
        String state_=state.getText().toString().trim();
        String district_=district.getText().toString().trim();
        String cluster_=cluster.getText().toString().trim();
        String gp_=gp.getText().toString().trim();
        String component_=component.getText().toString().trim();
        String sub_component_=sub_component.getText().toString().trim();
        String phase_=phase.getText().toString().trim();
        String workStatus_=workStatus.getText().toString().trim();

        if(state_.length()==0)
        {
            state.setError("Enter State");
        }
        else if(district_.length()==0)
        {
            district.setError("Enter District");
        }
        else if(cluster_.length()==0)
        {
            cluster.setError("Enter Cluster");
        }
        else if(gp_.length()==0)
        {
            gp.setError("Enter Gram Panchayat");
        }
        else if(component_.length()==0)
        {
            component.setError("Enter Component");
        }
        else if(sub_component_.length()==0)
        {
            sub_component.setError("Enter Sub Component");
        }
        else if(phase_.length()==0)
        {
            phase.setError("Enter Phase");
        }
        else if(workStatus_.length()==0)
        {
            workStatus.setError("Enter Work Status");
        }
        else
        {
            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
            intent.putExtra("state", state_);
            intent.putExtra("district", district_);
            intent.putExtra("cluster", cluster_);
            intent.putExtra("gp", gp_);
            intent.putExtra("component", component_);
            intent.putExtra("sub_component", sub_component_);
            intent.putExtra("phase", phase_);
            intent.putExtra("status", workStatus_);
            startActivity(intent);
        }
    }

}