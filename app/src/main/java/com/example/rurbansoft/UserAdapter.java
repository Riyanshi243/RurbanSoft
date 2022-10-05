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
    public static final String URL_SAVE_USER = "https://192.168.1.52/SqliteSync/saveUsers.php";
    public static final int USER_SYNCED_WITH_SERVER = 1;
    public static final int USER_NOT_SYNCED_WITH_SERVER = 0;

    // constructor
    public UserAdapter(ArrayList<AllUsers> UserArrayList, Context context) {
        this.UserArrayList = UserArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // on below line we are inflating our layout
        // file for our recycler view items.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_table_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        // on below line we are setting data
        // to our views of recycler view item.
        AllUsers modal = UserArrayList.get(position);
        holder.Name.setText(modal.getName());
        holder.Design.setText(modal.getDesign());
        holder.Phone.setText(modal.getPhone());
        holder.Email.setText(modal.getEmail());
        int sync= modal.getSync();
        if(sync==1)
            holder.sync_img.setVisibility(View.GONE);
        else {
//            holder.sync_img.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    holder.sync_img.setVisibility(View.GONE);
//
//                    HttpsTrustManager.allowAllSSL();
//
//                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL_SAVE_USER,
//                            new Response.Listener<String>() {
//                                @Override
//                                public void onResponse(String response) {
//                                    try {
//                                        JSONObject obj = new JSONObject(response);
//                                        if (!obj.getBoolean("error")) {
//
//                                        } else {
//                                        }
//                                    } catch (JSONException e) {
//                                        e.printStackTrace();
//                                    }
//                                }
//                            },
//                            new Response.ErrorListener() {
//                                @Override
//                                public void onErrorResponse(VolleyError error) {
//
//                                    Log.e("msg", " " + error);
//                                }
//                            }) {
//                        @Override
//                        protected Map<String, String> getParams() throws AuthFailureError {
//                            Map<String, String> params = new HashMap<>();
//                            params.put("Name", "name_");
//                            params.put("PhoneNumber", "phone_");
//                            params.put("EmailId", "email_");
//                            params.put("Designation", "designation_");
//                            params.put("Password", "password_");
//                            return params;
//                        }
//                    };
//
//                    //VolleySingleton.getInstance(UserAdapter.this).addToRequestQueue(stringRequest);
//                }
//
//            });
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
