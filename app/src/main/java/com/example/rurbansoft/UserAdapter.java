package com.example.rurbansoft;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

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
    }

    @Override
    public int getItemCount() {
        // returning the size of our array list
        return UserArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView Name, Design, Phone, Email;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            Name = itemView.findViewById(R.id.Name);
            Design = itemView.findViewById(R.id.Design);
            Phone = itemView.findViewById(R.id.Phone);
            Email=itemView.findViewById(R.id.email);

        }
    }

}
