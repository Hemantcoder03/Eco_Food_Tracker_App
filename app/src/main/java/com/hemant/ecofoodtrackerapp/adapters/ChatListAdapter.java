package com.hemant.ecofoodtrackerapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.hemant.ecofoodtrackerapp.R;
import com.hemant.ecofoodtrackerapp.models.UserDataModel;
import com.hemant.ecofoodtrackerapp.ui.activities.MainChatActivity;
import com.hemant.ecofoodtrackerapp.util.AndroidUtil;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends FirestoreRecyclerAdapter<UserDataModel,ChatListAdapter.MyViewHolder> {

    Context context;

    public ChatListAdapter(@NonNull FirestoreRecyclerOptions<UserDataModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_chat_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull UserDataModel model) {
        holder.chatDonorName.setText(model.getUserName());
        holder.chatDonorLastMessage.setText(model.getUserEmail());
//        Picasso.get().load(model.get).into(holder.chatDonorImage);

        holder.userChatItem.setOnClickListener(v -> {
            Intent intent = new Intent(context, MainChatActivity.class);
            AndroidUtil.setIntentForUserDataModel(intent, model);
            context.startActivity(intent);
        });
    }

    static class MyViewHolder extends RecyclerView.ViewHolder{

        CircleImageView chatDonorImage;
        TextView chatDonorName, chatDonorLastMessage;
        LinearLayout userChatItem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            chatDonorName = itemView.findViewById(R.id.chatDonorName);
            chatDonorLastMessage = itemView.findViewById(R.id.chatDonorLastMessage);
            chatDonorImage = itemView.findViewById(R.id.chatDonorImage);
            userChatItem = itemView.findViewById(R.id.userChatItem);
        }
    }
}
