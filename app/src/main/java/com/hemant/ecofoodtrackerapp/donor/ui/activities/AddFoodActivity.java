package com.hemant.ecofoodtrackerapp.donor.ui.activities;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.hemant.ecofoodtrackerapp.R;
import com.hemant.ecofoodtrackerapp.databinding.ActivityAddFoodBinding;
import com.hemant.ecofoodtrackerapp.models.FoodDataModel;
import com.hemant.ecofoodtrackerapp.models.LocationModel;
import com.hemant.ecofoodtrackerapp.models.UserDataModel;
import com.hemant.ecofoodtrackerapp.util.AndroidUtil;
import com.hemant.ecofoodtrackerapp.util.FirebaseUtil;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

public class AddFoodActivity extends AppCompatActivity {

    ActivityAddFoodBinding binding;
    private final int PICK_IMAGE_REQUEST = 101;
    private final int LOCATION_CODE = 102;
    private Uri filePath;
    //used for store the images
    FirebaseStorage storage;
    StorageReference storageReference;
    //set the details of food
    FirebaseFirestore firestore;
    //food image uri
    String foodImageUri;
    Bitmap bitmap = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddFoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
        firestore = FirebaseFirestore.getInstance();

        //this code run when click on add food btn
        if (getIntent().getStringExtra("comeFromBtn").equals("Add")) {
            binding.addFoodBackBtn.setOnClickListener(v -> {
                startActivity(new Intent(this, DonorMainActivity.class));
                finish();
            });

            binding.donorAddFoodBtn.setOnClickListener(v -> {
                addFoodToDB(v);
            });
        }
        //this code run when click on modify food item
        else {
            FirebaseFirestore.getInstance().collection("Foods")
                    .document(Objects.requireNonNull(getIntent().getStringExtra("foodRef"))).get()
                    .addOnSuccessListener(v -> {
                        FoodDataModel model = v.toObject(FoodDataModel.class);
                        if (model != null) {
                            binding.addFoodText.setText("Modify Food Here");
                            binding.donorAddFoodNameEt.setText(model.getItemFoodName());
                            binding.donorAddFoodQuantityEt.setText(String.valueOf(model.getItemQuantity()));
                            Picasso.get().load(Uri.parse(model.getItemFoodImage()))
                                    .into(binding.donorAddSelectedImage);
                            binding.donorAddFoodDesc.setText(model.getItemShortDesc());
                            binding.donorFoodExpiryTime.setText(model.getItemExpiryTime());
                            binding.donorAddFoodBtn.setText(R.string.modify_food);
                            binding.addFoodBackBtn.setOnClickListener(v1 -> {
                                Intent intent = new Intent(AddFoodActivity.this, DonorMainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                            });

                            binding.donorAddFoodBtn.setOnClickListener(v2 -> {
                                modifyFoodToDB(v2);
                            });
                        } else {
                            AndroidUtil.setToast(this, "Something went wrong");
                        }
                    })
                    .addOnFailureListener(v -> {
                        AndroidUtil.setToast(this, "Something went wrong");
                    });
        }

        //same for both conditions
        binding.donorAddSelectedImage.setOnClickListener(v -> {
            selectImage();
        });
        binding.donorAddFoodImage.setOnClickListener(v -> {
            selectImage();
        });
    }

