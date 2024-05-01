package com.hemant.ecofoodtrackerapp.donor.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hemant.ecofoodtrackerapp.R;
import com.hemant.ecofoodtrackerapp.donor.ui.activities.DonorTrackOrderActivity;
import com.hemant.ecofoodtrackerapp.donor.ui.fragments.DonorHistoryFragment;
import com.hemant.ecofoodtrackerapp.models.FoodDataModel;
import com.hemant.ecofoodtrackerapp.models.FoodOrderModel;
import com.hemant.ecofoodtrackerapp.ui.activities.FoodDescActivity;
import com.hemant.ecofoodtrackerapp.util.AndroidUtil;
import com.hemant.ecofoodtrackerapp.util.FirebaseUtil;
import com.squareup.picasso.Picasso;

import java.util.List;

public class DonorHistoryAdapter extends FirestoreRecyclerAdapter<FoodOrderModel, DonorHistoryAdapter.MyViewHolder> {
    Context context;
    DonorHistoryFragment donorHistoryFragment;
    DonorTrackOrderActivity donorTrackOrderActivity;
    String comingFrom;

    public DonorHistoryAdapter(@NonNull FirestoreRecyclerOptions<FoodOrderModel> options, Context context, DonorHistoryFragment fragment,DonorTrackOrderActivity donorTrackOrderActivity, String comingFrom) {
        super(options);
        this.context = context;
        this.donorHistoryFragment = fragment;
        this.donorTrackOrderActivity = donorTrackOrderActivity;
        this.comingFrom = comingFrom;
    }

    @NonNull
    @Override
    public DonorHistoryAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.donor_history_item, parent, false);
        return new DonorHistoryAdapter.MyViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull DonorHistoryAdapter.MyViewHolder holder, int i, @NonNull FoodOrderModel model) {

        if(model.getDonorId().equals(FirebaseUtil.getCurrentUserId())){

            if(comingFrom.equals("DonorHistory")){

                //hide the item when it not show as deliverable
                if(!model.getStatus().equals("delivered") && !model.getStatus().equals("cancelled")){
                    holder.donorHistoryItemCV.setVisibility(View.GONE);
                }

                setFoodDetails(holder,model);

                //set the text color
                if(model.getStatus().equals("cancelled")){
                    holder.donorHistoryFoodCurrentStatus.setTextColor(context.getColor(R.color.red));
                }
                holder.donorHistoryFoodCurrentStatus.setText(model.getStatus());

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
                                        donorHistoryFragment.showNoFoodText();
                                    }
                                }
                            }
                        }
                    });
                });

                holder.donorHistoryItemCV.setOnClickListener(v ->{

                    sendToFoodDesc(context,model);
                });
            }
            else if(comingFrom.equals("DonorTrackOrder")){
                //hide the item when it not show as deliverable
                if(model.getStatus().equals("delivered") || model.getStatus().equals("cancelled")){
                    holder.donorHistoryItemCV.setVisibility(View.GONE);
                }

                setFoodDetails(holder,model);
                holder.donorHistoryFoodCurrentStatus.setText(model.getStatus());

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
                                        donorTrackOrderActivity.showNoFoodText();
                                    }
                                }
                            }
                        }
                    });
                });

                holder.donorHistoryItemCV.setOnClickListener(v ->{

                    //send to food desc activity to show the description of food
                    sendToFoodDesc(context,model);
                });
            }

        }
        else{
            holder.donorHistoryItemCV.setVisibility(View.GONE);
        }

    }

    private void sendToFoodDesc(Context context, FoodOrderModel model) {

        //send to food desc activity to show the description of food
        Intent intent = new Intent(context, FoodDescActivity.class);
        intent.putExtra("foodId",model.getFoodId());
        intent.putExtra("donorId",model.getDonorId());
        intent.putExtra("comingFrom","DonorHistory");  //it will hide the order and other not wanted details
        context.startActivity(intent);
    }

    private void setFoodDetails(DonorHistoryAdapter.MyViewHolder holder, FoodOrderModel model){

        FirebaseFirestore.getInstance().collection("Foods")
                .document(model.getFoodId())
                .get()
                .addOnSuccessListener(v ->{
                    FoodDataModel foodDataModel = v.toObject(FoodDataModel.class);
                    if(foodDataModel != null){
                        holder.donorHistoryFoodName.setText(foodDataModel.getItemFoodName());
                        Picasso.get().load(foodDataModel.getItemFoodImage())
                                .into(holder.donorHistoryFoodImage);
                    }else{
                        AndroidUtil.setToast(context,"Something went wrong");
                    }
                })
                .addOnFailureListener(v ->{
                    AndroidUtil.setToast(context,"Please check your internet connection");
                });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView donorHistoryFoodName, donorHistoryFoodCurrentStatus;
        ImageView donorHistoryFoodImage;
        CardView donorHistoryItemCV;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            donorHistoryFoodName = itemView.findViewById(R.id.donorHistoryFoodName);
            donorHistoryFoodImage = itemView.findViewById(R.id.donorHistoryFoodImage);
            donorHistoryItemCV = itemView.findViewById(R.id.donorHistoryItemCV);
            donorHistoryFoodCurrentStatus = itemView.findViewById(R.id.donorHistoryFoodCurrentStatus);
        }
    }
}
