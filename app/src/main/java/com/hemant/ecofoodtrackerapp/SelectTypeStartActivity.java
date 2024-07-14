package com.hemant.ecofoodtrackerapp;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.hemant.ecofoodtrackerapp.donor.ui.activities.DonorStartActivity;
import com.hemant.ecofoodtrackerapp.ui.activities.StartActivity;

public class SelectTypeStartActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_type_start);

        AppCompatButton userBtn = findViewById(R.id.userBtn);
        AppCompatButton donorBtn = findViewById(R.id.donorBtn);

        SharedPreferences sharedPreferences = getSharedPreferences("My_Pref",0);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        userBtn.setOnClickListener(v ->{
            Intent intent = new Intent(this, StartActivity.class);
            intent.putExtra("userType","Receiver");
            editor.putString("userType","Receiver");
            editor.apply();
            startActivity(intent);
        });

        donorBtn.setOnClickListener(v ->{
            Intent intent = new Intent(this, DonorStartActivity.class);
            intent.putExtra("userType","Donor");
            editor.putString("userType","Donor");
            editor.apply();
            startActivity(intent);
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}