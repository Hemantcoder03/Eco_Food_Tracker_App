package com.hemant.ecofoodtrackerapp.fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.airbnb.lottie.LottieAnimationView;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hemant.ecofoodtrackerapp.R;;
import com.hemant.ecofoodtrackerapp.adapters.FoodListAdapter;
import com.hemant.ecofoodtrackerapp.models.FoodData;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class HomeFragment extends Fragment {

    FirebaseDatabase db;
    DatabaseReference ref;
    SearchView searchFood;
    ImageButton searchFilterBtn;
    RecyclerView foodListRV;
    ArrayList<FoodData> foodDataList;
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

        FirebaseRecyclerOptions<FoodData> options =
                new FirebaseRecyclerOptions.Builder<FoodData>()
                        .setQuery(ref.child("FoodList"), FoodData.class)
                        .build();

        foodDataList = new ArrayList<>();
        foodListRV.setLayoutManager(new LinearLayoutManager(requireContext()));

        refreshBar.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
//                clearList();
//                foodDataList = retrieveFoods();

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
//        foodListAdapter = new FoodListAdapter(foodDataList, requireContext());
        foodListAdapter = new FoodListAdapter(options);
        foodListRV.setAdapter(foodListAdapter);

        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                checkVisibility();
            }
        }, 0,100);

    }

    private void clearList() {
        foodDataList.clear();
        foodListAdapter.notifyDataSetChanged();
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
        }, 0,100);
        foodListAdapter.reloadAdapter();
    }

    public void checkVisibility(){
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
        onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
        foodListAdapter.stopListening();
        checkVisibility();
    }
}