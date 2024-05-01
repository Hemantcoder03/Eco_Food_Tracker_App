package com.hemant.ecofoodtrackerapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.hemant.ecofoodtrackerapp.R;
import com.hemant.ecofoodtrackerapp.models.ChatMessageModel;
import com.hemant.ecofoodtrackerapp.models.ChatroomModel;
import com.hemant.ecofoodtrackerapp.models.UserDataModel;
import com.hemant.ecofoodtrackerapp.ui.activities.MainChatActivity;
import com.hemant.ecofoodtrackerapp.ui.fragments.ChatsFragment;
import com.hemant.ecofoodtrackerapp.util.AndroidUtil;
import com.hemant.ecofoodtrackerapp.util.FirebaseUtil;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class ChatListAdapter extends FirestoreRecyclerAdapter<ChatroomModel, ChatListAdapter.MyViewHolder> {

    Context context;
    ChatsFragment chatsFragment;

    public ChatListAdapter(@NonNull FirestoreRecyclerOptions<ChatroomModel> options, Context context, ChatsFragment chatsFragment) {
        super(options);
        this.context = context;
        this.chatsFragment = chatsFragment;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_chat_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull ChatroomModel model) {

        //check whether the chat for current user or not
        if (model.getUserIds().get(0).equals(FirebaseUtil.getCurrentUserId()) && !model.getChatroomId().isEmpty()) {

            //query to get the last message from the "chats" endpoint
            Query query = FirebaseUtil.getChatroomMessageReference(model.getChatroomId()).orderBy("timestamp", Query.Direction.DESCENDING).limit(1);
            query.get().addOnSuccessListener(v -> {
                        List<ChatMessageModel> messageModel = v.toObjects(ChatMessageModel.class);

                        //set the data of last message
                        if (messageModel.get(0) != null) {
                            holder.chatLastMessage.setText(messageModel.get(0).getMessage());
                            FirebaseUtil.getDonorDetails(model.getUserIds().get(1)).get().addOnSuccessListener(v1 -> {
                                        //get the donor details and set them
                                        UserDataModel userDataModel = v1.toObject(UserDataModel.class);
                                        if (userDataModel != null) {
                                            holder.chatSenderName.setText(userDataModel.getUserName());
                                            if (userDataModel.getUserImage() != null) {
                                                Picasso.get().load(Uri.parse(userDataModel.getUserImage())).into(holder.chatSenderImage);
                                            }

                                            //get the chat created time and format it according
                                            SimpleDateFormat format = new SimpleDateFormat("dd MMM yyyy");
                                            holder.chatMessageTime.setText(format.format(model.getLastMessageTimestamp().toDate()));

                                            //set click listener for chat item
                                            holder.userChatItem.setOnClickListener(v3 -> {
                                                //also send the data as intent to the mainchatactivity page
                                                Intent intent = new Intent(context, MainChatActivity.class);
                                                AndroidUtil.setIntentForUserDataModel(intent, userDataModel);
                                                context.startActivity(intent);
                                            });
                                        } else {
                                            AndroidUtil.setToast(context, "Something went wrong");
                                        }
                                    })
                                    .addOnFailureListener(v2 -> {
                                        AndroidUtil.setToast(context, "Something went wrong");
                                    });

                        } else {
                            AndroidUtil.setToast(context, "Something went wrong");
                        }
                    })
                    .addOnFailureListener(v -> {
                        AndroidUtil.setToast(context, "Something went wrong");
                    });

            holder.userChatItem.setOnLongClickListener(v -> {
                //set the dialog box for remove the chat
                AlertDialog dialog = new AlertDialog.Builder(context)
                        .setCancelable(true)
                        .setIcon(R.drawable.profile_chat_icon)
                        .setMessage("Do you really want to delete chat!!")
                        .setTitle("Delete chat")
                        .setPositiveButton("Delete", ((dialog1, which) -> {
                            dialog1.dismiss();
                            FirebaseUtil.getChatroomReference(model.getChatroomId()).delete().addOnSuccessListener(v2 -> {
                                        AndroidUtil.setToast(context, "Chat Removed Successfully");
                                        //reload the chatlist and also reload the chat page to check the new items
                                        ChatListAdapter.this.notifyDataSetChanged();
                                        chatsFragment.reload();
                                    })
                                    .addOnFailureListener(v2 -> {
                                        AndroidUtil.setToast(context, "Something went wrong");
                                    });
                        }))
                        .setNegativeButton("Cancel", (dialog1, which) -> {
                            dialog1.dismiss();
                        }).show();

                //default return #return type boolean
                return false;
            });
        } else {
            holder.userChatItem.setVisibility(View.GONE);
        }
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        CircleImageView chatSenderImage;
        TextView chatSenderName, chatLastMessage, chatMessageTime;
        CardView userChatItem;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            chatSenderName = itemView.findViewById(R.id.chatSenderName);
            chatLastMessage = itemView.findViewById(R.id.chatLastMessage);
            chatSenderImage = itemView.findViewById(R.id.chatSenderImage);
            userChatItem = itemView.findViewById(R.id.userChatItem);
            chatMessageTime = itemView.findViewById(R.id.chatMessageTime);
        }
    }
}
