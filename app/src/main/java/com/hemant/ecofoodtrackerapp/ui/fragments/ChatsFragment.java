package com.hemant.ecofoodtrackerapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.hemant.ecofoodtrackerapp.adapters.ChatListAdapter;
import com.hemant.ecofoodtrackerapp.databinding.FragmentChatsBinding;
import com.hemant.ecofoodtrackerapp.models.ChatroomModel;
import com.hemant.ecofoodtrackerapp.util.FirebaseUtil;

import java.util.List;

public class ChatsFragment extends Fragment {

    private FragmentChatsBinding binding;
    ChatListAdapter chatListAdapter;
    View view;
    FirebaseFirestore db;

    public ChatsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentChatsBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        binding.chatsListProgressBar.setVisibility(View.VISIBLE);

        db = FirebaseFirestore.getInstance();

        Query query = FirebaseFirestore.getInstance().collection("ChatRooms");
        FirestoreRecyclerOptions<ChatroomModel> options = new FirestoreRecyclerOptions.Builder<ChatroomModel>()
                .setQuery(query, ChatroomModel.class)
                .build();

        //check whether any chat is present if not then show the no chat found text
        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if(value.isEmpty()){
                    binding.chatNoChatFound.setVisibility(View.VISIBLE);
                }
                else {
                    query.get().addOnSuccessListener(v -> {
                        List<ChatroomModel> model = v.toObjects(ChatroomModel.class);
                        for (ChatroomModel chatroomModel : model) {
                            if (!chatroomModel.getUserIds().get(0).equals(FirebaseUtil.getCurrentUserId())) {
                                binding.chatNoChatFound.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                }
            }
        });

        chatListAdapter = new ChatListAdapter(options, requireActivity());
        binding.chatRV.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.chatRV.setAdapter(chatListAdapter);
        binding.chatsListProgressBar.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }

    @Override
    public void onStart() {
        super.onStart();
        chatListAdapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        chatListAdapter.stopListening();
    }

    @Override
    public void onResume() {
        super.onResume();
        chatListAdapter.startListening();
    }
}