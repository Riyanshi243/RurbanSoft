package com.example.rurbansoft;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegistrationActivity extends AppCompatActivity {

    EditText name, designation,phone,email,password;
    Button register;
    TextView login;
    FirebaseAuth fAuth;
    FirebaseUser fUser;
    FirebaseFirestore fStore;
    String userId;
    DatabaseHelper myDB;

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
        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        fUser=fAuth.getCurrentUser();
        myDB = new DatabaseHelper(this);

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
                PerformAuth();
            }
        });
    }

    private void PerformAuth() {

        //performing registration and storing details of user
        String name_=name.getText().toString().trim();
        String designation_=designation.getText().toString().trim();
        String phone_=phone.getText().toString().trim();
        String email_=email.getText().toString().trim();
        String password_=password.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
        if(name_.length()==0)
        {
            name.setError("Enter Name");
            name.requestFocus();
        }
        else if(designation_.length()==0)
        {
            designation.setError("Enter Designation");
            designation.requestFocus();
        }
        else if(phone_.length()==0)
        {
            phone.setError("Enter Phone Number");
            phone.requestFocus();
        }
        else if(phone_.length()<10 || phone_.length()>12  )
        {
            phone.setError("Enter valid Phone Number");
            phone.requestFocus();
        }
        else if(email_.length()==0)
        {
            email.setError("Enter Email Id");
            email.requestFocus();
        }
        else if (!email_.matches(emailPattern))
        {
            email.setError("Enter valid EmailId");
            email.requestFocus();
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

//            fAuth.createUserWithEmailAndPassword(email_, password_).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//                @Override
//                public void onComplete(@NonNull Task<AuthResult> task) {
//                    if(task.isSuccessful())
//                    {
//                        userId=fAuth.getCurrentUser().getUid();
//                        DocumentReference allUserData=fStore.collection("users").document(userId);
//                        Map<String, Object> users=new HashMap<>();
//
//                        users.put("Email",email_);
//                        users.put("Designation",designation_);
//                        users.put("Name",name_);
//                        users.put("Phone",phone_);
//                        allUserData.set(users).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void unused) {
//                                progressDialog.dismiss();
//                                Toast.makeText(RegistrationActivity.this,"Registration Successful",Toast.LENGTH_SHORT).show();
//                                Intent intent = new Intent(RegistrationActivity.this, LoginActivity.class);
//                                startActivity(intent);
//                                RegistrationActivity.this.finish();
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                progressDialog.dismiss();
//                                Toast.makeText(RegistrationActivity.this,"Registration Failed",Toast.LENGTH_SHORT).show();
//                            }
//                        });
//                    }
//                    else
//                    {
//                        progressDialog.dismiss();
//                        Toast.makeText(RegistrationActivity.this,"Registration Failed "+ task.getException(),Toast.LENGTH_SHORT).show();
//
//                    }
//                }
//            });
            boolean result =  myDB.RegisterUser(name_,designation_, phone_,email_,password_);
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


}