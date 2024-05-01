package com.hemant.ecofoodtrackerapp.ui.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hemant.ecofoodtrackerapp.adapters.HistoryAdapter;
import com.hemant.ecofoodtrackerapp.databinding.ActivityHistoryBinding;
import com.hemant.ecofoodtrackerapp.models.FoodOrderModel;
import com.hemant.ecofoodtrackerapp.util.AndroidUtil;

public class HistoryActivity extends AppCompatActivity {

    ActivityHistoryBinding binding;
    HistoryAdapter adapter;
    CollectionReference ref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ref = FirebaseFirestore.getInstance().collection("Orders");

        //pickup delivery type data
        FirestoreRecyclerOptions<FoodOrderModel> options =
                new FirestoreRecyclerOptions.Builder<FoodOrderModel>()
                        .setQuery(ref, FoodOrderModel.class)
                        .build();

        adapter = new HistoryAdapter(options, this,HistoryActivity.this,null,"History");
        binding.historyRV.setLayoutManager(new LinearLayoutManager(this));
        binding.historyRV.setAdapter(adapter);

        //check the food item present in list or not
        ref.addSnapshotListener(((value, error) -> {
            if(value != null && value.isEmpty()){
                showNoFoodText();
            }
        }));

        binding.historyBackBtn.setOnClickListener(v -> {
            AndroidUtil.setIntentToMainActivity(HistoryActivity.this);
        });
    }

    public void showNoFoodText() {
        try {
            binding.historyConstraint.setVisibility(View.VISIBLE);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AndroidUtil.setIntentToMainActivity(HistoryActivity.this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}