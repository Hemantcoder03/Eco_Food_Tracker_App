package com.hemant.ecofoodtrackerapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.hemant.ecofoodtrackerapp.R;
import com.hemant.ecofoodtrackerapp.models.FoodDataModel;
import com.hemant.ecofoodtrackerapp.models.LocationModel;
import com.hemant.ecofoodtrackerapp.models.UserDataModel;
import com.hemant.ecofoodtrackerapp.ui.activities.FoodDescActivity;
import com.hemant.ecofoodtrackerapp.util.AndroidUtil;
import com.hemant.ecofoodtrackerapp.util.FirebaseUtil;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class FoodListAdapter extends FirestoreRecyclerAdapter<FoodDataModel, FoodListAdapter.MyViewHolder> {

    Context context;

    public FoodListAdapter(@NonNull FirestoreRecyclerOptions<FoodDataModel> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull FoodDataModel model) {

        //set the near by location of user and donor food location
        try{
            FirebaseUtil.getCurrentUserDetails().get().addOnSuccessListener(v -> {
                        UserDataModel userDataModel = v.toObject(UserDataModel.class);
                        if(userDataModel != null){

                            //find the distance between two locations
                            LocationModel locationModel = model.getItemDonorNearbyLoc();
                            Location donorFoodLocation = new Location("Food Location");
                            donorFoodLocation.setLatitude(locationModel.getLatitude());
                            donorFoodLocation.setLongitude(locationModel.getLongitude());

                            Location userLocation = new Location("User Location");
                            userLocation.setLatitude(userDataModel.getCurrentLocation().getLatitude());
                            userLocation.setLongitude(userDataModel.getCurrentLocation().getLongitude());

                            float meters = userLocation.distanceTo(donorFoodLocation);
                            if (meters > 0) {
                                if (meters > 1000) {
                                    String formattedNumber = String.format("%.2f", meters / 1000);
                                    holder.itemDonorNearbyLoc.setText(formattedNumber + " km");
                                    AndroidUtil.setLog("checkError", "nearby location is " + meters / 1000 + " km");
                                } else {
                                    String formattedNumber = String.format("%.2f", meters);
                                    AndroidUtil.setLog("checkError", "nearby location is " + meters + " m");
                                    holder.itemDonorNearbyLoc.setText(formattedNumber + " m");
                                }
                            }
                        }
                        else{
                            AndroidUtil.setToast(context, "Please check your internet connection");
                        }
                    })
                    .addOnFailureListener(v -> {
                        AndroidUtil.setToast(context, "Please check your internet connection");
                    });
        }
        catch (Exception e){
            AndroidUtil.setLog("checkError","error");
        }


        FirebaseUtil.getDonorDetails(model.getItemDonorProfileId()).get().addOnCompleteListener(v -> {
            if (v.isSuccessful()) {
                //used to fetch user name using userid
                UserDataModel userDataModel = v.getResult().toObject(UserDataModel.class);
                if (userDataModel != null) {
                    holder.itemDonorProfileName.setText(userDataModel.getUserName());
                } else {
                    AndroidUtil.setToast(context, "Something went wrong");
                }
            } else {
                AndroidUtil.setToast(context, "Something went wrong");
            }
        });
        holder.itemFoodName.setText(model.getItemFoodName());
        Picasso.get().load(Uri.parse(model.getItemFoodImage()))
                .into(holder.itemFoodImg);

        holder.itemDonateDate.setText(model.getItemDonateDate());
        holder.itemRateCount.setText(model.getItemRateCount());
        holder.foodItemLayout.setOnClickListener(v -> {
            Intent intent = new Intent(context, FoodDescActivity.class);
            intent.putExtra("donorId",model.getItemDonorProfileId());
            intent.putExtra("foodId", model.getItemId());
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
        return new MyViewHolder(view);
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView itemFoodName, itemDonorProfileName, itemDonorNearbyLoc, itemDonateDate, itemRateCount;
        CircleImageView itemDonorProfileImg;
        ImageButton itemFoodImg;
        CardView foodItemLayout;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            itemFoodName = itemView.findViewById(R.id.itemFoodName);
            itemDonorProfileName = itemView.findViewById(R.id.itemDonorProfileName);
            itemDonorNearbyLoc = itemView.findViewById(R.id.itemDonorNearbyLoc);
            itemDonateDate = itemView.findViewById(R.id.itemDonateDate);
            itemRateCount = itemView.findViewById(R.id.itemRateCount);
            itemDonorProfileImg = itemView.findViewById(R.id.itemDonorProfileImg);
            itemFoodImg = itemView.findViewById(R.id.itemFoodImg);
            foodItemLayout = itemView.findViewById(R.id.foodItemLayout);
        }
    }

    public void reloadAdapter() {
        notifyDataSetChanged();
    }

}