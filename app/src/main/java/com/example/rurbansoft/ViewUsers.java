package com.example.rurbansoft;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ViewUsers extends AppCompatActivity {

    private ArrayList<AllUsers> UserArrayList;
    private DBHelper myDB;
    private UserAdapter userAdapter;
    private RecyclerView userRV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users);

        UserArrayList = new ArrayList<>();
        myDB = new DBHelper(this);
        UserArrayList = myDB.readUsers();

        userAdapter = new UserAdapter(UserArrayList, ViewUsers.this);
        userRV = findViewById(R.id.users);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewUsers.this, RecyclerView.VERTICAL, false);
        userRV.setLayoutManager(linearLayoutManager);
        userRV.setAdapter(userAdapter);
    }

}