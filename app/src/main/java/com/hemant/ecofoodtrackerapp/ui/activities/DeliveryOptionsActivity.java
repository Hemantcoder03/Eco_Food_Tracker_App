package com.hemant.ecofoodtrackerapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hemant.ecofoodtrackerapp.databinding.ActivityDeliveryOptionsBinding;
import com.hemant.ecofoodtrackerapp.models.FoodOrderModel;
import com.hemant.ecofoodtrackerapp.util.AndroidUtil;
import com.hemant.ecofoodtrackerapp.util.FirebaseUtil;

import java.util.HashMap;
import java.util.Map;

public class DeliveryOptionsActivity extends AppCompatActivity {

    ActivityDeliveryOptionsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDeliveryOptionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String donorId = getIntent().getStringExtra("donorId");
        String foodId = getIntent().getStringExtra("foodId");

        binding.foodPickUpImage.setOnClickListener(v -> {
            orderPickUpFood(foodId, donorId);
        });

        binding.foodPickUpText.setOnClickListener(v -> {
            orderPickUpFood(foodId, donorId);
        });

        binding.foodHomeDeliveryImage.setOnClickListener(v -> {
            setHomeDeliveryIntent();
        });

        binding.foodHomeDeliveryText.setOnClickListener(v -> {
            setHomeDeliveryIntent();
        });
    }

    private void orderPickUpFood(String foodId, String donorId) {

        //set the order model to make order
        FoodOrderModel model = new FoodOrderModel();

        if (foodId != null && donorId != null) {
            FirebaseFirestore.getInstance().collection("Foods")
                    .document(foodId).get()
                    .addOnSuccessListener(v -> {
                        model.setFoodId(foodId);
                        model.setDonorId(donorId);
                        model.setOrderId("order" + foodId);
                        model.setOrderUserId(FirebaseUtil.getCurrentUserId());    //get current user id because he/she was ordering food hence needed him/her id
                        model.setOrderTime(Timestamp.now());
                        model.setDeliveryType("Pick Up");
                        model.setDeliveryCharge("0");
                        model.setDeliveryAddress("");
                        model.setStatus("ordered");

                        FirebaseUtil.setOrderFoodRef(model.getOrderId())
                                .set(model).addOnSuccessListener(v2 -> {
                                    AndroidUtil.setSuccessSnackBar(binding.getRoot(), "Food Ordered Successfully");
                                    //update the food order status hence it is not visible to another
                                    updateFoodDetails(model.getOrderUserId(), model.getFoodId());
                                })
                                .addOnFailureListener(v2 -> {
                                    AndroidUtil.setFailedSnackBar(binding.getRoot(), "Something went wrong");
                                });
                    })
                    .addOnFailureListener(v -> {
                        AndroidUtil.setToast(this, "Something went wrong");
                    });
        }
    }

    private void updateFoodDetails(String userId, String foodId) {
        //update the status and also the orderUid to know who is order the food
        Map<String, Object> map = new HashMap<>();
        map.put("itemOrderStatus", "ordered");
        map.put("itemOrderUid", userId);

        FirebaseFirestore.getInstance().collection("Foods")
                .document(foodId)
                .update(map)
                .addOnSuccessListener(v -> {
                    setPickUpIntent();
                })
                .addOnFailureListener(v -> {
                   AndroidUtil.setToast(this,"Something went wrong");
                });
    }

    private void setPickUpIntent() {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, FoodPickUpActivity.class);
            intent.putExtra("comingFrom","Pick Up");
            startActivity(intent);
        }, 1000);
    }

    private void setHomeDeliveryIntent() {
        Intent intent = new Intent(this, FoodHomeDeliveryActivity.class);
        intent.putExtra("donorId", getIntent().getStringExtra("donorId"));
        intent.putExtra("foodId", getIntent().getStringExtra("foodId"));
        startActivity(intent);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}