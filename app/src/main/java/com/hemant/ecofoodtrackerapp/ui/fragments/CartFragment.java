package com.hemant.ecofoodtrackerapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hemant.ecofoodtrackerapp.adapters.CartAdapter;
import com.hemant.ecofoodtrackerapp.databinding.FragmentCartBinding;
import com.hemant.ecofoodtrackerapp.models.CartModel;

public class CartFragment extends Fragment {

    FragmentCartBinding binding;
    View view;
    CartAdapter adapter;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCartBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        FirestoreRecyclerOptions<CartModel> options =
                new FirestoreRecyclerOptions.Builder<CartModel>()
                        .setQuery(FirebaseFirestore.getInstance().collection("Carts"), CartModel.class)
                        .build();

        adapter = new CartAdapter(options, requireActivity());
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.recyclerView.setAdapter(adapter);

        // Inflate the layout for this fragment
        return view;
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