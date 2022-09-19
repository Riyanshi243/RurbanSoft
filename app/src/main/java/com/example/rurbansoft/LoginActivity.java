package com.example.rurbansoft;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends AppCompatActivity {

    EditText phno,password;
    Button login, existingUsers;
    TextView register;
    DatabaseHelper myDB;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        phno=findViewById(R.id.phno);
        password=findViewById(R.id.password);
        login=findViewById(R.id.login);
        register=findViewById(R.id.register);
        existingUsers=findViewById(R.id.existingUsers);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.ACCESS_NETWORK_STATE) == PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getApplicationContext(),
                android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
        }
        else{
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.WRITE_EXTERNAL_STORAGE,}, 101);

        }

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkInternetConenction()) {
                    Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                    startActivity(intent);
                    LoginActivity.this.finish();
                }
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (checkInternetConenction())
                    PerformLogin();
            }
        });

        existingUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, ViewUsers.class);
                startActivity(intent);
            }
        });

    }



    private void PerformLogin() {
        String phno_=phno.getText().toString().trim();
        String password_=password.getText().toString().trim();

        if(phno_.length()==0)
        {
            phno.setError("Enter Phone Number");
            phno.requestFocus();
        }
        else if(phno_.length()<10 || phno_.length()>12  )
        {
            phno.setError("Enter valid Phone Number");
            phno.requestFocus();
        }
        else if(password_.length()==0)
        {
            password.setError("Enter Password");
            password.requestFocus();
        }
        else if(password_.length()<8)
        {
            password.setError("Password must be at least 8 Characters");
            password.requestFocus();
        }

        else{
            myDB = new DatabaseHelper(this);
            boolean result = myDB.checkLogin(phno_,password_);
            if(result == true){
                Toast.makeText(LoginActivity.this, "Login Successful", Toast.LENGTH_LONG).show();
                Intent intent  = new Intent(LoginActivity.this,LandingUser.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
            else{
                Toast.makeText(LoginActivity.this, "User Not found, Please Register first..", Toast.LENGTH_LONG).show();
            }

        }
    }
    @SuppressLint("MissingPermission")
    private boolean checkInternetConenction() {
        ConnectivityManager connec = (ConnectivityManager)getSystemService(getBaseContext().CONNECTIVITY_SERVICE);

        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED ) {
            return true;
        }
        else if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED  ) {
            AlertDialog.Builder a_builder = new AlertDialog.Builder(LoginActivity.this);
            a_builder.setMessage("Please Check Your Internet Connection.")
                    .setTitle("Network Error!!")
                    .setCancelable(false)
                    .setPositiveButton("Turn On", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivity(new Intent(Settings.ACTION_WIFI_SETTINGS));
                        }
                    })
                    .setNegativeButton("Later", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            return;
                        }
                    });
            a_builder.show();
        }
        return false;
    }

}