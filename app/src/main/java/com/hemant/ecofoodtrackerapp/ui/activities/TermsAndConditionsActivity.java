package com.hemant.ecofoodtrackerapp.ui.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.hemant.ecofoodtrackerapp.databinding.ActivityTermsAndConditionsBinding;
import com.hemant.ecofoodtrackerapp.util.AndroidUtil;

public class TermsAndConditionsActivity extends AppCompatActivity {

    ActivityTermsAndConditionsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTermsAndConditionsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.conditionsBackBtn.setOnClickListener(v ->{
            AndroidUtil.setIntentToMainActivity(TermsAndConditionsActivity.this);
        });

        binding.conditionsAcceptBtn.setOnClickListener(v ->{
            handleVisibility();
        });

        binding.conditionsDeclineBtn.setOnClickListener(v ->{
            handleVisibility();
        });
    }

    private void handleVisibility(){
        binding.conditionsAcceptBtn.setVisibility(View.GONE);
        binding.conditionsDeclineBtn.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AndroidUtil.setIntentToMainActivity(TermsAndConditionsActivity.this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}