package com.hemant.ecofoodtrackerapp.ui.activities;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.hemant.ecofoodtrackerapp.R;
import com.hemant.ecofoodtrackerapp.util.AndroidUtil;

public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        ImageView aboutBackBtn = findViewById(R.id.aboutBackBtn);
        aboutBackBtn.setOnClickListener(v ->{
            AndroidUtil.setIntentToMainActivity(AboutActivity.this);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AndroidUtil.setIntentToMainActivity(AboutActivity.this);
    }
}