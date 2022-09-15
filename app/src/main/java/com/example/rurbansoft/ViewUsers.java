package com.example.rurbansoft;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import java.util.ArrayList;

public class ViewUsers extends AppCompatActivity {

    private ArrayList<AllUsers> UserArrayList;
    private DatabaseHelper myDB;
    private UserAdapter userAdapter;
    private RecyclerView userRV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_users);

        UserArrayList = new ArrayList<>();
        myDB = new DatabaseHelper(this);

        UserArrayList = myDB.readUsers();
        userAdapter = new UserAdapter(UserArrayList, ViewUsers.this);
        userRV = findViewById(R.id.users);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(ViewUsers.this, RecyclerView.VERTICAL, false);
        userRV.setLayoutManager(linearLayoutManager);
        userRV.setAdapter(userAdapter);
    }
}