package com.hemant.ecofoodtrackerapp.donor.ui.fragments;

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
import com.hemant.ecofoodtrackerapp.adapters.DonorChatListAdapter;
import com.hemant.ecofoodtrackerapp.databinding.FragmentDonorChatsBinding;
import com.hemant.ecofoodtrackerapp.models.ChatroomModel;
import com.hemant.ecofoodtrackerapp.util.FirebaseUtil;

import java.util.List;

public class DonorChatsFragment extends Fragment {

    FragmentDonorChatsBinding binding;
    View view;
    DonorChatListAdapter donorChatListAdapter;
    FirebaseFirestore db;
    Boolean isVisible = false;

    public DonorChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentDonorChatsBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        binding.donorChatsListProgressBar.setVisibility(View.VISIBLE);

        Query query = FirebaseFirestore.getInstance().collection("ChatRooms");
        FirestoreRecyclerOptions<ChatroomModel> options = new FirestoreRecyclerOptions.Builder<ChatroomModel>()
                .setQuery(query, ChatroomModel.class)
                .build();

        //check whether any item is present if not then show the no cart item found text
        query.addSnapshotListener((value, error) -> {
            if (value != null && value.isEmpty()) {
                noFoundTextVisible();
            } else {
                query.get().addOnSuccessListener(v -> {
                    List<ChatroomModel> model = v.toObjects(ChatroomModel.class);
                    for (ChatroomModel chatroomModel : model) {
                        if(!isVisible){
                            if (chatroomModel.getUserIds().get(1).equals(FirebaseUtil.getCurrentUserId())) {
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


        donorChatListAdapter = new DonorChatListAdapter(options, requireActivity(),DonorChatsFragment.this);
        binding.mainChatRV.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.mainChatRV.setAdapter(donorChatListAdapter);
        binding.donorChatsListProgressBar.setVisibility(View.GONE);

        // Inflate the layout for this fragment
        return view;
    }

    public void reload(){
        FragmentTransaction tr = getFragmentManager().beginTransaction();
        tr.replace(R.id.frame, new DonorChatsFragment());
        tr.commit();
    }

    private void noFoundTextVisible() {
        try {
            binding.donorChatNoChatFound.setVisibility(View.VISIBLE);
            isVisible = false;
        } catch (Exception ignored) {

        }
    }

    private void noFoundTextGone() {
        try {
            binding.donorChatNoChatFound.setVisibility(View.GONE);
            isVisible = true;
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //check the visibility of no found chat text to check chat present or not
        if(binding.donorChatNoChatFound.getVisibility() != View.VISIBLE){
            donorChatListAdapter.startListening();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        donorChatListAdapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        donorChatListAdapter.startListening();
    }
}