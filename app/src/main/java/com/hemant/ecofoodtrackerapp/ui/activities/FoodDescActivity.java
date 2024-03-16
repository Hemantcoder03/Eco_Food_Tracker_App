package com.hemant.ecofoodtrackerapp.ui.activities;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.hemant.ecofoodtrackerapp.databinding.ActivityFoodDescBinding;
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

        binding.foodDescBackBtn.setOnClickListener(v -> {

            startActivity(new Intent(this, MainActivity.class));
        });

        binding.foodDescSendToChatBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainChatActivity.class);
            FirebaseFirestore.getInstance().collection("Donors")
                    .document("klvf3uYi05RCfPZukZMLISEOP9k2")
//                    .document(Objects.requireNonNull(getIntent().getStringExtra("donorId")))
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

        String foodId = getIntent().getStringExtra("foodId");

        FirebaseFirestore.getInstance().collection("Foods")
                .document(String.valueOf(foodId))
                .get().addOnSuccessListener(v -> {
                    FoodDataModel model = v.toObject(FoodDataModel.class);
                    if (model != null) {
                        binding.foodName.setText(model.getItemFoodName());
                        Picasso.get().load(Uri.parse(model.getItemFoodImage())).into(binding.foodDescImage);
                        //add food name with the quantity
                        binding.foodDescQltyFoodNames.setText(model.getItemFoodName() + " x " + model.getItemQuantity());
                        binding.foodDescExpiryTime.setText(model.getItemExpiryTime());

                        //set on click listener for food add to cart button then it used for make order
                        binding.foodDescAddToCartBtn.setOnClickListener(v1 -> {
                            //set to cart
                            FirebaseUtil.setCartsDetails(new CartModel(model.getItemId(), model.getItemOrderStatus(), FirebaseUtil.getCurrentUserId()), binding.getRoot(), model.getItemId());
                        });
                    } else {
                        AndroidUtil.setToast(this, "Something went wrong");
                    }
                })
                .addOnFailureListener(v -> {
                    AndroidUtil.setToast(this, "Something went wrong");
                });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, MainActivity.class));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}