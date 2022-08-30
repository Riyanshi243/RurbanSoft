package com.example.rurbansoft;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class LandingUser extends AppCompatActivity {

    Button workItem, map;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_landing_user);

        workItem=findViewById(R.id.workItem);
        map=findViewById(R.id.mapView);
        //for adding new workitem
        workItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LandingUser.this, MainActivity.class);
                startActivity(intent);
            }
        });
        //for viewing existing items on map
        map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LandingUser.this, MapActivity.class);
                startActivity(intent);
            }
        });
    }
    @Override
    public void onBackPressed() {

        //logout feature
        AlertDialog.Builder alertbox = new AlertDialog.Builder(this)
                .setMessage("Do you want to Logout?")
                .setTitle("Logout ")
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        LandingUser.super.onBackPressed();
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.cancel();
                    }
                });
        alertbox.show();
    }
}