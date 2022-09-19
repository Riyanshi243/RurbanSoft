package com.example.rurbansoft;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


public class MainActivity extends AppCompatActivity {

    EditText state, district, cluster, gp, component, sub_component, phase, workStatus;
    Button saveItem;
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

        //save the entered details to move on to image capture. data will not be saved in database at this level.
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
        //sending all the entered values to the next activity
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
            state.requestFocus();
        }
        else if(district_.length()==0)
        {
            district.setError("Enter District");
            district.requestFocus();
        }
        else if(cluster_.length()==0)
        {
            cluster.setError("Enter Cluster");
            cluster.requestFocus();
        }
        else if(gp_.length()==0)
        {
            gp.setError("Enter Gram Panchayat");
            gp.requestFocus();
        }
        else if(component_.length()==0)
        {
            component.setError("Enter Component");
            component.requestFocus();
        }
        else if(sub_component_.length()==0)
        {
            sub_component.setError("Enter Sub Component");
            sub_component.requestFocus();
        }
        else if(phase_.length()==0)
        {
            phase.setError("Enter Phase");
            phase.requestFocus();
        }
        else if(workStatus_.length()==0)
        {
            workStatus.setError("Enter Work Status");
            workStatus.requestFocus();
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