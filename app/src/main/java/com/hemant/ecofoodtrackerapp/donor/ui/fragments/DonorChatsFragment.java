package com.hemant.ecofoodtrackerapp.donor.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
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

    public DonorChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentDonorChatsBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        db = FirebaseFirestore.getInstance();

        Query query = FirebaseFirestore.getInstance().collection("ChatRooms");
        FirestoreRecyclerOptions<ChatroomModel> options = new FirestoreRecyclerOptions.Builder<ChatroomModel>()
                .setQuery(query, ChatroomModel.class)
                .build();

        //check whether any chat is present if not then show the no chat found text
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.isEmpty()) {
                    binding.donorChatNoChatFound.setVisibility(View.VISIBLE);
                } else {
                    query.get().addOnSuccessListener(v -> {
                        List<ChatroomModel> model = v.toObjects(ChatroomModel.class);
                        for (ChatroomModel chatroomModel : model) {
                            if (!chatroomModel.getUserIds().get(1).equals(FirebaseUtil.getCurrentUserId())) {
                                binding.donorChatNoChatFound.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }
        });

        donorChatListAdapter = new DonorChatListAdapter(options, requireActivity());
        binding.mainChatRV.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.mainChatRV.setAdapter(donorChatListAdapter);
        binding.donorChatsListProgressBar.setVisibility(View.GONE);

        // Inflate the layout for this fragment
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        donorChatListAdapter.startListening();
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