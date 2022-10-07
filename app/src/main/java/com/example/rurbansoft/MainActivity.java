package com.example.rurbansoft;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.SQLException;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Button saveItem;
    private Spinner spinnerState,spinnerDistrict, spinnerCluster,spinnerGP,spinnerComponents,spinnerSubComponents,spinnerPhase,spinnerStatus;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        saveItem=findViewById(R.id.saveItem);

        spinnerState = (Spinner) findViewById(R.id.spinner1);
        spinnerDistrict = (Spinner)findViewById(R.id.spinner2);
        spinnerCluster = (Spinner)findViewById(R.id.spinner3);
        spinnerGP = (Spinner)findViewById(R.id.spinner4);
        spinnerComponents = (Spinner)findViewById(R.id.spinner5);
        spinnerSubComponents = (Spinner)findViewById(R.id.spinner6);
        spinnerPhase = (Spinner)findViewById(R.id.spinner7);
        spinnerStatus = (Spinner)findViewById(R.id.spinner8);

        spinnerState.setOnItemSelectedListener(this);
        spinnerDistrict.setOnItemSelectedListener(this);
        spinnerCluster.setOnItemSelectedListener(this);
        spinnerGP.setOnItemSelectedListener(this);
        spinnerComponents.setOnItemSelectedListener(this);
        spinnerSubComponents.setOnItemSelectedListener(this);
        spinnerStatus.setOnItemSelectedListener(this);

        DatabaseHelper myDB = new DatabaseHelper(this);
        try {
            myDB.createDataBase();
        } catch (IOException ioe) {
            throw new Error("Unable to create database");
        }
        try {
            myDB.openDataBase();
        } catch (SQLException sqle) {
            throw sqle;
        }
        loadSpinnerData(1,null);

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


        if(spinnerState.getSelectedItem().toString().startsWith("--") )
        {
            ((TextView)spinnerState.getSelectedView()).setError("Select State from dropdown");
            spinnerState.requestFocus();
        }
        else if(spinnerDistrict.getSelectedItem().toString().startsWith("--") )
        {
            ((TextView)spinnerDistrict.getSelectedView()).setError("Select District from dropdown");
            spinnerDistrict.requestFocus();
        }
        else if(spinnerCluster.getSelectedItem().toString().startsWith("--"))
        {
            ((TextView)spinnerCluster.getSelectedView()).setError("Select Cluster from dropdown");
            spinnerCluster.requestFocus();
        }
        else if(spinnerGP.getSelectedItem().toString().startsWith("--"))
        {
            ((TextView)spinnerGP.getSelectedView()).setError("Select GP from dropdown");
            spinnerGP.requestFocus();
        }
        else if(spinnerComponents.getSelectedItem().toString().startsWith("--"))
        {
            ((TextView)spinnerComponents.getSelectedView()).setError("Select Component from dropdown");
            spinnerComponents.requestFocus();
        }
        else if(spinnerSubComponents.getSelectedItem().toString().startsWith("--"))
        {
            ((TextView)spinnerSubComponents.getSelectedView()).setError("Select SubComponent from dropdown");
            spinnerSubComponents.requestFocus();
        }
        else if(spinnerPhase.getSelectedItem().toString().startsWith("--"))
        {
            ((TextView)spinnerPhase.getSelectedView()).setError("Select Phase from dropdown");
            spinnerPhase.requestFocus();
        }
        else if(spinnerStatus.getSelectedItem().toString().startsWith("--"))
        {
            ((TextView)spinnerStatus.getSelectedView()).setError("Select Status from dropdown");
            spinnerStatus.requestFocus();
        }
        else
        {
            String state_=spinnerState.getSelectedItem().toString();
            String district_=spinnerDistrict.getSelectedItem().toString();
            String cluster_=spinnerCluster.getSelectedItem().toString();
            String gp_=spinnerGP.getSelectedItem().toString();
            String component_=spinnerComponents.getSelectedItem().toString();
            String sub_component_=spinnerSubComponents.getSelectedItem().toString();
            String phase_=spinnerPhase.getSelectedItem().toString();
            String workStatus_= spinnerStatus.getSelectedItem().toString();
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

    private void loadSpinnerData(int spinnerID ,String name) {
        DatabaseHelper myDB = new DatabaseHelper(this);

        switch (spinnerID){
            case 1 :
                List<String> state = myDB.getData(1,null);
                state.add(0,"--Select State--");
                ArrayAdapter<String> stateAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, state);
                stateAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerState.setAdapter(stateAdapter);
                break;
            case 2:
                List<String> dis = new ArrayList<String>();
                dis = myDB.getData(2,name);
                dis.add(0,"--Select District--");
                ArrayAdapter<String> districtAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, dis);
                districtAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerDistrict.setAdapter(districtAdapter);
                break;
            case 3:
                List<String> cluster = myDB.getData(3,name);
                cluster.add(0,"--Select Cluster--");
                ArrayAdapter<String> clusterAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, cluster);
                clusterAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerCluster.setAdapter(clusterAdapter);
                break;
            case 4:
                List<String> gp = myDB.getData(4,name);
                gp.add(0,"--Select GP--");
                ArrayAdapter<String> gpAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, gp);
                gpAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerGP.setAdapter(gpAdapter);
                break;
            case 5:
                List<String> component = myDB.getData(5,name);
                component.add(0,"--Select Component--");
                ArrayAdapter<String> componentAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, component);
                componentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerComponents.setAdapter(componentAdapter);
                break;
            case 6:
                List<String> sub_component = myDB.getData(6,name);
                sub_component.add(0,"--Select Sub-Component--");
                ArrayAdapter<String> sub_componentAdapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_item, sub_component);
                sub_componentAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerSubComponents.setAdapter(sub_componentAdapter);
                break;
            case 7:
                String phase_list[]={"--Select Phase--","I","II","III","IV","V"};
                ArrayAdapter<String> phaseAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, phase_list);
                phaseAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerPhase.setAdapter(phaseAdapter);
            case 8:
                String status_list[]={"--Select status of work--","Started","In progress","Completed","Stopped"};
                ArrayAdapter<String> statusAdapter = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, status_list);
                statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinnerStatus.setAdapter(statusAdapter);
            default:
                //Toast.makeText(this, "Something went wrong .. switch went to default case", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()){
            case R.id.spinner1:
                if(position!=0){
                    String name = parent.getSelectedItem().toString();
                    loadSpinnerData(2,name);
                }
                break;
            case R.id.spinner2:
                if(position!=0){
                    String name = parent.getSelectedItem().toString();
                    loadSpinnerData(3,name);
                }
                break;
            case R.id.spinner3:
                if(position!=0){
                    String name = parent.getSelectedItem().toString();
                    loadSpinnerData(4,name);
                }
                break;
            case R.id.spinner4:
                if(position!=0){
                    String name = parent.getSelectedItem().toString();
                    loadSpinnerData(5,name);
                }
                break;
            case R.id.spinner5:
                if(position!=0){
                    String name = parent.getSelectedItem().toString();
                    loadSpinnerData(6,name);
                }
                break;
            case R.id.spinner6:
                if(position!=0){
                    String name = parent.getSelectedItem().toString();
                    loadSpinnerData(7,name);
                }
                break;
            case R.id.spinner7:
                if(position!=0){
                    String name = parent.getSelectedItem().toString();
                    loadSpinnerData(8,name);
                }
                break;
            default:
                //Toast.makeText(this, "Select spinner first", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        saveItem.setEnabled(false);

    }
}