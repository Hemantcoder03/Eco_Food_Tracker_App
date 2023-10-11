package com.hemant.ecofoodtrackerapp.adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.widget.ContentLoadingProgressBar;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.hemant.ecofoodtrackerapp.R;
import com.hemant.ecofoodtrackerapp.models.FoodData;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

//public class FoodListAdapter extends RecyclerView.Adapter<FoodListAdapter.MyViewHolder>{
//
//    ArrayList<FoodData> filteredList;
//    Context context;
//
//    public FoodListAdapter(ArrayList<FoodData> filteredList, Context context) {
//        this.filteredList = filteredList;
//        this.context = context;
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
//
//        FoodData model = filteredList.get(position);
//        holder.itemFoodName.setText(model.getItemFoodName());
//        holder.itemDonorProfileName.setText(model.getItemDonorProfileName());
//        holder.itemDonorNearbyLoc.setText(model.getItemDonorNearbyLoc());
//        holder.itemDonateDate.setText(model.getItemDonateDate());
//        holder.itemRateCount.setText(model.getItemRateCount());
//    }
//
//    @NonNull
//    @Override
//    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
//        return new MyViewHolder(view);
//    }
//
//    @Override
//    public int getItemCount() {
//        return filteredList.size();
//    }
//
//    class MyViewHolder extends RecyclerView.ViewHolder {
//
//        TextView itemFoodName, itemDonorProfileName, itemDonorNearbyLoc, itemDonateDate, itemRateCount;
//        CircleImageView itemDonorProfileImg;
//
//        public MyViewHolder(@NonNull View itemView) {
//            super(itemView);
//            itemFoodName = itemView.findViewById(R.id.itemFoodName);
//            itemDonorProfileName = itemView.findViewById(R.id.itemDonorProfileName);
//            itemDonorNearbyLoc = itemView.findViewById(R.id.itemDonorNearbyLoc);
//            itemDonateDate = itemView.findViewById(R.id.itemDonateDate);
//            itemRateCount = itemView.findViewById(R.id.itemRateCount);
//            itemDonorProfileImg = itemView.findViewById(R.id.itemDonorProfileImg);
//        }
//
//    }
//
//}




public class FoodListAdapter extends FirebaseRecyclerAdapter<FoodData, FoodListAdapter.MyViewHolder> {

    public FoodListAdapter(@NonNull FirebaseRecyclerOptions<FoodData> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull MyViewHolder holder, int position, @NonNull FoodData model) {

        holder.itemFoodName.setText(model.getItemFoodName());
        holder.itemDonorProfileName.setText(model.getItemDonorProfileName());
        holder.itemDonorNearbyLoc.setText(model.getItemDonorNearbyLoc());
        holder.itemDonateDate.setText(model.getItemDonateDate());
        holder.itemRateCount.setText(model.getItemRateCount());
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.food_item, parent, false);
        return new MyViewHolder(view);
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView itemFoodName, itemDonorProfileName, itemDonorNearbyLoc, itemDonateDate, itemRateCount;
        CircleImageView itemDonorProfileImg;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            itemFoodName = itemView.findViewById(R.id.itemFoodName);
            itemDonorProfileName = itemView.findViewById(R.id.itemDonorProfileName);
            itemDonorNearbyLoc = itemView.findViewById(R.id.itemDonorNearbyLoc);
            itemDonateDate = itemView.findViewById(R.id.itemDonateDate);
            itemRateCount = itemView.findViewById(R.id.itemRateCount);
            itemDonorProfileImg = itemView.findViewById(R.id.itemDonorProfileImg);
        }
    }

    public void reloadAdapter(){
        notifyDataSetChanged();
    }
}