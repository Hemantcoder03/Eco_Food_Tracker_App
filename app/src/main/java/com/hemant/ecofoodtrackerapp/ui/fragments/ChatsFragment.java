package com.hemant.ecofoodtrackerapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.hemant.ecofoodtrackerapp.R;
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
    Boolean isVisible = false;

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

        //check whether any item is present if not then show the no chat item found text
        query.addSnapshotListener((value, error) -> {
            if (value != null && value.isEmpty()) {
                noFoundTextVisible();
            } else {
                query.get().addOnSuccessListener(v -> {
                    List<ChatroomModel> model = v.toObjects(ChatroomModel.class);
                    for (ChatroomModel chatroomModel : model) {

                        if(!isVisible){
                            if (chatroomModel.getUserIds().get(0).equals(FirebaseUtil.getCurrentUserId())) {
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

        chatListAdapter = new ChatListAdapter(options, requireActivity(), ChatsFragment.this);
        binding.chatRV.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.chatRV.setAdapter(chatListAdapter);
        binding.chatsListProgressBar.setVisibility(View.GONE);

        return view;
    }

    public void reload(){
        FragmentTransaction tr = getFragmentManager().beginTransaction();
        tr.replace(R.id.frame, new ChatsFragment());
        tr.commit();
    }

    private void noFoundTextVisible() {
        try {
            binding.chatNoChatFound.setVisibility(View.VISIBLE);
            isVisible = false;
        } catch (Exception ignored) {

        }
    }

    private void noFoundTextGone() {
        try {
            binding.chatNoChatFound.setVisibility(View.GONE);
            isVisible = true;
        } catch (Exception ignored) {

        }
    }

    @Override
    public void onStart() {
        super.onStart();
        //check the visibility of no found chat text to check chat present or not
        if(binding.chatNoChatFound.getVisibility() != View.VISIBLE){
            chatListAdapter.startListening();
        }
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}