package com.hemant.ecofoodtrackerapp.ui.activities;

import android.os.Bundle;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.hemant.ecofoodtrackerapp.R;
import com.hemant.ecofoodtrackerapp.util.AndroidUtil;

public class HelpAndSupportActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_help_and_support);

        ImageButton helpAndSupportBackBtn = findViewById(R.id.helpAndSupportBackBtn);

        helpAndSupportBackBtn.setOnClickListener(v ->{
            AndroidUtil.setIntentToMainActivity(this);
        });
    }
}