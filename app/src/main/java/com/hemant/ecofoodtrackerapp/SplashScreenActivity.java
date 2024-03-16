package com.hemant.ecofoodtrackerapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.hemant.ecofoodtrackerapp.donor.ui.activities.DonorMainActivity;
import com.hemant.ecofoodtrackerapp.ui.activities.MainActivity;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        SharedPreferences sharedPreferences = getSharedPreferences("My_Pref",0);

        new Handler().postDelayed(() -> {

            //current user check if already authenticated then send to main page
            FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
            if (currentUser != null) {

                if(sharedPreferences.getString("userType", "").equals("Receiver")){
                    startActivity(new Intent(this, MainActivity.class));
                }
                else{
                    startActivity(new Intent(this, DonorMainActivity.class));
                }
                finish();
            }
            else{
                startActivity(new Intent(this, SelectTypeStartActivity.class));
            }

        },3000);

    }
}