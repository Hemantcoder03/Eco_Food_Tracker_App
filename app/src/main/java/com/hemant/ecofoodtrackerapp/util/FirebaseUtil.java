package com.hemant.ecofoodtrackerapp.util;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hemant.ecofoodtrackerapp.models.UserDataModel;

import java.util.HashMap;
import java.util.Map;

public class FirebaseUtil {

    public static String getCurrentUserId(){
        return FirebaseAuth.getInstance().getUid();
    }

    public static DocumentReference getCurrentUserDetails(){
        return FirebaseFirestore.getInstance().collection("Receivers").document(getCurrentUserId());
    }

    public static void setCurrentUserDetails(UserDataModel user){
        getCurrentUserDetails().set(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    AndroidUtil.setLog("checkUser","Successful");
                }
                else{
                    AndroidUtil.setLog("checkUser","Failed");
                }
            }
        });
    }

    public static void updateCurrentUserDetails(String userName, String userPhone, String userAddress){

        Map<String, Object> data = new HashMap<>();
        data.put("userName", userName);
        data.put("userPhone", userPhone);
        data.put("userAddress", userAddress);

        getCurrentUserDetails().update(data);
    }

    public static DocumentReference getChatroomReference(String chatroomId){
        return FirebaseFirestore.getInstance().collection("classrooms").document(chatroomId);
    }

    public static CollectionReference getChatroomMessageReference(String chatroomId){
        return getChatroomReference(chatroomId).collection("chats");
    }

    public static String getChatroomId(String userId1, String userId2){
        if(userId1.hashCode() < userId2.hashCode()){
            return userId1+"_"+userId2;
        }
        else {
            return userId2+"_"+userId1;
        }
    }
}
