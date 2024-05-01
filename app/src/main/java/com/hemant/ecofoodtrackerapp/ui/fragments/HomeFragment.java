package com.hemant.ecofoodtrackerapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.hemant.ecofoodtrackerapp.adapters.FoodListAdapter;
import com.hemant.ecofoodtrackerapp.databinding.FragmentHomeBinding;
import com.hemant.ecofoodtrackerapp.models.FoodDataModel;
import com.hemant.ecofoodtrackerapp.util.AndroidUtil;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class HomeFragment extends Fragment {
    FoodListAdapter foodListAdapter;
    FragmentHomeBinding binding;
    CollectionReference query;
    ArrayList<FoodDataModel> foodList = new ArrayList<>();

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        binding.loadingAnimation.setVisibility(View.VISIBLE);
        binding.loadingAnimation.playAnimation();

        query = FirebaseFirestore.getInstance().collection("Foods");

//        FirestoreRecyclerOptions<FoodDataModel> options =
//                new FirestoreRecyclerOptions.Builder<FoodDataModel>()
//                        .setQuery(query, FoodDataModel.class)
//                        .build();

        binding.foodListRV.setLayoutManager(new LinearLayoutManager(requireContext()));

        binding.swipeRefreshBar.setOnRefreshListener(() -> {
            Timer timer = new Timer();
            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    checkVisibility();
                }
            }, 0, 1000);

            foodListAdapter.notifyDataSetChanged();
            binding.swipeRefreshBar.setRefreshing(false);
        });

//        foodListAdapter = new FoodListAdapter(options, requireActivity(), HomeFragment.this);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    AndroidUtil.setToast(requireActivity(), "Please check your internet connection");
                }

                if (value != null) {
                    foodList.clear();
                    for (QueryDocumentSnapshot snapshots : value) {
                        FoodDataModel model = snapshots.toObject(FoodDataModel.class);
                        foodList.add(model);
                    }
                    foodListAdapter = new FoodListAdapter(requireActivity(), HomeFragment.this, foodList);
                    binding.foodListRV.setAdapter(foodListAdapter);
                } else {
                    AndroidUtil.setToast(requireActivity(), "Please check your internet connection");
                }
            }
        });

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkVisibility();
            }
        }, 0, 1000);

//        binding.searchFilterBtn.setOnClickListener(v ->{
//            Dialog dialog = new Dialog(requireActivity());
//            dialog.setContentView(R.layout.filter_layout);
//            dialog.show();
//        });

        // Search listener
        binding.searchFood.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ArrayList<FoodDataModel> list = new ArrayList<>();
                if(!newText.isEmpty()){
                    list = foodListAdapter.search(newText);
                }

                if(list.isEmpty()){
                    binding.noFoodFoundText.setVisibility(View.VISIBLE);
                }
                foodListAdapter = new FoodListAdapter(requireActivity(), HomeFragment.this, list);
                binding.foodListRV.setAdapter(foodListAdapter);

                if(newText.isEmpty()){
                    foodListAdapter = new FoodListAdapter(requireActivity(), HomeFragment.this, foodList);
                    binding.foodListRV.setAdapter(foodListAdapter);
                }
                foodListAdapter.notifyDataSetChanged();
                return false;
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    public void setNoFoundTextVisible() {
        try {
            binding.noFoodFoundText.setVisibility(View.VISIBLE);
        } catch (Exception ignored) {
        }
    }

    public void setNoFoundTextGone() {
        try {
            binding.noFoodFoundText.setVisibility(View.GONE);
        } catch (Exception ignored) {
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkVisibility();
            }
        }, 0, 1000);
    }

    public void checkVisibility() {
        try {
            if (foodList.isEmpty()) {

                //if food list is empty
                setNoFoundTextVisible();
                requireActivity().runOnUiThread(() -> {
                    try {
                        binding.loadingAnimation.cancelAnimation();
                        binding.loadingAnimation.setVisibility(View.GONE);
                    } catch (Exception ignored) {
                    }
                });

            }
        } catch (Exception ignored) {

        }

    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}