package com.hemant.ecofoodtrackerapp.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.hemant.ecofoodtrackerapp.databinding.ActivityFoodDescBinding;
import com.hemant.ecofoodtrackerapp.donor.ui.activities.DonorMainActivity;
import com.hemant.ecofoodtrackerapp.models.CartModel;
import com.hemant.ecofoodtrackerapp.models.FoodDataModel;
import com.hemant.ecofoodtrackerapp.models.UserDataModel;
import com.hemant.ecofoodtrackerapp.util.AndroidUtil;
import com.hemant.ecofoodtrackerapp.util.FirebaseUtil;
import com.squareup.picasso.Picasso;

public class FoodDescActivity extends AppCompatActivity {

    ActivityFoodDescBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFoodDescBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String donorId = getIntent().getStringExtra("donorId");
        String foodId = getIntent().getStringExtra("foodId");

        //check whether from it coming if coming from donor side then hide "order" btn, and "add to cart" btn
        if(getIntent().getStringExtra("comingFrom") != null){
            if(getIntent().getStringExtra("comingFrom").equals("DonorHistory")){
                binding.foodDescOrderBtn.setVisibility(View.GONE);
                binding.foodDescAddToCartBtn.setVisibility(View.GONE);
                binding.foodDescSendToChatBtn.setVisibility(View.GONE);
                binding.foodDescBackBtn.setOnClickListener(v ->{
                    startActivity(new Intent(this, DonorMainActivity.class));
                });
            }
            else if(getIntent().getStringExtra("comingFrom").equals("TrackOrder")){
                binding.foodDescOrderBtn.setVisibility(View.GONE);
                binding.foodDescAddToCartBtn.setVisibility(View.GONE);
                binding.foodDescSendToChatBtn.setVisibility(View.GONE);
                binding.foodDescBackBtn.setOnClickListener(v ->{
                    startActivity(new Intent(this, MainActivity.class));
                });
            }
        }
        else{
            binding.foodDescBackBtn.setOnClickListener(v ->{
                startActivity(new Intent(this, MainActivity.class));
            });
        }

        binding.foodDescSendToChatBtn.setOnClickListener(v -> {

            Intent intent = new Intent(this, MainChatActivity.class);
            FirebaseFirestore.getInstance().collection("Donors")
                    .document(donorId)
                    .get()
                    .addOnSuccessListener(v2 -> {
                        UserDataModel model = v2.toObject(UserDataModel.class);
                        if(model != null){
                            AndroidUtil.setIntentForUserDataModel(intent, model);
                            startActivity(intent);
                        }
                        else{
                            AndroidUtil.setToast(this,"Something went wrong");
                        }
                    })
                    .addOnFailureListener(v3 -> {
                        AndroidUtil.setToast(this,"Something went wrong");
                    });
        });

        FirebaseFirestore.getInstance().collection("Foods")
                .document(foodId)
                .get().addOnSuccessListener(v -> {
                    FoodDataModel model = v.toObject(FoodDataModel.class);
                    if (model != null) {
                        try{
                            binding.foodDescFoodName.setText(model.getItemFoodName());
                            Picasso.get().load(Uri.parse(model.getItemFoodImage())).into(binding.foodDescImage);
                            //add food name with the quantity
                            binding.foodDescQltyFoodNames.setText(model.getItemFoodName() + " x " + model.getItemQuantity());
                            binding.foodDescExpiryTime.setText(model.getItemExpiryTime());

                            //set on click listener for food add to cart button then it used for make order
                            binding.foodDescAddToCartBtn.setOnClickListener(v1 -> {
                                //set to cart
                                FirebaseUtil.setCartsDetails(new CartModel(model.getItemId(), model.getItemOrderStatus(), FirebaseUtil.getCurrentUserId()), binding.getRoot(), model.getItemId()+" "+FirebaseUtil.getCurrentUserId());
                            });
                        }
                        catch (Exception ignored){
                        }
                    } else {
                        AndroidUtil.setToast(this, "Something went wrong");
                    }
                })
                .addOnFailureListener(v -> {
                    AndroidUtil.setToast(this, "Something went wrong");
                });

        binding.foodDescOrderBtn.setOnClickListener(v ->{
            //send to order procedure
            Intent intent = new Intent(this, DeliveryOptionsActivity.class);
            intent.putExtra("donorId",donorId);
            intent.putExtra("foodId",foodId);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(getIntent().getStringExtra("comingFrom") != null){
            if(getIntent().getStringExtra("comingFrom").equals("DonorHistory")){
                    AndroidUtil.setIntentToDonorMainActivity(FoodDescActivity.this);
            }
            else if(getIntent().getStringExtra("comingFrom").equals("TrackOrder")){
                AndroidUtil.setIntentToMainActivity(FoodDescActivity.this);
            }
        }
        else{
            AndroidUtil.setIntentToMainActivity(FoodDescActivity.this);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}