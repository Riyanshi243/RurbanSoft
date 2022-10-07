package com.example.rurbansoft;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;


public class RegistrationActivity extends AppCompatActivity {

    EditText name, designation,phone,email,password;
    Button register;
    TextView login;
    DBHelper myDB;
    String name_,designation_,phone_,email_, password_;
    //public static final String URL_SAVE_USER = "https://192.168.1.52/SqliteSync/saveUsers.php";
    public static final int USER_SYNCED_WITH_SERVER = 1;
    public static final int USER_NOT_SYNCED_WITH_SERVER = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        name=findViewById(R.id.name);
        designation=findViewById(R.id.designation);
        phone=findViewById(R.id.phone);
        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        register=findViewById(R.id.register);
        login=findViewById(R.id.login);
        myDB = new DBHelper(this);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
                startActivity(intent);
                RegistrationActivity.this.finish();
            }
        });

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNameToServer();
            }
        });
    }

    private void saveUserToLocalStorage(int sync) {

        if(checkAllFields())
        {//saving to local storage
            boolean result =  myDB.RegisterUser(name_,designation_, phone_,email_,password_, sync);
            if(result == true){
                Toast.makeText(RegistrationActivity.this, "Registration Successful", Toast.LENGTH_LONG).show();
                Intent intent  = new Intent(RegistrationActivity.this,LoginActivity.class);
                startActivity(intent);
                RegistrationActivity.this.finish();
            }
            else{
                Toast.makeText(RegistrationActivity.this, "Something went Wrong try again later", Toast.LENGTH_LONG).show();
            }
        }

    }
    public void saveNameToServer() {


        if(checkAllFields()) {

            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Saving USER...");
            progressDialog.show();
            HttpsTrustManager.allowAllSSL();

            StringRequest stringRequest = new StringRequest(Request.Method.POST, ServerConnect.SERVER_URL_Users,
                    new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            progressDialog.dismiss();
                            try {
                                JSONObject obj = new JSONObject(response);
                                if (!obj.getBoolean("error")) {

                                    AlertDialog.Builder a_builder = new AlertDialog.Builder(RegistrationActivity.this);
                                    a_builder.setMessage("User Details synced to server!!")
                                            .setTitle("Sync Status!")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.cancel();
                                                }
                                            });
                                    a_builder.show();
                                    saveUserToLocalStorage(USER_SYNCED_WITH_SERVER);
                                } else {

                                    AlertDialog.Builder a_builder = new AlertDialog.Builder(RegistrationActivity.this);
                                    a_builder.setMessage("User Details could not be synced to server, Please sync manually by visiting \"VIEW ALL EXISTING USERS\" on login page !!")
                                            .setTitle("Sync Status!")
                                            .setCancelable(false)
                                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialogInterface, int i) {
                                                    dialogInterface.cancel();
                                                }
                                            });
                                    a_builder.show();
                                    saveUserToLocalStorage(USER_NOT_SYNCED_WITH_SERVER);
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
                            Log.e("msg", " " + error);

                            AlertDialog.Builder a_builder = new AlertDialog.Builder(RegistrationActivity.this);
                            a_builder.setMessage("User Details could not be synced to server, Please sync manually by visiting \"VIEW ALL EXISTING USERS\" on login page !!")
                                    .setTitle("Sync Status!")
                                    .setCancelable(false)
                                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            dialogInterface.cancel();
                                        }
                                    });
                            a_builder.show();
                            saveUserToLocalStorage(USER_NOT_SYNCED_WITH_SERVER);
                        }
                    }) {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<>();
                    params.put("Name", name_);
                    params.put("PhoneNumber",phone_ );
                    params.put("EmailId", email_);
                    params.put("Designation",designation_);
                    params.put("Password",password_);
                    return params;
                }
            };

            VolleySingleton.getInstance(this).addToRequestQueue(stringRequest);
        }
    }

    public boolean checkAllFields()
    {
        //performing registration and storing details of user
        name_=name.getText().toString().trim();
        designation_=designation.getText().toString().trim();
        phone_=phone.getText().toString().trim();
        email_=email.getText().toString().trim();
        password_=password.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(name_.length()==0)
        {
            name.setError("Enter Name");
            name.requestFocus();
            return false;
        }
        else if(designation_.length()==0)
        {
            designation.setError("Enter Designation");
            designation.requestFocus();
            return false;
        }
        else if(phone_.length()==0)
        {
            phone.setError("Enter Phone Number");
            phone.requestFocus();
            return false;
        }
        else if(phone_.length()<10 || phone_.length()>12  )
        {
            phone.setError("Enter valid Phone Number");
            phone.requestFocus();
            return false;
        }
        else if(email_.length()==0)
        {
            email.setError("Enter Email Id");
            email.requestFocus();
            return false;
        }
        else if (!email_.matches(emailPattern))
        {
            email.setError("Enter valid EmailId");
            email.requestFocus();
            return false;
        }
        else if(password_.length()==0)
        {
            password.setError("Enter Password");
            password.requestFocus();
            return false;
        }
        else if(password_.length()<8)
        {
            password.setError("Password must be at least 8 Characters");
            password.requestFocus();
            return false;
        }

        return true;

    }


}