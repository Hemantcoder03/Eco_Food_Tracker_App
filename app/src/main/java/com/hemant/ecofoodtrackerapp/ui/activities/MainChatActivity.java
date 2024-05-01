package com.hemant.ecofoodtrackerapp.ui.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Query;
import com.hemant.ecofoodtrackerapp.adapters.MainChatAdapter;
import com.hemant.ecofoodtrackerapp.databinding.ActivityMainChatBinding;
import com.hemant.ecofoodtrackerapp.models.ChatMessageModel;
import com.hemant.ecofoodtrackerapp.models.ChatroomModel;
import com.hemant.ecofoodtrackerapp.models.UserDataModel;
import com.hemant.ecofoodtrackerapp.util.AndroidUtil;
import com.hemant.ecofoodtrackerapp.util.ConnectionReceiver;
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

        if (!checkInternetCon()) {
            return;
        }

        binding.donorChatName.setText(donor.getUserName());

        binding.mainChatBackBtn.setOnClickListener(v -> {
            startActivity(new Intent(MainChatActivity.this, MainActivity.class));
        });

        binding.mainChatSendBtn.setOnClickListener(v -> {
            String msg = binding.mainChatTextInput.getText().toString().trim();
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
        binding.mainChatRV.setLayoutManager(manager);
        binding.mainChatRV.setAdapter(adapter);
    }

    private void sendMessageToDonor(String msg) {
        if (!checkInternetCon()) {
            return;
        }

        chatroomModel.setLastMessageSenderId(FirebaseUtil.getCurrentUserId());
        chatroomModel.setLastMessageTimestamp(Timestamp.now());
        FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel);

        ChatMessageModel chatMessageModel = new ChatMessageModel(msg, FirebaseUtil.getCurrentUserId(), Timestamp.now());
        FirebaseUtil.getChatroomMessageReference(chatroomId).add(chatMessageModel)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        binding.mainChatProgressBar.setVisibility(View.GONE);
                        binding.mainChatTextInput.setText("");
                    }
                });
    }

    public Boolean checkInternetCon() {
        //first check the network connection
        if (!AndroidUtil.checkConnection(this)) {
            AndroidUtil.setToast(this, "Please check your internet connection");
            binding.mainChatProgressBar.setVisibility(View.GONE);
            return false;
        }
        return true;
    }

    private void getOrCreateChatroomId() {
        FirebaseUtil.getChatroomReference(chatroomId).get().addOnCompleteListener(v -> {
            chatroomModel = v.getResult().toObject(ChatroomModel.class);
            if (chatroomModel == null) {
                //create a new chatroom
                chatroomModel = new ChatroomModel(chatroomId, Arrays.asList(FirebaseUtil.getCurrentUserId(), donor.getUserId()), Timestamp.now(), "");
                FirebaseUtil.getChatroomReference(chatroomId).set(chatroomModel)
                        .addOnFailureListener(v2 -> {
                            AndroidUtil.setToast(MainChatActivity.this, "Something went wrong");
                        });
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

    //is is used for check network connection but not used for now
    @Override
    public void onNetworkChange(boolean isConnected) {

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(MainChatActivity.this, MainActivity.class));
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}