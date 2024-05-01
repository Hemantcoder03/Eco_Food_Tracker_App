package com.hemant.ecofoodtrackerapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.hemant.ecofoodtrackerapp.R;
import com.hemant.ecofoodtrackerapp.databinding.ActivityFoodPickUpBinding;

import java.util.Objects;

public class FoodPickUpActivity extends AppCompatActivity {

    ActivityFoodPickUpBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityFoodPickUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        if (Objects.requireNonNull(getIntent().getStringExtra("comingFrom")).equals("Pick Up")) {
            binding.orderLocationText.setText(R.string.your_order_is_waiting_for_you);
        } else {
            binding.orderLocationText.setText(R.string.your_order_is_on_the_way);
        }

        binding.foodPickUpGoBackBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}