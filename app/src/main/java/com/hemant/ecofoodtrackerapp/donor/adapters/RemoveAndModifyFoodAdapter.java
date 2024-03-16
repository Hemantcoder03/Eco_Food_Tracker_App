package com.hemant.ecofoodtrackerapp.donor.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
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

import com.hemant.ecofoodtrackerapp.R;
import com.hemant.ecofoodtrackerapp.donor.ui.activities.AddFoodActivity;
import com.hemant.ecofoodtrackerapp.models.FoodDataModel;
import com.hemant.ecofoodtrackerapp.util.FirebaseUtil;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class RemoveAndModifyFoodAdapter extends RecyclerView.Adapter<RemoveAndModifyFoodAdapter.MyViewHolder> {

    Context context;
    ArrayList<FoodDataModel> removeFoodList;
    ArrayList<String> removeFoodRefList;
    String comingFrom;

    public RemoveAndModifyFoodAdapter(){}

    public RemoveAndModifyFoodAdapter(Context context, ArrayList<FoodDataModel> removeFoodList, ArrayList<String> removeFoodRefList, String comingFrom) {
        this.context = context;
        this.removeFoodList = removeFoodList;
        this.removeFoodRefList = removeFoodRefList;
        this.comingFrom = comingFrom;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.remove_and_modify_food_item, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        holder.removeFoodCV.setVisibility(View.GONE);
        holder.modifyFoodCV.setVisibility(View.GONE);

        FoodDataModel model = removeFoodList.get(position);
        if (model.getItemDonorProfileId().equals(FirebaseUtil.getCurrentUserId()) && model.getItemOrderStatus().equals("not ordered")) {
            if (comingFrom.equals("Remove")) {
                holder.removeFoodCV.setVisibility(View.VISIBLE);
                //It show only donor food
                holder.removeFoodName.setText(model.getItemFoodName());
                holder.removeFoodDonateDate.setText(model.getItemDonateDate());

                //remove food from the database
                holder.removeFoodIcon.setOnClickListener(v1 -> {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(context);
                    dialog.setCancelable(false);
                    dialog.setMessage("Food Removed from list.");
                    dialog.setTitle("You Really want to remove food ?");
                    dialog.setIcon(R.drawable.remove_food_icon);
                    dialog.setPositiveButton("Remove", (dialog1, which) -> {
                                dialog1.dismiss();
                                FirebaseUtil.removeFoodUsingRef(context,v1,removeFoodRefList.get(position));
                            })
                            .setNegativeButton("Cancel", (dialog1, which) -> {
                                dialog1.dismiss();
                            });
                    dialog.show();
                });

                Picasso.get().load(Uri.parse(model.getItemFoodImage()))
                        .into(holder.removeFoodImage);
            } else if (comingFrom.equals("Modify")) {

                holder.modifyFoodCV.setVisibility(View.VISIBLE);
                //set the food data
                holder.modifyFoodName.setText(model.getItemFoodName());
                holder.modifyFoodDonateDate.setText(model.getItemDonateDate());
                Picasso.get().load(Uri.parse(model.getItemFoodImage()))
                        .into(holder.modifyFoodImage);
                holder.modifyFoodCV.setOnClickListener(v ->{
                    Intent intent = new Intent(context, AddFoodActivity.class);
                    intent.putExtra("comeFromBtn","ModifyFoodData");
                    intent.putExtra("foodRef", removeFoodRefList.get(position));
                    context.startActivity(intent);
                });
            }
        }
        else{
            //hide both the views for default count of food item
            holder.removeFoodCV.setVisibility(View.GONE);
            holder.modifyFoodCV.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return removeFoodList.size();
    }

    static class MyViewHolder extends RecyclerView.ViewHolder {

        CardView removeFoodCV, modifyFoodCV;
        ImageView removeFoodImage, modifyFoodImage;
        TextView removeFoodName, removeFoodDonateDate, modifyFoodName, modifyFoodDonateDate;
        ImageButton removeFoodIcon;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            //remove food
            removeFoodCV = itemView.findViewById(R.id.removeFoodCV);
            removeFoodImage = itemView.findViewById(R.id.removeFoodImage);
            removeFoodName = itemView.findViewById(R.id.removeFoodName);
            removeFoodDonateDate = itemView.findViewById(R.id.removeFoodDonateDate);
            removeFoodIcon = itemView.findViewById(R.id.removeFoodIcon);

            //modify food
            modifyFoodCV = itemView.findViewById(R.id.modifyFoodCV);
            modifyFoodImage = itemView.findViewById(R.id.modifyFoodImage);
            modifyFoodName = itemView.findViewById(R.id.modifyFoodName);
            modifyFoodDonateDate = itemView.findViewById(R.id.modifyFoodDonateDate);
        }
    }
}
