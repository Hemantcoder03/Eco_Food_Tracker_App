package com.hemant.ecofoodtrackerapp.ui.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.hemant.ecofoodtrackerapp.databinding.ActivityUpdateProfileBinding;
import com.hemant.ecofoodtrackerapp.util.AndroidUtil;
import com.hemant.ecofoodtrackerapp.util.FirebaseUtil;

import java.util.Objects;

public class UpdateProfileActivity extends AppCompatActivity {

    ActivityUpdateProfileBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityUpdateProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //set the data to update
        binding.updateProfileUsername.setText(getIntent().getStringExtra("name"));
        binding.updateProfilePhone.setText(getIntent().getStringExtra("phone"));
        binding.updateProfileAddress.setText(getIntent().getStringExtra("address"));

        binding.updateProfileBackBtn.setOnClickListener(v -> {
            AndroidUtil.setIntentToMainActivity(UpdateProfileActivity.this);
        });

        binding.updateProfileBtn.setOnClickListener(v -> {
            checkData();
        });
    }

    private void checkData() {

        binding.updateProfileUsernameTV.setVisibility(View.GONE);
        binding.updateProfilePhoneTV.setVisibility(View.GONE);
        binding.updateProfileAddressTV.setVisibility(View.GONE);

        String username = Objects.requireNonNull(binding.updateProfileUsername.getText()).toString();
        String phone = Objects.requireNonNull(binding.updateProfilePhone.getText()).toString();
        String address = Objects.requireNonNull(binding.updateProfileAddress.getText()).toString();

        if (Objects.requireNonNull(username).isEmpty()) {
            binding.updateProfileUsernameTV.setText("Please enter valid username");
            binding.updateProfileUsernameTV.setVisibility(View.VISIBLE);
        } else if (Objects.requireNonNull(phone).isEmpty()) {
            binding.updateProfilePhoneTV.setText("Please enter valid Phone Number");
            binding.updateProfilePhoneTV.setVisibility(View.VISIBLE);
        } else if (Objects.requireNonNull(address).isEmpty()) {
            binding.updateProfileAddressTV.setText("Please enter valid Address");
            binding.updateProfileAddressTV.setVisibility(View.VISIBLE);
        } else {
            FirebaseUtil.updateCurrentUserDetails(username, phone, address);
            Toast.makeText(this, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
            AndroidUtil.setIntentToMainActivity(UpdateProfileActivity.this);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AndroidUtil.setIntentToMainActivity(UpdateProfileActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}