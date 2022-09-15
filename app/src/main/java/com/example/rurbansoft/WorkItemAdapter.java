package com.example.rurbansoft;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

public class WorkItemAdapter extends RecyclerView.Adapter<WorkItemAdapter.ViewHolder>{

    private ArrayList<AllWorkItems> WorkItemArrayList;
    private Context context;
    DatabaseHelper myDB;

    // constructor

    public WorkItemAdapter(ArrayList<AllWorkItems> WorkItemArrayList, Context context) {
        this.WorkItemArrayList = WorkItemArrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public WorkItemAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.workitem_table_item, parent, false);
        return new WorkItemAdapter.ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(@NonNull WorkItemAdapter.ViewHolder holder, int position) {

        AllWorkItems modal = WorkItemArrayList.get(position);
        holder.State.setText(modal.getState());
        holder.District.setText(modal.getDistrict());
        holder.Cluster.setText(modal.getCluster());
        holder.gp.setText(modal.getGp());
        holder.Components.setText(modal.getComponents());
        holder.subComponent.setText(modal.getSubComponents());
        holder.status.setText(modal.getStatus());
        holder.phase.setText(modal.getPhase());
        holder.datetime.setText(modal.getDateTime());
//
//        myDB = new DatabaseHelper(WorkItemAdapter.this);
//        Cursor cursor = myDB.getImage(modal.getDateTime());
//        @SuppressLint("Range") byte[] image = cursor.getBlob(cursor.getColumnIndex("IMAGE"));
////
////        byte[] image=modal.getImg();
//
//        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
//        bmOptions.inJustDecodeBounds = true;
//
//        Bitmap bitmap = BitmapFactory.decodeByteArray(image, 0, image.length, bmOptions);
//
//        int photoW = bmOptions.outWidth;
//        int photoH = bmOptions.outHeight;
//
//        int targetW = photoW / 8; //image to reduce to 1/10 of original
//        int targetH = photoH / 8;
//
//        bitmap = BitmapFactory.decodeByteArray(image, 0, image.length);
//        Matrix matrix = new Matrix();
//
//        matrix.postRotate(90);
//        Bitmap photo = Bitmap.createScaledBitmap(bitmap, targetW, targetH, false);
//        Bitmap rotatedBitmap = Bitmap.createBitmap(photo, 0, 0, photo.getWidth(), photo.getHeight(), matrix, true);
//        holder.img.setImageBitmap(rotatedBitmap);



    }

    @Override
    public int getItemCount() {
        // returning the size of our array list
        return WorkItemArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        private TextView State, District, Cluster, gp,Components, subComponent, status, phase, latitude, longitude, datetime;
        private ImageView img;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            State = itemView.findViewById(R.id.state);
            District = itemView.findViewById(R.id.district);
            Cluster = itemView.findViewById(R.id.cluster);
            gp=itemView.findViewById(R.id.gp);
            Components=itemView.findViewById(R.id.component);
            subComponent=itemView.findViewById(R.id.sub_component);
            status=itemView.findViewById(R.id.workStatus);
            phase=itemView.findViewById(R.id.Phase);
            datetime=itemView.findViewById(R.id.timeStamp);
            img=itemView.findViewById(R.id.workItemImg);

        }
    }

}
