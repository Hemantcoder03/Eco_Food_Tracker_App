package com.hemant.ecofoodtrackerapp.ui.activities;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hemant.ecofoodtrackerapp.R;
import com.hemant.ecofoodtrackerapp.databinding.ActivityFoodHomeDeliveryBinding;
import com.hemant.ecofoodtrackerapp.models.FoodDataModel;
import com.hemant.ecofoodtrackerapp.models.FoodOrderModel;
import com.hemant.ecofoodtrackerapp.models.LocationModel;
import com.hemant.ecofoodtrackerapp.models.UserDataModel;
import com.hemant.ecofoodtrackerapp.util.AndroidUtil;
import com.hemant.ecofoodtrackerapp.util.FirebaseUtil;

import java.util.HashMap;
import java.util.Map;

public class FoodHomeDeliveryActivity extends AppCompatActivity {

    ActivityFoodHomeDeliveryBinding binding;
    float deliveryCharge = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFoodHomeDeliveryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String foodId = getIntent().getStringExtra("foodId");
        String donorId = getIntent().getStringExtra("donorId");

        binding.orderHomeDeliveryBackBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, DeliveryOptionsActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });

        //set the user data like name and address or phone is available
        FirebaseUtil.getUserDetails(FirebaseUtil.getCurrentUserId()).get()
                .addOnSuccessListener(v -> {

                    UserDataModel model = v.toObject(UserDataModel.class);
                    if (model != null) {
                        binding.orderHomeDeliveryUserName.setText(model.getUserName());

                        if (!model.getUserPhone().isEmpty()) {
                            binding.orderHomeDeliveryUserNumber.setText(model.getUserPhone());
                        }

                        if (!model.getUserAddress().isEmpty()) {
                            binding.orderHomeDeliveryUserAddress.setText(model.getUserAddress());
                        }

                        setChargeDetails(foodId, model);
                    } else {
                        AndroidUtil.setToast(this, "Something wrong");
                    }
                })
                .addOnFailureListener(v -> {
                    AndroidUtil.setToast(this, "Please check your internet connection");
                });

        binding.orderHomeDeliveryUserNumber.setOnFocusChangeListener((v, v1) -> {
            //if focused then setfocusable and else release focus
            if (v1) {
                binding.orderHomeDeliveryUserNumber.setBackground(getDrawable(R.drawable.focusable_background));
            } else {
                binding.orderHomeDeliveryUserNumber.setBackground(getDrawable(R.drawable.non_focusable_background));
            }
        });

        binding.orderHomeDeliveryUserAddress.setOnFocusChangeListener((v, v1) -> {
            //if focused then setfocusable and else release focus
            if (v1) {
                binding.orderHomeDeliveryUserAddress.setBackground(getDrawable(R.drawable.focusable_background));
            } else {
                binding.orderHomeDeliveryUserAddress.setBackground(getDrawable(R.drawable.non_focusable_background));
            }
        });

        binding.orderHomeDeliveryCV.setOnClickListener(v -> {
            //release focus when click on outside of edittext
            releaseEtFocus();
        });

        binding.orderHomeDeliveryView.setOnClickListener(v -> {
            //release focus when click on outside of edittext
            releaseEtFocus();
        });

        binding.orderHomeDeliveryCharge.setOnClickListener(v -> {
            //release focus when click on outside of edittext
            releaseEtFocus();
        });

        binding.orderHomeDeliveryOrderBtn.setOnClickListener(v -> {
            //release focus when click on outside of edittext
            releaseEtFocus();

            //check the phone and address is update or present or not if not present then show error
            if(binding.orderHomeDeliveryUserNumber.getText().toString().isEmpty()){
                AndroidUtil.setToast(this,"Please enter mobile number");
            }else if(binding.orderHomeDeliveryUserAddress.getText().toString().isEmpty()){
                AndroidUtil.setToast(this,"Please enter address");
            }
            else{
                setOrderDetails(foodId,donorId);
            }
        });
    }

    private void setOrderDetails(String foodId, String donorId) {

        FoodOrderModel model = new FoodOrderModel();

        //place order of food
        model.setFoodId(foodId);
        model.setDonorId(donorId);
        model.setOrderId("order" + foodId);
        model.setOrderUserId(FirebaseUtil.getCurrentUserId());    //get current user id because he/she was ordering food hence needed him/her id
        model.setOrderTime(Timestamp.now());
        model.setDeliveryType("Home Delivery");
        model.setDeliveryCharge(deliveryCharge + "");
        model.setDeliveryAddress(binding.orderHomeDeliveryUserAddress.getText().toString());
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
                    updateUserDetails(userId);
                })
                .addOnFailureListener(v -> {
                    AndroidUtil.setToast(this, "Something went wrong");
                });
    }

    private void updateUserDetails(String userId) {
        //update the status and also the orderUid to know who is order the food
        Map<String, Object> map = new HashMap<>();
        map.put("userAddress", binding.orderHomeDeliveryUserAddress.getText().toString());
        map.put("userPhone", binding.orderHomeDeliveryUserNumber.getText().toString());

        FirebaseFirestore.getInstance().collection("Receivers")
                .document(userId)
                .update(map)
                .addOnSuccessListener(v -> {
                    setPickUpIntent();
                })
                .addOnFailureListener(v -> {
                    AndroidUtil.setToast(this, "Something went wrong");
                });
    }

    private void setPickUpIntent() {
        new Handler().postDelayed(() -> {
            Intent intent = new Intent(this, FoodPickUpActivity.class);
            intent.putExtra("comingFrom","Home Delivery");
            startActivity(intent);
        }, 1000);
    }

    private void setChargeDetails(String foodId, UserDataModel user) {
        if (foodId != null) {
            //set the charge to the delivery of food
            FirebaseFirestore.getInstance().collection("Foods")
                    .document(foodId)
                    .get()
                    .addOnSuccessListener(v -> {
                        FoodDataModel food = v.toObject(FoodDataModel.class);

                        //find the distance between two locations
                        LocationModel locationModel = food.getItemDonorNearbyLoc();
                        Location donorFoodLocation = new Location("Food Location");
                        donorFoodLocation.setLatitude(locationModel.getLatitude());
                        donorFoodLocation.setLongitude(locationModel.getLongitude());

                        Location userLocation = new Location("User Location");
                        userLocation.setLatitude(user.getCurrentLocation().getLatitude());
                        userLocation.setLongitude(user.getCurrentLocation().getLongitude());

                        float kilometers = userLocation.distanceTo(donorFoodLocation) / 1000;

                        //set the charge add with 10 as default
                        if (kilometers <= 1) {
                            //charge set "10" when less then 1 km is distance
                            deliveryCharge = 10;
                            binding.orderHomeDeliveryCharge.setText("₹"+deliveryCharge);
                        } else {
                            if (kilometers >= 100) {
                                //charge set "not deliverable" when more then 100 km is distance
                                deliveryCharge = 0;
                                binding.orderHomeDeliveryCharge.setText("Not Deliverable");
                                binding.orderHomeDeliveryOrderBtn.setClickable(false);
                                binding.orderHomeDeliveryOrderBtn.setBackgroundColor(getResources().getColor(R.color.gray));
                            } else {
                                //charge set according to distance in else condition
                                deliveryCharge = kilometers * 10;
                                binding.orderHomeDeliveryCharge.setText("₹"+deliveryCharge);
                            }
                        }
                    })
                    .addOnFailureListener(v -> {
                        AndroidUtil.setToast(this, "Please check your internet connection");
                    });
        } else {
            AndroidUtil.setToast(this, "Something went wrong");
        }
    }

    private void releaseEtFocus() {
        binding.orderHomeDeliveryUserNumber.clearFocus();
        binding.orderHomeDeliveryUserAddress.clearFocus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}