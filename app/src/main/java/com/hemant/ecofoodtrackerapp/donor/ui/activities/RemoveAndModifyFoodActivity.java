package com.hemant.ecofoodtrackerapp.donor.ui.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hemant.ecofoodtrackerapp.R;
import com.hemant.ecofoodtrackerapp.databinding.ActivityRemoveAndModifyFoodBinding;
import com.hemant.ecofoodtrackerapp.donor.adapters.RemoveAndModifyFoodAdapter;
import com.hemant.ecofoodtrackerapp.models.FoodDataModel;
import com.hemant.ecofoodtrackerapp.util.AndroidUtil;

import java.util.ArrayList;
import java.util.Objects;

public class RemoveAndModifyFoodActivity extends AppCompatActivity {

    ActivityRemoveAndModifyFoodBinding binding;
    ArrayList<FoodDataModel> donorFoodList;
    ArrayList<String> donorFoodRefList;
    RemoveAndModifyFoodAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityRemoveAndModifyFoodBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        donorFoodList = new ArrayList<>();
        donorFoodRefList = new ArrayList<>();
        binding.removeFoodProgressBar.setVisibility(View.VISIBLE);

        binding.removeFoodBackBtn.setOnClickListener(v -> {
            binding.removeFoodProgressBar.setVisibility(View.GONE);
            finish();
        });
    }

    private void setFoodData() {

        //integrate all pages like "Remove food", "Modify food"

        FirebaseFirestore.getInstance().collection("Foods").get().addOnSuccessListener(v -> {
                    for (DocumentSnapshot snapshots : v.getDocuments()) {
                        donorFoodRefList.add(snapshots.getId());
                        FoodDataModel model = snapshots.toObject(FoodDataModel.class);
                        donorFoodList.add(model);
                    }

                    if (Objects.requireNonNull(getIntent().getStringExtra("comeFromBtn")).equals("Remove")) {
                        try {
                            binding.removeFoodPageTitle.setText(R.string.remove_food);
                            adapter = new RemoveAndModifyFoodAdapter(this, donorFoodList, donorFoodRefList, "Remove");
                        } catch (Exception ignored) {
                        }
                    } else if (Objects.requireNonNull(getIntent().getStringExtra("comeFromBtn")).equals("Modify")) {
                        try {
                            binding.removeFoodPageTitle.setText(R.string.modify_food);
                            adapter = new RemoveAndModifyFoodAdapter(this, donorFoodList, donorFoodRefList, "Modify");
                        } catch (Exception ignored) {
                        }
                    }

                    if(adapter != null){
                        binding.removeFoodRV.setLayoutManager(new LinearLayoutManager(this));
                        binding.removeFoodRV.setAdapter(adapter);
                        binding.removeFoodProgressBar.setVisibility(View.GONE);
                    }

                    //if it is empty then show "no food" text
                    if (donorFoodList.isEmpty()) {
                        try{
                            binding.removeFoodNoFoodText.setVisibility(View.VISIBLE);
                        }catch (Exception ignored){}
                    }
                })
                .addOnFailureListener(v -> {
                    AndroidUtil.setToast(this, "Please check your Internet Connection");
                    binding.removeFoodProgressBar.setVisibility(View.GONE);
                    binding.removeFoodNoFoodText.setVisibility(View.VISIBLE);
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
        //clear the previous data of an arraylist and new updated data into it
        donorFoodList.clear();
        donorFoodRefList.clear();
        setFoodData();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        binding.removeFoodProgressBar.setVisibility(View.GONE);
        binding = null;
    }
}