    private void addFoodToDB(View v) {

        if (binding.donorAddFoodNameEt.getText().toString().equals("")) {
            binding.donorAddFoodNameEt.setError("Enter the FOOD NAME");
            AndroidUtil.setToast(this, "Enter the food name");
        } else if (binding.donorAddFoodQuantityEt.getText().toString().equals("")) {
            binding.donorAddFoodQuantityEt.setError("Enter the FOOD QUANTITY");
            AndroidUtil.setToast(this, "Enter the food quantity");
        } else if (getFoodImageUri() == null) {
            binding.donorAddFoodImage.setError("Add food Image");
            AndroidUtil.setToast(this, "Please add Food Image");
        } else {
            FoodDataModel model = new FoodDataModel();
            model.setItemFoodName(binding.donorAddFoodNameEt.getText().toString());
            model.setItemQuantity(Integer.parseInt(binding.donorAddFoodQuantityEt.getText().toString()));
            model.setItemShortDesc(binding.donorAddFoodDesc.getText().toString());
            model.setItemOrderStatus("not ordered");
            model.setItemDonateDate(new SimpleDateFormat("dd MMM yyyy").format(new Date()));
            model.setItemRateCount("0");
            model.setItemExpiryTime(binding.donorFoodExpiryTime.getText().toString());
            model.setItemFoodImage(getFoodImageUri());
            model.setItemDonorNearbyLoc(getCurrentLocation());

            //get unique id for store food details and pair contain timestamp which id for it and also ref
            Pair<String, DocumentReference> foodIdPair = FirebaseUtil.getFoodDocumentRefDetails();
            String foodUniqueId = foodIdPair.first;

            model.setItemId(foodUniqueId + "" + FirebaseUtil.getCurrentUserId());
            FirebaseUtil.getCurrentDonorDetails().get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    UserDataModel userDataModel = task.getResult().toObject(UserDataModel.class);
                    if (userDataModel != null) {
                        model.setItemDonorProfileId(userDataModel.getUserId());
                        Map<String, Object> foodDetails = AndroidUtil.setFoodDetails(model);
                        foodIdPair.second.set(foodDetails).addOnCompleteListener(v1 -> {
                            if (v1.isSuccessful()) {

                                //set the notification for receiver
                                sendFoodAddedNotification();
                                AndroidUtil.setSuccessSnackBar(v,"Food Added Successfully");
                                //set delay for loading main activity to show snack-bar
                                new Handler().postDelayed(() -> {
                                    Intent intent = new Intent(AddFoodActivity.this, DonorMainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                },2000);
                            } else {
                                AndroidUtil.setFailedSnackBar(v,"Something went wrong");
                            }
                        });
                    } else {
                        AndroidUtil.setToast(this, "Something went wrong");
                    }
                } else {
                    AndroidUtil.setToast(this, "Something went wrong");
                }
            });
        }
    }

    private void sendFoodAddedNotification() {

        String cloudApiKey = "AAAAIxPADkM:APA91bGLyOEfRgqGdA-70Mi8eRK2eZmjJEJ1oPrSPVkznjR1M622q7D9TqorG7vNspphlvpxyE5BIsobuJWt4jz2p17ZmBmEz41-PPI2vmqgc-fVDyOm58xfIQ2NXEjAPAt6Z9xpa64q";
        String topic = "NewFoodAdded";

        RequestQueue mRequestQue = Volley.newRequestQueue(this);

        JSONObject json = new JSONObject();
        try {
            json.put("to", "/topics/" + topic);
            JSONObject notificationObj = new JSONObject();
            notificationObj.put("title", "Eco Food Tracker App");
            notificationObj.put("body", "New Food Added, Order Now!!");
            json.put("notification", notificationObj);

            String URL = "https://fcm.googleapis.com/fcm/send";
            JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, URL,
                    json,
                    response -> Log.d("checkError", "onResponse: " + response.toString()),
                    error -> Log.d("checkError", "onError: " + error.networkResponse)
            ) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> header = new HashMap<>();
                    header.put("content-type", "application/json");
                    header.put("authorization", "key=" + cloudApiKey);
                    return header;
                }
            };

            mRequestQue.add(request);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public void modifyFoodToDB(View v2) {
        if (binding.donorAddFoodNameEt.getText().toString().equals("")) {
            binding.donorAddFoodNameEt.setError("Enter the FOOD NAME");
            AndroidUtil.setToast(this, "Enter the food name");
        } else if (binding.donorAddFoodQuantityEt.getText().toString().equals("")) {
            binding.donorAddFoodQuantityEt.setError("Enter the FOOD QUANTITY");
            AndroidUtil.setToast(this, "Enter the food quantity");
        } else {
            //add map which can update the database
            Map<String, Object> map = new HashMap<>();
            map.put("itemFoodName", binding.donorAddFoodNameEt.getText().toString());
            map.put("itemQuantity", Integer.parseInt(binding.donorAddFoodQuantityEt.getText().toString()));
            map.put("itemShortDesc", binding.donorAddFoodDesc.getText().toString());
            map.put("itemExpiryTime", binding.donorFoodExpiryTime.getText().toString());
            if (bitmap != null) {
                map.put("itemFoodImage", getFoodImageUri());
            }
            map.put("itemDonorNearbyLoc", getCurrentLocation());
            FirebaseFirestore.getInstance().collection("Foods")
                    .document(Objects.requireNonNull(getIntent().getStringExtra("foodRef")))
                    .update(map)
                    .addOnSuccessListener(v -> {
                        AndroidUtil.setSuccessSnackBar(v2,"Food updated successfully");
                        //set delay for loading main activity to show snack-bar
                        new Handler().postDelayed(() ->{
                                Intent intent = new Intent(AddFoodActivity.this, DonorMainActivity.class);
                                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(intent);
                        },2000);
                    })
                    .addOnFailureListener(v -> {
                        AndroidUtil.setFailedSnackBar(v2, "Something went wrong");
                    });
        }
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 100);
        }
    }

    private void selectImage() {
        checkPermissions();
        //define the intent to open file explorer selector and select image
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select food image here..."), PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            filePath = data.getData();

            //show progress dialog for showing it is uploading
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("Uploading...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            StorageReference ref = storageReference.child("food_images/" + UUID.randomUUID().toString());
            ref.putFile(filePath).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                                                                try {
                                                                    bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                                                                } catch (IOException e) {
                                                                    throw new RuntimeException(e);
                                                                }
                                                                binding.donorAddSelectedImage.setImageBitmap(bitmap);
                                                                progressDialog.dismiss();
                                                                ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                                            @Override
                                                                            public void onSuccess(Uri uri) {
                                                                                setFoodImageUri(uri.toString());
                                                                            }
                                                                        })
                                                                        .addOnFailureListener(new OnFailureListener() {
                                                                            @Override
                                                                            public void onFailure(@NonNull Exception e) {
                                                                                e.printStackTrace();
                                                                            }
                                                                        });
                                                            }
                                                        }

            ).addOnFailureListener(v -> {
                progressDialog.dismiss();
                AndroidUtil.setToast(this, "Something went wrong");
            });
        }
    }

    private void setFoodImageUri(String uri) {
        this.foodImageUri = uri;
    }

    private String getFoodImageUri() {
        return this.foodImageUri;
    }

    private LocationModel getCurrentLocation() {
        LocationModel currentLocation = new LocationModel();

        FusedLocationProviderClient locationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, LOCATION_CODE);
        }
        locationProviderClient.getLastLocation().addOnSuccessListener(v -> {
            Log.d("checkError", v.getLatitude() + " " + v.getLongitude());
            currentLocation.setLatitude(v.getLatitude());
            currentLocation.setLongitude(v.getLongitude());
        }).addOnFailureListener(v -> {
            Log.d("checkError", v.getMessage() + "");
            AndroidUtil.setToast(this, "Please allow location permission");
        });
        return currentLocation;
    }
}