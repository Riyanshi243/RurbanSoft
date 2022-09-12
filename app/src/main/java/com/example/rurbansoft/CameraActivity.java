package com.example.rurbansoft;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class CameraActivity extends AppCompatActivity
{
    String state, district, cluster, gp, component, sub_component, phase, workStatus, userId, timeStamp, email, currentPhotoPath;;
    Button capture, retake, save;
    ImageView captureImage;
    FirebaseAuth fAuth;
    FirebaseUser fUser;
    FirebaseFirestore fStore;
    StorageReference storageReference;
    ProgressDialog progressDialog;
    Uri imageuri;
    DatabaseHelper myDB;


    private static final int CAMERA_REQUEST = 1888;
    private static final int MY_CAMERA_PERMISSION_CODE = 100;
    double lat, lon;
    int status1=0, status2=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        state = getIntent().getExtras().getString("state");
        district = getIntent().getExtras().getString("district");
        cluster = getIntent().getExtras().getString("cluster");
        gp = getIntent().getExtras().getString("gp");
        component = getIntent().getExtras().getString("component");
        sub_component = getIntent().getExtras().getString("sub_component");
        phase = getIntent().getExtras().getString("phase");
        workStatus = getIntent().getExtras().getString("status");

        captureImage=findViewById(R.id.captureImage);
        capture=findViewById(R.id.capture);
        retake=findViewById(R.id.retake);
        save=findViewById(R.id.save);
        fAuth=FirebaseAuth.getInstance();
        fStore=FirebaseFirestore.getInstance();
        fUser=fAuth.getCurrentUser();
        progressDialog = new ProgressDialog(this);
        storageReference = FirebaseStorage.getInstance().getReference();
        //retake and save button to appear only when image is clicked
        retake.setVisibility(View.GONE);
        save.setVisibility(View.GONE);
        myDB = new DatabaseHelper(this);

        File photoFile = null;
        try {
            photoFile = createImageFile();
        } catch (IOException ex) {

        }
        //location permission
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        boolean statusOfGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

        if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

        }
        else {
            if (statusOfGPS == true) {
                @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                if (location != null) {
                    lat = location.getLatitude();
                    lon = location.getLongitude();

                } else {
                    AlertDialog.Builder a_builder = new AlertDialog.Builder(CameraActivity.this);
                    a_builder.setMessage("Unable to fetch Location..!! Try again later")
                            .setTitle("Network Error!!")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(Settings.ACTION_SETTINGS));
                                }
                            });
                    a_builder.show();
                }
            } else {
                AlertDialog.Builder a_builder = new AlertDialog.Builder(CameraActivity.this);
                a_builder.setMessage("GPS is OFF, Turn it ON and Try again !..")
                        .setTitle("GPS Error!!")
                        .setCancelable(false)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                            }
                        });
                a_builder.show();
            }
        }

            capture.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {

                if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                {
                    requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                }
                else
                {
                    Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                    if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                        // Create the File where the photo should go
                        File photoFile = null;
                        try {
                            photoFile = createImageFile();
                        } catch (IOException ex) {

                        }
                        // Continue only if the File was successfully created
                        if (photoFile != null) {
                            Uri photoURI = FileProvider.getUriForFile(CameraActivity.this,
                                    "com.example.rurbansoft.fileprovider",
                                    photoFile);
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                            startActivityForResult(cameraIntent, CAMERA_REQUEST);
                        }
                    }
                }
            }
        });

        retake.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View view) {
                    if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                    {
                        requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_PERMISSION_CODE);
                    }
                    else
                    {
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
                            // Create the File where the photo should go
                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                            } catch (IOException ex) {

                            }
                            // Continue only if the File was successfully created
                            if (photoFile != null) {
                                Uri photoURI = FileProvider.getUriForFile(CameraActivity.this,
                                        "com.example.rurbansoft.fileprovider",
                                        photoFile);
                                cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                startActivityForResult(cameraIntent, CAMERA_REQUEST);
                            }
                        }
                    }
            }
        });

        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveWorkItem();
            }
        });

    }
    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        currentPhotoPath = image.getAbsolutePath();

        return image;
    }

    private void saveWorkItem() {
        Date t= Calendar.getInstance().getTime();
        timeStamp=t.toString();
        progressDialog.setMessage("Please wait while workItem image is uploaded");
        progressDialog.setTitle("WORK-ITEM");
        progressDialog.setCanceledOnTouchOutside(false);
        Toast.makeText(CameraActivity.this,"WorkItem is uploading.. Please wait",Toast.LENGTH_LONG).show();


//        //uploading data to firebase
//        DocumentReference allWorkItem=fStore.collection("users").document(userId).collection("WorkItem").document(timeStamp);
//        Map<String, Object> workItem=new HashMap<>();
//        uploadPic(imageuri);//uploading image to firebase
//
//        workItem.put("Email",email);
//        workItem.put("State",state );
//        workItem.put("District", district);
//        workItem.put("Cluster",cluster);
//        workItem.put("Gram Panchayat",gp);
//        workItem.put("Component",component);
//        workItem.put("Sub_component",sub_component);
//        workItem.put("Phase",phase);
//        workItem.put("WorkStatus",workStatus);
//        workItem.put("Latitude",lat);
//        workItem.put("Longitude",lon);
//        workItem.put("timeStamp", timeStamp);
//
//        allWorkItem.set(workItem).addOnSuccessListener(new OnSuccessListener<Void>() {
//            @Override
//            public void onSuccess(Void unused) {
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                progressDialog.dismiss();
//                Toast.makeText(CameraActivity.this,"WorkItem uploading failed",Toast.LENGTH_SHORT).show();
//            }
//        });

        int targetW = captureImage.getWidth();
        int targetH = captureImage.getHeight();

        // Get the dimensions of the bitmap
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(currentPhotoPath, bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;

        // Determine how much to scale down the image
        int scaleFactor = Math.min(photoW/targetW, photoH/targetH);

        // createa matrix for the manipulation
        Matrix matrix = new Matrix();
        // resize the bit map
        matrix.postScale(photoW/targetW, photoH/targetH);
        // rotate the Bitmap
        matrix.postRotate(90);

        // Decode the image file into a Bitmap sized to fill the View
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;

        Bitmap photo = BitmapFactory.decodeFile(currentPhotoPath, bmOptions);

        //saving values to Database

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
        byte[] byteArray = stream.toByteArray();

        myDB = new DatabaseHelper(this);

        boolean insertedData = myDB.insertData(state,district,cluster,gp,component,sub_component,phase,lat,lon,workStatus,timeStamp,byteArray);


        if(insertedData   == true)
        {
            AlertDialog.Builder a_builder = new AlertDialog.Builder(CameraActivity.this);
            a_builder.setMessage("Click YES to view workitem on map!..")
                    .setTitle("Workitem Updated!!")
                    .setCancelable(false)
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //dialog.cancel();
                            Intent intent = new Intent(CameraActivity.this,MapsActivity.class);
                            startActivity(intent);
                        }
                    })
                    .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                            Intent intent = new Intent(CameraActivity.this,MainActivity.class);
                            startActivity(intent);
                        }
                    });
            a_builder.show();

        }
        else {
            Toast.makeText(CameraActivity.this, "Error Please Try Again!!", Toast.LENGTH_SHORT).show();
            AlertDialog.Builder a_builder = new AlertDialog.Builder(CameraActivity.this);
            a_builder.setMessage("Something went Wrong Try Again Later!..")
                    .setTitle("Workitem Not Updated!!")
                    .setCancelable(false)
                    .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //dialog.cancel();
                            finish();
                        }
                    });

            a_builder.show();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == MY_CAMERA_PERMISSION_CODE)
        {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                Toast.makeText(this, "Camera permission granted", Toast.LENGTH_LONG).show();
                Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(cameraIntent, CAMERA_REQUEST);
            }
            else
            {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == Activity.RESULT_OK) {
            File f = new File(currentPhotoPath);
            captureImage.setImageURI(Uri.fromFile(f));
            imageuri = Uri.fromFile(f);

            capture.setVisibility(View.GONE);
            retake.setVisibility(View.VISIBLE);
            save.setVisibility(View.VISIBLE);

            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            boolean statusOfGPS = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

            if (ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION,
                        android.Manifest.permission.ACCESS_COARSE_LOCATION}, 101);

            }
            else {
                if (statusOfGPS == true) {
                    @SuppressLint("MissingPermission") Location location = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                    if (location != null) {
                        lat = location.getLatitude();
                        lon = location.getLongitude();

                    }
                    else {
                        AlertDialog.Builder a_builder = new AlertDialog.Builder(CameraActivity.this);
                        a_builder.setMessage("Unable to fetch Location..!! Try again later")
                                .setTitle("Network Error!!")
                                .setCancelable(false)
                                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        a_builder.show();
                    }
                }
                else{
                    AlertDialog.Builder a_builder = new AlertDialog.Builder(CameraActivity.this);
                    a_builder.setMessage("GPS is OFF, Turn it ON and Try again !..")
                            .setTitle("GPS Error!!")
                            .setCancelable(false)
                            .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                                }
                            });
                    a_builder.show();
                }

            }
        }
    }


