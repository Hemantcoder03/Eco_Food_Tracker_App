package com.hemant.ecofoodtrackerapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.hemant.ecofoodtrackerapp.R;
import com.hemant.ecofoodtrackerapp.SplashScreenActivity;
import com.hemant.ecofoodtrackerapp.databinding.ActivitySettingsBinding;

public class SettingsActivity extends AppCompatActivity {

    ActivitySettingsBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.settingsLogout.setOnClickListener(v ->{

            AlertDialog.Builder builder = new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setIcon(R.drawable.logout_icon)
                    .setTitle("Logout")
                    .setMessage("Do you really want to logout ?")
                    .setPositiveButton("Logout",((dialog, which) -> {
                        startActivity(new Intent(this, SplashScreenActivity.class));
                        finish();
                        //logout from device
                        FirebaseAuth.getInstance().signOut();
                    }))
                    .setNegativeButton("Cancel",((dialog, which) -> {
                        dialog.dismiss();
                    }));

            //create and show the dialog
            builder.create();
            builder.show();
        });
    }
}