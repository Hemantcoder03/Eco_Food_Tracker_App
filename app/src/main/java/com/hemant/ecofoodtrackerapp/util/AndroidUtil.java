package com.hemant.ecofoodtrackerapp.util;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.Toast;

import com.hemant.ecofoodtrackerapp.ConnectionReceiver;
import com.hemant.ecofoodtrackerapp.models.UserDataModel;

public class AndroidUtil implements ConnectionReceiver.ReceiverListener{

    public static void setToast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }

    public static void setLog(String key,String msg){
        Log.d(key,msg);
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
}
