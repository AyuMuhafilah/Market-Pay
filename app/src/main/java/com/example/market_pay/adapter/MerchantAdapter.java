package com.example.market_pay.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.example.market_pay.R;

import java.util.List;

public class MerchantAdapter extends RecyclerView.Adapter<MerchantAdapter.MerchantViewHolder> {

    private static final String TAG = "MerchantAdapter";

    private Context context;
    private List<String> foodNames;
    private List<String> imageUrls;

    public MerchantAdapter(Context context, List<String> foodNames, List<String> imageUrls) {
        this.context = context;
        this.foodNames = foodNames;
        this.imageUrls = imageUrls;
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

        String imageUrl = imageUrls.get(position);
        Log.d(TAG, "Attempting to load image for: " + foodNames.get(position) + " from URL: " + imageUrl);

        Glide.with(context)
                .load(imageUrl)
                .placeholder(R.drawable.placeholder_image) // Pastikan drawable ini ada
                .error(R.drawable.error_image)     // Pastikan drawable ini ada
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e(TAG, "Glide image load failed for URL: " + imageUrl, e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d(TAG, "Glide image loaded successfully for URL: " + imageUrl);
                        return false;
                    }
                })
                .into(holder.foodImage);
    }

    @Override
    public int getItemCount() {
        return foodNames.size();
    }

    static class MerchantViewHolder extends RecyclerView.ViewHolder {
        TextView foodName;
        ImageView foodImage;

        public MerchantViewHolder(View itemView) {
            super(itemView);
            foodName = itemView.findViewById(R.id.text_food_name);
            foodImage = itemView.findViewById(R.id.image_food);
        }
    }
}