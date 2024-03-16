package com.hemant.ecofoodtrackerapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hemant.ecofoodtrackerapp.adapters.CartAdapter;
import com.hemant.ecofoodtrackerapp.databinding.ActivityHistoryBinding;
import com.hemant.ecofoodtrackerapp.models.CartModel;

import java.util.ArrayList;

public class HistoryActivity extends AppCompatActivity {

    ActivityHistoryBinding binding;
    CartAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        ArrayList<String> list = new ArrayList<>();
        list.add("Maggi");


        FirestoreRecyclerOptions<CartModel> options =
                new FirestoreRecyclerOptions.Builder<CartModel>()
                        .setQuery(FirebaseFirestore.getInstance().collection("Carts"), CartModel.class)
                        .build();

        adapter = new CartAdapter(options, this);
        binding.historyRV.setLayoutManager(new LinearLayoutManager(this));
        binding.historyRV.setAdapter(adapter);

        binding.historyBackBtn.setOnClickListener(v ->{
            startActivity(new Intent(this, MainActivity.class));
            finish();
        });
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