package com.hemant.ecofoodtrackerapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.hemant.ecofoodtrackerapp.R;
import com.hemant.ecofoodtrackerapp.models.ChatMessageModel;
import com.hemant.ecofoodtrackerapp.util.FirebaseUtil;

public class MainChatAdapter extends FirestoreRecyclerAdapter<ChatMessageModel, MainChatAdapter.MyViewHolder> {

    Context context;

    public MainChatAdapter(@NonNull FirestoreRecyclerOptions<ChatMessageModel> options,Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public MainChatAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.chats_recycelerview_layout, parent, false);
        return new MainChatAdapter.MyViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull ChatMessageModel model) {

        if(model.getSenderId().equals(FirebaseUtil.getCurrentUserId())){
            holder.leftChatTV.setVisibility(View.GONE);
            holder.rightChatTV.setVisibility(View.VISIBLE);
            holder.rightChatTV.setText(model.getMessage());
        }
        else{
            holder.rightChatTV.setVisibility(View.GONE);
            holder.leftChatTV.setVisibility(View.VISIBLE);
            holder.leftChatTV.setText(model.getMessage());
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView leftChatTV, rightChatTV;
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            leftChatTV = itemView.findViewById(R.id.leftChatTV);
            rightChatTV = itemView.findViewById(R.id.rightChatTV);
        }
    }
}
