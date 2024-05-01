package com.hemant.ecofoodtrackerapp.donor.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hemant.ecofoodtrackerapp.databinding.FragmentDonorHistoryBinding;
import com.hemant.ecofoodtrackerapp.donor.adapters.DonorHistoryAdapter;
import com.hemant.ecofoodtrackerapp.models.FoodOrderModel;

public class DonorHistoryFragment extends Fragment {

    FragmentDonorHistoryBinding binding;
    View view;
    CollectionReference ref;
    DonorHistoryAdapter adapter;

    public DonorHistoryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDonorHistoryBinding.inflate(inflater,container,false);
        view = binding.getRoot();

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

        adapter = new DonorHistoryAdapter(options, requireActivity(),DonorHistoryFragment.this,null, "DonorHistory");
        binding.donorHistoryRV.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.donorHistoryRV.setAdapter(adapter);

        // Inflate the layout for this fragment
        return view;
    }

    public void showNoFoodText() {
        try {
            binding.noDonorHistoryFoodFoundText.setVisibility(View.VISIBLE);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}