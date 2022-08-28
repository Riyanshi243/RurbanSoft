package com.example.rurbansoft;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.bumptech.glide.load.resource.bitmap.RoundedCorners;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class MarkerInfoMap extends AppCompatActivity {
    FirebaseUser fUser;
    FirebaseFirestore fStore;
    ImageView image;
    ProgressDialog progressDialog;
    TextView state,district, cluster, timeStamp, Latitude, Longitude, gp, component, sub_component, phase, workStatus ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_info_map);
        fStore=FirebaseFirestore.getInstance();
        fUser= FirebaseAuth.getInstance().getCurrentUser();
        String userId=FirebaseAuth.getInstance().getCurrentUser().getUid();

        state=findViewById(R.id.state);
        district=findViewById(R.id.district);
        cluster=findViewById(R.id.cluster);
        timeStamp=findViewById(R.id.timeStamp);
        Latitude=findViewById(R.id.Latitude);
        Longitude=findViewById(R.id.Longitude);
        gp=findViewById(R.id.gp);
        component=findViewById(R.id.component);
        sub_component=findViewById(R.id.sub_component);
        phase=findViewById(R.id.phase);
        workStatus=findViewById(R.id.workStatus);
        image=findViewById(R.id.image);
        progressDialog=new ProgressDialog(this);

        progressDialog.setMessage("Please wait while data is loaded");
        progressDialog.setTitle("WORKITEM");
        progressDialog.setCanceledOnTouchOutside(false);

        String timeStampItem=getIntent().getExtras().getString("timeStamp");

        DocumentReference allWorkItem=fStore.collection("users").document(userId).collection("WorkItem").document(timeStampItem);

        allWorkItem.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists())
                {
                    state.setText(documentSnapshot.get("State").toString());
                    district.setText(documentSnapshot.get("District").toString());
                    cluster.setText(documentSnapshot.get("Cluster").toString());
                    timeStamp.setText(documentSnapshot.get("timeStamp").toString());
                    Latitude.setText(documentSnapshot.get("Latitude").toString());
                    Longitude.setText(documentSnapshot.get("Longitude").toString());
                    gp.setText(documentSnapshot.get("Gram Panchayat").toString());
                    component.setText(documentSnapshot.get("Component").toString());
                    sub_component.setText(documentSnapshot.get("Sub_component").toString());
                    phase.setText(documentSnapshot.get("Phase").toString());
                    workStatus.setText(documentSnapshot.get("WorkStatus").toString());

                    StorageReference storageReference = FirebaseStorage.getInstance().getReference(userId).child(timeStampItem);
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            RequestOptions requestOptions = new RequestOptions();
                            requestOptions = requestOptions.transforms(new CenterCrop(), new RoundedCorners(16));
                            Glide.with(MarkerInfoMap.this)
                                .load(uri).diskCacheStrategy(DiskCacheStrategy.ALL)
                                .error(R.drawable.ic_baseline_error_24)
                                .placeholder(R.drawable.ic_baseline_browser_updated_24).apply(requestOptions)
                                .into(image);
                            Toast.makeText(MarkerInfoMap.this,"set image ",Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(MarkerInfoMap.this,"No image "+e.getMessage(),Toast.LENGTH_SHORT).show();

                        }
                    });
                }
                progressDialog.show();
            }

        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MarkerInfoMap.this,"Something went Wrong "+e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        });
    }
}