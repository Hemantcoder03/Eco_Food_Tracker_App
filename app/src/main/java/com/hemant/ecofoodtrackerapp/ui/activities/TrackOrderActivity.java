package com.hemant.ecofoodtrackerapp.ui.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hemant.ecofoodtrackerapp.adapters.HistoryAdapter;
import com.hemant.ecofoodtrackerapp.databinding.ActivityTrackOrderBinding;
import com.hemant.ecofoodtrackerapp.models.FoodOrderModel;

public class TrackOrderActivity extends AppCompatActivity {

    ActivityTrackOrderBinding binding;
    CollectionReference ref;
    HistoryAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTrackOrderBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ref = FirebaseFirestore.getInstance().collection("Orders");

        //pickup delivery type data
        FirestoreRecyclerOptions<FoodOrderModel> options =
                new FirestoreRecyclerOptions.Builder<FoodOrderModel>()
                        .setQuery(ref, FoodOrderModel.class)
                        .build();

        //check the food item present in list or not
        ref.addSnapshotListener(((value, error) -> {
            if(value != null && value.isEmpty()){
                showNoFoodText();
            }
        }));

        adapter = new HistoryAdapter(options, this,null,TrackOrderActivity.this,"TrackOrder");
        binding.trackOrderRV.setLayoutManager(new LinearLayoutManager(this));
        binding.trackOrderRV.setAdapter(adapter);

        binding.trackOrderBackBtn.setOnClickListener(v -> {
            finish();
        });
    }

    public void showNoFoodText() {
        try {
            binding.trackOrderConstraint.setVisibility(View.VISIBLE);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
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
    protected void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}