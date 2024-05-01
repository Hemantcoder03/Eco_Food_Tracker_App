package com.hemant.ecofoodtrackerapp.donor.ui.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hemant.ecofoodtrackerapp.databinding.ActivityDonorTrackOrderBinding;
import com.hemant.ecofoodtrackerapp.donor.adapters.DonorHistoryAdapter;
import com.hemant.ecofoodtrackerapp.models.FoodOrderModel;
import com.hemant.ecofoodtrackerapp.util.AndroidUtil;

public class DonorTrackOrderActivity extends AppCompatActivity {

    ActivityDonorTrackOrderBinding binding;
    CollectionReference ref;
    DonorHistoryAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityDonorTrackOrderBinding.inflate(getLayoutInflater());
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

        adapter = new DonorHistoryAdapter(options, this,null, DonorTrackOrderActivity.this,"DonorTrackOrder");
        binding.donorTrackOrderRV.setLayoutManager(new LinearLayoutManager(this));
        binding.donorTrackOrderRV.setAdapter(adapter);

        binding.donorTrackOrderBackBtn.setOnClickListener(v -> {
            AndroidUtil.setIntentToDonorMainActivity(DonorTrackOrderActivity.this);
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AndroidUtil.setIntentToDonorMainActivity(DonorTrackOrderActivity.this);
    }

    public void showNoFoodText() {
        try {
            binding.donorTrackOrderConstraint.setVisibility(View.VISIBLE);
        } catch (Exception ignored) {
        }
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