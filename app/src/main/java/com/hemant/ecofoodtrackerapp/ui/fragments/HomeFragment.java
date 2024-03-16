package com.hemant.ecofoodtrackerapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hemant.ecofoodtrackerapp.adapters.FoodListAdapter;
import com.hemant.ecofoodtrackerapp.databinding.FragmentHomeBinding;
import com.hemant.ecofoodtrackerapp.models.FoodDataModel;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

;

public class HomeFragment extends Fragment {

    FirebaseDatabase db;
    DatabaseReference ref;
    ArrayList<FoodDataModel> foodDataList;
    FoodListAdapter foodListAdapter;
    FragmentHomeBinding binding;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        //get the database reference
        ref = FirebaseDatabase.getInstance().getReference();

        binding.loadingAnimation.setVisibility(View.VISIBLE);
        binding.loadingAnimation.playAnimation();

        FirestoreRecyclerOptions<FoodDataModel> options =
                new FirestoreRecyclerOptions.Builder<FoodDataModel>()
                        .setQuery(FirebaseFirestore.getInstance().collection("Foods"), FoodDataModel.class)
                        .build();

        foodDataList = new ArrayList<>();
        binding.foodListRV.setLayoutManager(new LinearLayoutManager(requireContext()));

        binding.swipeRefreshBar.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                Timer timer = new Timer();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        checkVisibility();
                    }
                }, 0, 1000);

                foodListAdapter.reloadAdapter();
                binding.swipeRefreshBar.setRefreshing(false);
            }
        });

        foodListAdapter = new FoodListAdapter(options, requireActivity());
        binding.foodListRV.setAdapter(foodListAdapter);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkVisibility();
            }
        }, 0, 1000);

        // Inflate the layout for this fragment
        return view;
    }


    private void clearList() {
        foodDataList.clear();
        foodListAdapter.notifyDataSetChanged();
    }

    @Override
    public void onResume() {
        super.onResume();

        foodListAdapter.startListening();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkVisibility();
            }
        }, 0, 1000);
        foodListAdapter.reloadAdapter();
    }

    public void checkVisibility() {

        if (foodListAdapter.getSnapshots().isEmpty()) {
            try {
                if (binding.loadingAnimation != null) {
                    binding.loadingAnimation.setVisibility(View.VISIBLE);
                }
            } catch (Exception e) {

            }
        } else {
            requireActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    binding.loadingAnimation.cancelAnimation();
                    binding.loadingAnimation.setVisibility(View.GONE);
                }
            });
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        foodListAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        foodListAdapter.stopListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}