package com.hemant.ecofoodtrackerapp.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hemant.ecofoodtrackerapp.R;

import java.util.ArrayList;

public class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.MyViewHolder> {

    ArrayList<String> historyItemList;
    Context context;

    public HistoryAdapter(ArrayList<String> historyItemList, Context context) {
        this.historyItemList = historyItemList;
        this.context = context;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_again,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.historyFoodName.setText(historyItemList.get(position));
    }

    @Override
    public int getItemCount() {
        return historyItemList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView historyFoodName;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            historyFoodName = itemView.findViewById(R.id.historyFoodName);
        }
    }
}
