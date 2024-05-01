package com.hemant.ecofoodtrackerapp.util;

import android.app.Activity;
import android.content.Context;
import android.util.Pair;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hemant.ecofoodtrackerapp.models.CartModel;
import com.hemant.ecofoodtrackerapp.models.LocationModel;
import com.hemant.ecofoodtrackerapp.models.UserDataModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class FirebaseUtil {

    //used for both
    public static String getCurrentUserId() {
        return FirebaseAuth.getInstance().getUid();
    }

    //User
    public static DocumentReference getCurrentUserDetails() {
        return FirebaseFirestore.getInstance().collection("Receivers").document(getCurrentUserId());
    }

    public static DocumentReference getUserDetails(String userId) {
        return FirebaseFirestore.getInstance().collection("Receivers").document(userId);
    }

    public static void setCurrentUserDetails(UserDataModel user) {
        getCurrentUserDetails().set(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                AndroidUtil.setLog("checkUser", "Successful");
            } else {
                AndroidUtil.setLog("checkUser", "Failed");
            }
        });
    }

    public static void setCartsDetails(CartModel food, View view, String ref) {
        FirebaseFirestore.getInstance().collection("Carts").document(ref).set(food).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                AndroidUtil.setSuccessSnackBar(view, "Food added to cart");
            } else {
                AndroidUtil.setFailedSnackBar(view, "Something went wrong");
            }
        });
    }

    public static Boolean updateCurrentUserDetails(String userName, String userPhone, String userAddress) {

        Map<String, Object> data = new HashMap<>();
        data.put("userName", userName);
        data.put("userPhone", userPhone);
        data.put("userAddress", userAddress);

        return getCurrentUserDetails().update(data).isSuccessful();
    }

    public static void setUserCurrentLocation(Context context, LocationModel location) {

        Map<String, Object> map = new HashMap<>();
        map.put("currentLocation", location);
        FirebaseUtil.getCurrentUserDetails().update(map).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                AndroidUtil.setToast(context, "Location added successfully");
            } else {
                AndroidUtil.setToast(context, "Please check your internet connection");
            }
        });
    }

    //order
    public static DocumentReference setOrderFoodRef(String orderId){

        return FirebaseFirestore.getInstance().collection("Orders").document(orderId);
    }


    //chat room
    public static DocumentReference getChatroomReference(String chatroomId) {
        return FirebaseFirestore.getInstance().collection("ChatRooms").document(chatroomId);
    }

    public static CollectionReference getChatroomMessageReference(String chatroomId) {
        return getChatroomReference(chatroomId).collection("chats");
    }

    public static String getChatroomId(String userId1, String userId2) {
        if (userId1.hashCode() < userId2.hashCode()) {
            return userId1 + "_" + userId2;
        } else {
            return userId2 + "_" + userId1;
        }
    }



    //Donor
    public static DocumentReference getCurrentDonorDetails() {
        return FirebaseFirestore.getInstance().collection("Donors").document(getCurrentUserId());
    }

    public static DocumentReference getDonorDetails(String userId) {
        return FirebaseFirestore.getInstance().collection("Donors").document(userId);
    }

    public static void setCurrentDonorDetails(UserDataModel user) {
        getCurrentDonorDetails().set(user).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                AndroidUtil.setLog("checkUser", "Successful");
            } else {
                AndroidUtil.setLog("checkUser", "Failed");
            }
        });
    }

    public static void updateCurrentDonorDetails(String userName, String userPhone, String userAddress, View view) {

        Map<String, Object> data = new HashMap<>();
        data.put("userName", userName);
        data.put("userPhone", userPhone);
        data.put("userAddress", userAddress);

        getCurrentDonorDetails().update(data)
                .addOnSuccessListener(v -> {
                    AndroidUtil.setSuccessSnackBar(view, "Updated Successful");
                })
                .addOnFailureListener(v -> {
                    AndroidUtil.setFailedSnackBar(view, "Something went wrong");
                });
    }

    //return pair time and there ref
    public static Pair<String, DocumentReference> getFoodDocumentRefDetails() {
        String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        return new Pair<>(timeStamp, FirebaseFirestore.getInstance().collection("Foods").document(timeStamp+""+getCurrentUserId()));
    }

    public static void removeFoodUsingRef(Context context, View view, String ref) {
        FirebaseFirestore.getInstance().collection("Foods").document(ref).delete().addOnSuccessListener(v -> {
                    AndroidUtil.setSuccessSnackBar(view, "Food Removed Successfully");
                    ((Activity) context).finish();
                })
                .addOnFailureListener(v -> {
                    AndroidUtil.setFailedSnackBar(view, "Something went wrong");
                });
    }

    public static void removeCartFoodUsingRef(View view, String ref) {
        FirebaseFirestore.getInstance().collection("Carts").document(ref+" "+FirebaseUtil.getCurrentUserId()).delete().addOnSuccessListener(v -> {
                    AndroidUtil.setSuccessSnackBar(view, "Food removed from cart successfully");
                })
                .addOnFailureListener(v -> {
                    AndroidUtil.setFailedSnackBar(view, "Something went wrong");
                });
    }

    public static void setDonorCurrentLocation(Context context, LocationModel location) {

        Map<String, Object> map = new HashMap<>();
        map.put("currentLocation", location);
        FirebaseUtil.getCurrentDonorDetails().update(map).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                AndroidUtil.setToast(context, "Location added successfully");
            } else {
                AndroidUtil.setToast(context, "Please check your internet connection");
            }
        });
    }
}