//    public void uploadPic(Uri imageuri) {
//
//        if(imageuri!=null)
//        {
//            progressDialog2.setMessage("Please wait while workItem image is uploaded");
//            progressDialog2.setTitle("WORK-ITEM");
//            progressDialog2.setCanceledOnTouchOutside(false);
//            FirebaseStorage storage = FirebaseStorage.getInstance();
//
//            StorageReference storageRef=storage.getReference(userId);
//
//            StorageReference fileReference = storageRef.child(timeStamp);
//            fileReference.putFile(imageuri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
//                @Override
//                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
//                    status2=1;
//                    progressDialog2.dismiss();
//                    progressDialog.dismiss();
//                    Toast.makeText(CameraActivity.this,"WorkItem uploading successful",Toast.LENGTH_SHORT).show();
//                    Intent intent = new Intent(CameraActivity.this, MapActivity.class);
//                    startActivity(intent);
//                }
//            }).addOnFailureListener(new OnFailureListener() {
//                @Override
//                public void onFailure(@NonNull Exception e) {
//                    progressDialog2.dismiss();
//                    progressDialog.dismiss();
//                    Toast.makeText(CameraActivity.this,e.getMessage(),Toast.LENGTH_LONG).show();
//                }
//            });
//            progressDialog2.show();
//
//        }
//
//        else
//        {
//            Toast.makeText(this,"No Image selected",Toast.LENGTH_LONG).show();
//        }
//
//    }
}