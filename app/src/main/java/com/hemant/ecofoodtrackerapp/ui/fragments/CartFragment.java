package com.hemant.ecofoodtrackerapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.hemant.ecofoodtrackerapp.R;
import com.hemant.ecofoodtrackerapp.adapters.CartAdapter;
import com.hemant.ecofoodtrackerapp.databinding.FragmentCartBinding;
import com.hemant.ecofoodtrackerapp.models.CartModel;
import com.hemant.ecofoodtrackerapp.util.FirebaseUtil;

import java.util.List;

public class CartFragment extends Fragment {

    FragmentCartBinding binding;
    View view;
    CartAdapter adapter;
    //set no text found visibility
    Boolean isNotVisible = false;

    public CartFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentCartBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        binding.cartListProgressBar.setVisibility(View.VISIBLE);

        Query query = FirebaseFirestore.getInstance().collection("Carts");
        FirestoreRecyclerOptions<CartModel> options =
                new FirestoreRecyclerOptions.Builder<CartModel>()
                        .setQuery(query, CartModel.class)
                        .build();

        //check whether any item is present if not then show the no cart item found text
        query.addSnapshotListener((value, error) -> {
            if (value != null && value.isEmpty()) {
                noFoundTextVisible();
            } else {
                query.get().addOnSuccessListener(v -> {
                    List<CartModel> model = v.toObjects(CartModel.class);
                    for (CartModel cartModel : model) {

                        if(!isNotVisible){
                            if (cartModel.getCartFoodUserId().equals(FirebaseUtil.getCurrentUserId())) {
                                //check the last item and then adjust visibility
                                noFoundTextGone();
                            } else {
                                noFoundTextVisible();
                            }
                        }
                    }
                });
            }
        });

        adapter = new CartAdapter(options, requireActivity(), CartFragment.this);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.recyclerView.setAdapter(adapter);
        binding.cartListProgressBar.setVisibility(View.GONE);


        // Inflate the layout for this fragment
        return view;
    }

    public void reload(){
        FragmentTransaction tr = getFragmentManager().beginTransaction();
        tr.replace(R.id.frame, new CartFragment());
        tr.commit();
    }

    private void noFoundTextVisible() {
        try {
            binding.noCartItemFound.setVisibility(View.VISIBLE);
            isNotVisible = false;
        } catch (Exception ignored) {

        }
    }

    private void noFoundTextGone() {
        try {
            binding.noCartItemFound.setVisibility(View.GONE);
            isNotVisible = true;
        } catch (Exception ignored) {

        }
    }


    @Override
    public void onStart() {
        super.onStart();
        if (binding.noCartItemFound.getVisibility() != View.VISIBLE) {
            adapter.startListening();
        }
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