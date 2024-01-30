package com.hemant.ecofoodtrackerapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.hemant.ecofoodtrackerapp.R;
import com.hemant.ecofoodtrackerapp.adapters.FoodListAdapter;
import com.hemant.ecofoodtrackerapp.models.FoodDataModel;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

;

public class HomeFragment extends Fragment {

    FirebaseDatabase db;
    DatabaseReference ref;
    SearchView searchFood;
    ImageButton searchFilterBtn;
    RecyclerView foodListRV;
    ArrayList<FoodDataModel> foodDataList;
    FoodListAdapter foodListAdapter;
    SwipeRefreshLayout refreshBar;
    //    ProgressBar progressBar;
    LottieAnimationView loadingAnimation;

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);


        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //get the database reference
        ref = FirebaseDatabase.getInstance().getReference();

        searchFood = view.findViewById(R.id.searchFood);
        searchFilterBtn = view.findViewById(R.id.searchFilterBtn);
        foodListRV = view.findViewById(R.id.foodListRV);
        refreshBar = view.findViewById(R.id.swipeRefreshBar);
//        progressBar = view.findViewById(R.id.progressBar);
        loadingAnimation = view.findViewById(R.id.loadingAnimation);

//        progressBar.setVisibility(View.VISIBLE);
        loadingAnimation.setVisibility(View.VISIBLE);
        loadingAnimation.playAnimation();

        FirebaseRecyclerOptions<FoodDataModel> options =
                new FirebaseRecyclerOptions.Builder<FoodDataModel>()
                        .setQuery(ref.child("FoodList"), FoodDataModel.class)
                        .build();

        foodDataList = new ArrayList<>();
        foodListRV.setLayoutManager(new LinearLayoutManager(requireContext()));

        refreshBar.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
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
                refreshBar.setRefreshing(false);
            }
        });

        foodListAdapter = new FoodListAdapter(options);
        foodListRV.setAdapter(foodListAdapter);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkVisibility();
            }
        }, 0, 10);

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
        }, 0, 10);
        foodListAdapter.reloadAdapter();
    }

    public void checkVisibility() {
        if (foodListAdapter.getSnapshots().isEmpty()) {
//            progressBar.setVisibility(View.VISIBLE);
            loadingAnimation.setVisibility(View.VISIBLE);
        } else {
//            progressBar.setVisibility(View.GONE);
            loadingAnimation.setImageDrawable(null);
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
        checkVisibility();
    }
}