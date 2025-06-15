package com.example.market_pay.adapter;

import android.content.Context;
import android.content.Intent;
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
import com.example.market_pay.view.DetailMerchantActivity;
import com.example.market_pay.R;
import com.example.market_pay.model.MerchantModel;

import java.util.List;

public class MerchantAdapter extends RecyclerView.Adapter<MerchantAdapter.MerchantViewHolder> {

    private static final String TAG = "MerchantAdapter";

    private Context context;
    private List<MerchantModel> merchantList;

    public MerchantAdapter(Context context, List<MerchantModel> merchantList) {
        this.context = context;
        this.merchantList = merchantList;
    }

    @NonNull
    @Override
    public MerchantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context).inflate(R.layout.item_merchant, parent, false);
        return new MerchantViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MerchantViewHolder holder, int position) {
        MerchantModel merchant = merchantList.get(position);
        holder.foodName.setText(merchant.getUsaha());

        Glide.with(context)
                .load(merchant.getImage())
                .placeholder(R.drawable.placeholder_image)
                .error(R.drawable.error_image)
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        Log.e(TAG, "Glide image load failed for URL: " + merchant.getImage(), e);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        Log.d(TAG, "Glide image loaded successfully for URL: " + merchant.getImage());
                        return false;
                    }
                })
                .into(holder.foodImage);

        // ✅ Klik item bawa userId ke DetailMerchantActivity
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailMerchantActivity.class);
            intent.putExtra("userId", merchant.getUserId());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return merchantList.size();
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
