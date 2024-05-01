package com.hemant.ecofoodtrackerapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.hemant.ecofoodtrackerapp.R;
import com.hemant.ecofoodtrackerapp.models.CartModel;
import com.hemant.ecofoodtrackerapp.models.FoodDataModel;
import com.hemant.ecofoodtrackerapp.models.LocationModel;
import com.hemant.ecofoodtrackerapp.models.UserDataModel;
import com.hemant.ecofoodtrackerapp.ui.activities.FoodDescActivity;
import com.hemant.ecofoodtrackerapp.ui.fragments.CartFragment;
import com.hemant.ecofoodtrackerapp.util.AndroidUtil;
import com.hemant.ecofoodtrackerapp.util.FirebaseUtil;
import com.squareup.picasso.Picasso;

public class CartAdapter extends FirestoreRecyclerAdapter<CartModel, CartAdapter.MyViewHolder> {

    Context context;
    CartFragment cartFragment;

    public CartAdapter(@NonNull FirestoreRecyclerOptions<CartModel> options, Context context, CartFragment cartFragment) {
        super(options);
        this.context = context;
        this.cartFragment = cartFragment;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.cart_item, parent, false));
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int i, @NonNull CartModel model) {

        //set the near by location of user and donor food location
        //if he/she an current user then show the cart details
        if (model.getCartFoodUserId().equals(FirebaseUtil.getCurrentUserId()) && model.getCartFoodStatus().equals("not ordered")) {

            //fetch the food data using id of it
            FirebaseFirestore.getInstance().collection("Foods")
                    .document(model.getCartFoodId())
                    .get()
                    .addOnSuccessListener(v -> {
                        FoodDataModel foodDataModel = v.toObject(FoodDataModel.class);

                        try{
                            FirebaseUtil.getCurrentUserDetails().get().addOnSuccessListener(v1 -> {
                                        UserDataModel userDataModel = v1.toObject(UserDataModel.class);
                                        if(userDataModel != null){

                                            //find the distance between two locations
                                            LocationModel locationModel = null;
                                            if (foodDataModel != null) {
                                                locationModel = foodDataModel.getItemDonorNearbyLoc();
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
                                                        holder.cartFoodNearDis.setText(formattedNumber + " km");
                                                        AndroidUtil.setLog("checkError", "nearby location is " + meters / 1000 + " km");
                                                    } else {
                                                        String formattedNumber = String.format("%.2f", meters);
                                                        AndroidUtil.setLog("checkError", "nearby location is " + meters + " m");
                                                        holder.cartFoodNearDis.setText(formattedNumber + " m");
                                                    }
                                                }
                                                //set disabled the increment and decrement button
                                                holder.cartFoodIncreBtn.setEnabled(false);
                                                holder.cartFoodDecreBtn.setEnabled(false);

                                                holder.cartFoodName.setText(foodDataModel.getItemFoodName());
                                                holder.cartFoodQty.setText(String.valueOf(foodDataModel.getItemQuantity()));

                                                //set the image
                                                Picasso.get().load(Uri.parse(foodDataModel.getItemFoodImage()))
                                                        .into(holder.cartFoodImage);

                                                //remove food from cart
                                                holder.cartFoodRemoveBtn.setOnClickListener(v2 -> {
                                                    FirebaseUtil.removeCartFoodUsingRef(v2, model.getCartFoodId());
                                                    CartAdapter.this.notifyDataSetChanged();
                                                    //reload the fragment to check the new items
                                                    cartFragment.reload();
                                                });

                                                holder.cartCV.setOnClickListener(v3 ->{
                                                    //send to item to food desc page
                                                    Intent intent = new Intent(context, FoodDescActivity.class);
                                                    intent.putExtra("foodId",model.getCartFoodId());
                                                    intent.putExtra("donorId",foodDataModel.getItemDonorProfileId());
                                                    context.startActivity(intent);
                                                });
                                            }

                                        } else {
                                            AndroidUtil.setToast(context, "Please check your internet connection");
                                        }
                                    })
                                    .addOnFailureListener(v3 -> {
                                        AndroidUtil.setToast(context, "Please check your internet connection");
                                    });
                        } catch (Exception e) {
                            AndroidUtil.setLog("checkError", "error");
                        }
                    })
                    .addOnFailureListener(v -> {
                        AndroidUtil.setFailedSnackBar(holder.itemView, "Something went wrong");
                    });

        } else {
            holder.cartCV.setVisibility(View.GONE);
        }
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView cartFoodName, cartFoodNearDis, cartFoodQty;
        ImageButton cartFoodIncreBtn, cartFoodDecreBtn, cartFoodRemoveBtn;
        ImageView cartFoodImage;
        CardView cartCV;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            cartFoodName = itemView.findViewById(R.id.cartFoodName);
            cartFoodNearDis = itemView.findViewById(R.id.cartFoodNearDis);
            cartFoodQty = itemView.findViewById(R.id.cartFoodQty);
            cartFoodIncreBtn = itemView.findViewById(R.id.cartFoodIncreBtn);
            cartFoodDecreBtn = itemView.findViewById(R.id.cartFoodDecreBtn);
            cartFoodRemoveBtn = itemView.findViewById(R.id.cartFoodRemoveBtn);
            cartFoodImage = itemView.findViewById(R.id.cartFoodImage);
            cartCV = itemView.findViewById(R.id.cartCV);
        }
    }
}
