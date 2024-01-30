package com.hemant.ecofoodtrackerapp.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.hemant.ecofoodtrackerapp.adapters.ChatListAdapter;
import com.hemant.ecofoodtrackerapp.databinding.FragmentChatsBinding;
import com.hemant.ecofoodtrackerapp.models.ChatsDonor;
import com.hemant.ecofoodtrackerapp.models.UserDataModel;

import java.util.ArrayList;

public class ChatsFragment extends Fragment {

    private FragmentChatsBinding binding;
    private ArrayList<ChatsDonor> chatsDonors;
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
        chatsDonors = new ArrayList<>();

        Query query = FirebaseFirestore.getInstance().collection("Receivers");
        FirestoreRecyclerOptions<UserDataModel> options = new FirestoreRecyclerOptions.Builder<UserDataModel>()
                .setQuery(query, UserDataModel.class)
                .build();

        chatListAdapter = new ChatListAdapter(options, requireActivity());
        binding.donorChatRV.setLayoutManager(new LinearLayoutManager(requireActivity()));
        binding.donorChatRV.setAdapter(chatListAdapter);
        binding.chatsListProgressBar.setVisibility(View.GONE);

//        db.collection("Receivers").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
//                    @Override
//                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
//                        binding.chatsListProgressBar.setVisibility(View.GONE);
////                        if(!queryDocumentSnapshots.isEmpty()){
////                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
////                            for(DocumentSnapshot d : list){
////                                ChatsDonor data = d.toObject(ChatsDonor.class);
////                                chatsDonors.add(data);
////                            }
//
//                        ArrayList<UserDataModel> donorChats = new ArrayList<>();
//
//                        if(!queryDocumentSnapshots.isEmpty()){
//                            List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
//                            for(DocumentSnapshot d : list){
//                                UserDataModel data = d.toObject(UserDataModel.class);
//                                donorChats.add(data);
//                            }
//
//                            chatListAdapter = new ChatListAdapter(requireActivity(), donorChats);
//                            binding.donorChatRV.setLayoutManager(new LinearLayoutManager(requireActivity()));
//                            binding.donorChatRV.setAdapter(chatListAdapter);
//                        }else{
//                            AndroidUtil.setToast(requireActivity(),"No data found");
//                        }
//                    }
//                })
//                .addOnFailureListener(new OnFailureListener() {
//                    @Override
//                    public void onFailure(@NonNull Exception e) {
//                        binding.chatsListProgressBar.setVisibility(View.GONE);
//                        AndroidUtil.setToast(requireActivity(),"Please check the internet connection");
//                    }
//                });


//        CollectionReference ref = db.collection("Donors");
//        CollectionReference ref2 = db.collection("Receivers");
//        ref.add(donor).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
//            @Override
//            public void onComplete(@NonNull Task<DocumentReference> task) {
//
//                if(task.isSuccessful()){
//                    Toast.makeText(requireActivity(), "Successful", Toast.LENGTH_SHORT).show();
//                }
//                else{
//                    Toast.makeText(requireActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
//                }
//            }`
//        });

//        chatListAdapter = new ChatListAdapter(requireActivity(), chatsDonors);
//        binding.donorChatRV.setLayoutManager(new LinearLayoutManager(requireActivity()));
//        binding.donorChatRV.setAdapter(chatListAdapter);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
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