package com.hemant.ecofoodtrackerapp.donor.ui.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.hemant.ecofoodtrackerapp.databinding.FragmentDonorHomeBinding;
import com.hemant.ecofoodtrackerapp.donor.ui.activities.AddFoodActivity;
import com.hemant.ecofoodtrackerapp.donor.ui.activities.RemoveAndModifyFoodActivity;

public class DonorHomeFragment extends Fragment {

    FragmentDonorHomeBinding binding;
    View view;
    public DonorHomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentDonorHomeBinding.inflate(inflater,container,false);
        view = binding.getRoot();

        binding.donorAddFoodBtn.setOnClickListener(v ->{
            Intent intent = new Intent(requireActivity(), AddFoodActivity.class);
            intent.putExtra("comeFromBtn","Add");
            startActivity(intent);
        });

        binding.donorRemoveFoodBtn.setOnClickListener(v ->{
            Intent intent = new Intent(requireActivity(), RemoveAndModifyFoodActivity.class);
            intent.putExtra("comeFromBtn","Remove");
            startActivity(intent);
        });

        binding.donorModifyFoodBtn.setOnClickListener(v ->{
            Intent intent = new Intent(requireActivity(), RemoveAndModifyFoodActivity.class);
            intent.putExtra("comeFromBtn","Modify");
            startActivity(intent);
        });

        // Inflate the layout for this fragment
        return view;
    }
}