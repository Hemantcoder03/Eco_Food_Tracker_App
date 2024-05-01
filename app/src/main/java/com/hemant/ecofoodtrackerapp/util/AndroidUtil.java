package com.hemant.ecofoodtrackerapp.util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.snackbar.Snackbar;
import com.hemant.ecofoodtrackerapp.R;
import com.hemant.ecofoodtrackerapp.donor.ui.activities.DonorMainActivity;
import com.hemant.ecofoodtrackerapp.models.FoodDataModel;
import com.hemant.ecofoodtrackerapp.models.UserDataModel;
import com.hemant.ecofoodtrackerapp.ui.activities.MainActivity;

import java.util.HashMap;
import java.util.Map;

public class AndroidUtil implements ConnectionReceiver.ReceiverListener{

    public static void setToast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void setLog(String key,String msg){
        Log.d(key,msg);
    }

    public static void setSuccessSnackBar(View view, String message){
        Snackbar snackbar = Snackbar.make(view,message,Snackbar.LENGTH_LONG);
        snackbar.setAction("Done",v -> {
           snackbar.dismiss();
        });
        snackbar.setBackgroundTint(view.getResources().getColor(R.color.green));
        snackbar.show();
    }

    public static void setFailedSnackBar(View view, String message){
        Snackbar snackbar = Snackbar.make(view,message,Snackbar.LENGTH_LONG);
        snackbar.setAction("Dismiss",v -> {
            snackbar.dismiss();
        });
        snackbar.setBackgroundTint(view.getResources().getColor(R.color.red));
        snackbar.show();
    }

    public static void setIntentForUserDataModel(Intent intent, UserDataModel model){

        intent.putExtra("userName",model.getUserName());
        intent.putExtra("userEmail",model.getUserEmail());
        intent.putExtra("userId",model.getUserId());
    }

    public static UserDataModel getIntentForUserDataModel(Intent intent){

        UserDataModel userDataModel = new UserDataModel();
        userDataModel.setUserName(intent.getStringExtra("userName"));
        userDataModel.setUserEmail(intent.getStringExtra("userEmail"));
        userDataModel.setUserId(intent.getStringExtra("userId"));
        return userDataModel;
    }

    public static Boolean checkConnection(Context context){

        // initialize intent filter
        IntentFilter intentFilter = new IntentFilter();

        // add action
        intentFilter.addAction("android.new.conn.CONNECTIVITY_CHANGE");

        // register receiver
        context.registerReceiver(new ConnectionReceiver(),intentFilter);

        // Initialize listener
        ConnectionReceiver.Listener = (ConnectionReceiver.ReceiverListener) context;

        // Initialize connectivity manager
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

        // Initialize network info
        NetworkInfo networkInfo = manager.getActiveNetworkInfo();

        // get connection status
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }

    @Override
    public void onNetworkChange(boolean isConnected) {

    }

    public static Map<String, Object> setFoodDetails(FoodDataModel foodDataModel) {
        Map<String, Object> data = new HashMap<>();
        data.put("itemFoodName", foodDataModel.getItemFoodName());
        data.put("itemDonorProfileId", foodDataModel.getItemDonorProfileId());
        data.put("itemDonorNearbyLoc", foodDataModel.getItemDonorNearbyLoc());
        data.put("itemDonateDate", foodDataModel.getItemDonateDate());
        data.put("itemRateCount", foodDataModel.getItemRateCount());
        data.put("itemDonorProfileImg", foodDataModel.getItemDonorProfileImg());
        data.put("itemOrderStatus", foodDataModel.getItemOrderStatus());
        data.put("itemOrderUid", foodDataModel.getItemOrderUid());
        data.put("itemShortDesc", foodDataModel.getItemShortDesc());
        data.put("itemExpiryTime", foodDataModel.getItemExpiryTime());
        data.put("itemQuantity", foodDataModel.getItemQuantity());
        data.put("itemFoodImage", foodDataModel.getItemFoodImage());
        data.put("itemId", foodDataModel.getItemId());
        return data;
    }

    public static void setIntentToMainActivity(Context context){
        Intent intent = new Intent(context, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void setIntentToDonorMainActivity(Context context){
        Intent intent = new Intent(context, DonorMainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
