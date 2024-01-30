package com.hemant.ecofoodtrackerapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.Query;
import com.hemant.ecofoodtrackerapp.ConnectionReceiver;
import com.hemant.ecofoodtrackerapp.adapters.MainChatAdapter;
import com.hemant.ecofoodtrackerapp.databinding.ActivityMainChatBinding;
import com.hemant.ecofoodtrackerapp.models.ChatMessageModel;
import com.hemant.ecofoodtrackerapp.models.ChatroomModel;
import com.hemant.ecofoodtrackerapp.models.UserDataModel;
import com.hemant.ecofoodtrackerapp.util.AndroidUtil;
import com.hemant.ecofoodtrackerapp.util.FirebaseUtil;

import java.util.Arrays;

public class MainChatActivity extends AppCompatActivity implements ConnectionReceiver.ReceiverListener {

    ActivityMainChatBinding binding;
    UserDataModel donor;
    String chatroomId;
    ChatroomModel chatroomModel;
    MainChatAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        donor = AndroidUtil.getIntentForUserDataModel(getIntent());
        chatroomId = FirebaseUtil.getChatroomId(FirebaseUtil.getCurrentUserId(), donor.getUserId());

        binding = ActivityMainChatBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.donorChatName.setText(donor.getUserName());

        binding.donorChatBackBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainChatActivity.this, MainActivity.class));
        });

        binding.donorChatSendBtn.setOnClickListener(v -> {
            String msg = binding.donorChatTextInput.getText().toString().trim();
            if (msg.isEmpty())
                return;

            binding.mainChatProgressBar.setVisibility(View.VISIBLE);
            sendMessageToDonor(msg);
        });

        getOrCreateChatroomId();
        getMessages();
    }

    public void getMessages() {

        Query query = FirebaseUtil.getChatroomMessageReference(chatroomId).orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ChatMessageModel> options = new FirestoreRecyclerOptions.Builder<ChatMessageModel>()
                .setQuery(query, ChatMessageModel.class)
                .build();

        adapter = new MainChatAdapter(options, this);
        LinearLayoutManager manager = new LinearLayoutManager(this);
        manager.setReverseLayout(true);
        binding.donorChatRV.setLayoutManager(manager);
        binding.donorChatRV.setAdapter(adapter);
    }

    private void sendMessageToDonor(String msg) {

        //first check the network connection
        if(!AndroidUtil.checkConnection(this)){
            AndroidUtil.setToast(this,"Please check your internet connection");
            binding.mainChatProgressBar.setVisibility(View.GONE);
            return;
        }

        chatroomModel.setLastMessageSenderId(FirebaseUtil.getCurrentUserId());
        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(msg, FirebaseUtil.getCurrentUserId(), Timestamp.now());
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentReference> task) {
                        if (task.isSuccessful()) {
                            binding.mainChatProgressBar.setVisibility(View.GONE);
                            binding.donorChatTextInput.setText("");
                        }
                    }
                });
    }



    private void getOrCreateChatroomId() {
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                chatroomModel = task.getResult().toObject(ChatroomModel.class);
                if (chatroomModel == null) {
                    //create a new chatroom
                    chatroomModel = new ChatroomModel(chatroomId, Arrays.asList(FirebaseUtil.getCurrentUserId(), donor.getUserId()), Timestamp.now(), "");
                    FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);
                }

            }
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
    protected void onResume() {
        super.onResume();
        adapter.startListening();
    }

    @Override
    public void onNetworkChange(boolean isConnected) {

    }
}