package com.hemant.ecofoodtrackerapp.ui.activities;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;
import com.hemant.ecofoodtrackerapp.databinding.ActivityFeedBackBinding;
import com.hemant.ecofoodtrackerapp.models.FeedbackModel;
import com.hemant.ecofoodtrackerapp.util.AndroidUtil;
import com.hemant.ecofoodtrackerapp.util.FirebaseUtil;

public class FeedBackActivity extends AppCompatActivity {

    ActivityFeedBackBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityFeedBackBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.userFeedbackSendBtn.setOnClickListener(v ->{
            if(binding.userFeedbackMsg.getText().toString().isEmpty()){
                binding.userFeedbackMsg.setError("Please enter the feedback");
            }
            else{
                FirebaseFirestore.getInstance().collection("Feedback")
                        .document(FirebaseUtil.getCurrentUserId())
                        .set(new FeedbackModel(binding.userFeedbackMsg.getText().toString()))
                        .addOnSuccessListener(v2 ->{
                            Toast.makeText(this, "Thanks for sending Feedback.", Toast.LENGTH_SHORT).show();
                            AndroidUtil.setIntentToMainActivity(this);
                        })
                        .addOnFailureListener(v3 ->{
                            Toast.makeText(this, "Something went wrong, Please try again.", Toast.LENGTH_SHORT).show();
                        });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}