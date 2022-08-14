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

public class LoginActivity extends AppCompatActivity {

    EditText email,password;
    Button login;
    TextView register;
    ProgressDialog progressDialog;
    FirebaseAuth fAuth;
    FirebaseUser fUser;
    String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        email=findViewById(R.id.email);
        password=findViewById(R.id.password);
        login=findViewById(R.id.login);
        register=findViewById(R.id.register);

        progressDialog=new ProgressDialog(this);
        fAuth=FirebaseAuth.getInstance();
        fUser=fAuth.getCurrentUser();

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegistrationActivity.class);
                startActivity(intent);
                LoginActivity.this.finish();
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PerformLogin();
            }
        });
    }

    private void PerformLogin() {
        String email_=email.getText().toString().trim();
        String password_=password.getText().toString().trim();
        String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

        if(email_.length()==0)
        {
            email.setError("Enter Email Id");
        }
        else if (!email_.matches(emailPattern))
        {
            email.setError("Enter valid EmailId");
        }
        else if(password_.length()==0)
        {
            password.setError("Enter Password");
        }
        else if(password_.length()<8)
        {
            password.setError("Password must be at least 8 Characters");
        }

        else{
            progressDialog.setMessage("Please wait while credentials are validated");
            progressDialog.setTitle("LOGIN");
            progressDialog.setCanceledOnTouchOutside(false);

            fAuth.signInWithEmailAndPassword(email_, password_).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful())
                    {
                        progressDialog.dismiss();
                        Toast.makeText(LoginActivity.this,"Login Successful",Toast.LENGTH_SHORT).show();
                        userId=fAuth.getCurrentUser().getUid();
                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                        startActivity(intent);
                       LoginActivity.this.finish();
                    }
                    else
                    {
                        Toast.makeText(LoginActivity.this,"Login Failed "+ task.getException(),Toast.LENGTH_SHORT).show();

                    }
                }
            });
        }
    }

}