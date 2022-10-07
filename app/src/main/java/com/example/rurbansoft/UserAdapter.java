package com.example.rurbansoft;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

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

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> { // variable for our array list and context
    private ArrayList<AllUsers> UserArrayList;
    private Context context;


    // constructor
    public UserAdapter(ArrayList<AllUsers> UserArrayList, Context context) {
        this.UserArrayList = UserArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_table_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        AllUsers modal = UserArrayList.get(position);
        holder.Name.setText(modal.getName());
        holder.Design.setText(modal.getDesign());
        holder.Phone.setText(modal.getPhone());
        holder.Email.setText(modal.getEmail());
        int sync= modal.getSync();
        if(sync==1)
            holder.sync_img.setVisibility(View.GONE);
        else {
            holder.sync_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                }
            });
        }

    }


    @Override
    public int getItemCount() {
        // returning the size of our array list
        return UserArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView Name, Design, Phone, Email;
        private ImageView sync_img;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.Name);
            Design = itemView.findViewById(R.id.Design);
            Phone = itemView.findViewById(R.id.Phone);
            Email=itemView.findViewById(R.id.email);
            sync_img=itemView.findViewById(R.id.sync_img);

        }
    }

}
