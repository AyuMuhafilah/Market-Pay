package com.example.market_pay.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.market_pay.R;

import java.util.List;

public class MerchantAdapter extends RecyclerView.Adapter<MerchantAdapter.MerchantViewHolder> {

    private Context context;
    private List<String> foodNames;
    private List<Integer> imageResIds;

    public MerchantAdapter(Context context, List<String> foodNames, List<Integer> imageResIds) {
        this.context = context;
        this.foodNames = foodNames;
        this.imageResIds = imageResIds;
    }

    @NonNull
    @Override
    public MerchantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_merchant, parent, false);
        return new MerchantViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MerchantViewHolder holder, int position) {
        holder.foodName.setText(foodNames.get(position));
        holder.foodImage.setImageResource(imageResIds.get(position));
    }

    @Override
    public int getItemCount() {
        return foodNames.size(); // pastikan imageResIds juga punya ukuran sama
    }

    public static class MerchantViewHolder extends RecyclerView.ViewHolder {

        TextView foodName;
        ImageView foodImage;

        public MerchantViewHolder(View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.text_food_name);
            foodImage = itemView.findViewById(R.id.image_food);
        }
    }
}

