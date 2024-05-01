package com.hemant.ecofoodtrackerapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hemant.ecofoodtrackerapp.R;
import com.hemant.ecofoodtrackerapp.models.FoodDataModel;
import com.hemant.ecofoodtrackerapp.models.FoodOrderModel;
import com.hemant.ecofoodtrackerapp.ui.activities.FoodDescActivity;
import com.hemant.ecofoodtrackerapp.ui.activities.HistoryActivity;
import com.hemant.ecofoodtrackerapp.ui.activities.TrackOrderActivity;
import com.hemant.ecofoodtrackerapp.util.AndroidUtil;
import com.hemant.ecofoodtrackerapp.util.FirebaseUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

public class HistoryAdapter extends FirestoreRecyclerAdapter<FoodOrderModel, HistoryAdapter.MyViewHolder> {

    Context context;
    HistoryActivity historyActivity;
    TrackOrderActivity trackOrderActivity;
    String comingFrom;
    FirestoreRecyclerOptions<FoodOrderModel> options;

    public HistoryAdapter(@NonNull FirestoreRecyclerOptions<FoodOrderModel> options, Context context, HistoryActivity activity,TrackOrderActivity trackOrderActivity, String comingFrom) {
        super(options);
        this.context = context;
        this.historyActivity = activity;
        this.trackOrderActivity = trackOrderActivity;
        this.comingFrom = comingFrom;
        this.options = options;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.history_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int i, @NonNull FoodOrderModel model) {

        if(model.getOrderUserId().equals(FirebaseUtil.getCurrentUserId())){

            if(comingFrom.equals("History")){

                //hide the food status which is not needed for "history" activity
                holder.historyFoodCurrentStatus.setVisibility(View.GONE);

                //hide the item when it not show as deliverable
                if(!model.getStatus().equals("delivered") && !model.getStatus().equals("cancelled")){
                    holder.historyItemCV.setVisibility(View.GONE);
                }

                setFoodDetails(holder,model);

                //set the text color
                if(model.getStatus().equals("cancelled")){
                    holder.historyFoodStatus.setTextColor(context.getColor(R.color.red));
                }
                holder.historyFoodStatus.setText(model.getStatus());

                //check the food from both the adapters
                CollectionReference ref = FirebaseFirestore.getInstance().collection("Orders");
                ref.get().addOnSuccessListener(v -> {
                    ref.get().addOnSuccessListener(v2 -> {
                        List<FoodOrderModel> order1 = v.toObjects(FoodOrderModel.class);
                        List<FoodOrderModel> order2 = v2.toObjects(FoodOrderModel.class);

                        for (FoodOrderModel model2 : order1) {
                            for (FoodOrderModel model3 : order2) {
                                if (model2 != null && model3 != null && !model2.getStatus().equals("delivered") && !model2.getStatus().equals("cancelled")) {
                                    if (!model3.getStatus().equals("delivered") && !model3.getStatus().equals("cancelled")) {
                                        //if not both type of items was "delivered" then show text
                                        historyActivity.showNoFoodText();
                                    }
                                }
                            }
                        }
                    });
                });

                holder.historyItemCV.setOnClickListener(v ->{

                    sendToFoodDesc(context,model);
                });

                holder.historyFoodOrderBtn.setOnClickListener(v ->{
                    sendToFoodDesc(context,model);
                });
            }
            else if(comingFrom.equals("TrackOrder")){

                //hide the food status which is not needed for "track order" activity
                holder.historyFoodStatus.setVisibility(View.GONE);
                holder.historyFoodOrderBtn.setVisibility(View.GONE);
                holder.historyFoodCurrentStatus.setVisibility(View.VISIBLE);

                //hide the item when it not show as deliverable
                if(model.getStatus().equals("delivered") || model.getStatus().equals("cancelled")){
                    holder.historyItemCV.setVisibility(View.GONE);
                }

                setFoodDetails(holder,model);
                holder.historyFoodCurrentStatus.setText(model.getStatus());

                //check the food from both the adapters
                CollectionReference ref = FirebaseFirestore.getInstance().collection("Orders");
                ref.get().addOnSuccessListener(v -> {
                    ref.get().addOnSuccessListener(v2 -> {
                        List<FoodOrderModel> order1 = v.toObjects(FoodOrderModel.class);
                        List<FoodOrderModel> order2 = v2.toObjects(FoodOrderModel.class);

                        for (FoodOrderModel model2 : order1) {
                            for (FoodOrderModel model3 : order2) {
                                if (model2 != null && model3 != null) {
                                    if (model3.getStatus().equals("delivered") || model3.getStatus().equals("cancelled") || model2.getStatus().equals("delivered") || model2.getStatus().equals("cancelled")) {
                                        //if not both type of items was "delivered" then show text
                                        trackOrderActivity.showNoFoodText();
                                    }
                                }
                            }
                        }
                    });
                });

                holder.historyItemCV.setOnClickListener(v ->{

                    //send to food desc activity to show the description of food
                    Intent intent = new Intent(context, FoodDescActivity.class);
                    intent.putExtra("foodId",model.getFoodId());
                    intent.putExtra("donorId",model.getDonorId());
                    intent.putExtra("comingFrom","TrackOrder");  //it will hide the order and other not wanted details
                    context.startActivity(intent);
                });
            }

        }
        else{
            //check the food is available or not
            if(options.getSnapshots().isEmpty()){
                if(comingFrom.equals("History")){
                    historyActivity.showNoFoodText();
                }
                else{
                    trackOrderActivity.showNoFoodText();
                }
            }

            holder.historyItemCV.setVisibility(View.GONE);
        }


    }

    private void sendToFoodDesc(Context context, FoodOrderModel model) {

        //send to food desc activity to show the description of food
        Intent intent = new Intent(context, FoodDescActivity.class);
        intent.putExtra("foodId",model.getFoodId());
        intent.putExtra("donorId",model.getDonorId());
        context.startActivity(intent);
    }

    private void setFoodDetails(MyViewHolder holder, FoodOrderModel model){

        FirebaseFirestore.getInstance().collection("Foods")
                .document(model.getFoodId())
                .get()
                .addOnSuccessListener(v ->{
                    FoodDataModel foodDataModel = v.toObject(FoodDataModel.class);
                    if(foodDataModel != null){
                        holder.historyFoodName.setText(foodDataModel.getItemFoodName());
                        Picasso.get().load(foodDataModel.getItemFoodImage())
                                .into(holder.historyFoodImage);
                        holder.historyFoodOrderBtn.setOnClickListener(v2 ->{

                        });
                    }else{
                        AndroidUtil.setToast(context,"Something went wrong");
                    }
                })
                .addOnFailureListener(v ->{
                    AndroidUtil.setToast(context,"Please check your internet connection");
                });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView historyFoodName, historyFoodStatus,historyFoodCurrentStatus;
        ImageView historyFoodImage;
        AppCompatButton historyFoodOrderBtn;
        CardView historyItemCV;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            historyFoodName = itemView.findViewById(R.id.historyFoodName);
            historyFoodStatus = itemView.findViewById(R.id.historyFoodStatus);
            historyFoodImage = itemView.findViewById(R.id.historyFoodImage);
            historyFoodOrderBtn = itemView.findViewById(R.id.historyFoodOrderBtn);
            historyItemCV = itemView.findViewById(R.id.historyItemCV);
            historyFoodCurrentStatus = itemView.findViewById(R.id.historyFoodCurrentStatus);
        }
    }
}
