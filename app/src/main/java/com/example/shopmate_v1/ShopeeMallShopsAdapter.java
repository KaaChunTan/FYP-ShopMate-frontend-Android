package com.example.shopmate_v1;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class ShopeeMallShopsAdapter extends RecyclerView.Adapter<ShopeeMallShopsAdapter.ViewHolder> {

    //Initialize variable
    private ArrayList<ShopeeMallShopsData> dataArrayList;
    private Activity activity;

    //Create constructor
    public ShopeeMallShopsAdapter(Activity activity, ArrayList<ShopeeMallShopsData> dataArrayList){
        this.activity = activity;
        this.dataArrayList = dataArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Initialize variable
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shopee_mall_shops_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopeeMallShopsAdapter.ViewHolder holder, int position) {
        //Initialize variable
        ShopeeMallShopsData data = dataArrayList.get(position);

        //Set image view
        Glide.with(activity).load(data.getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.shopee_mall_shops_image_view);

        holder.shopee_mall_shops_promo_text_text_view.setText(data.getPromo_text());
        holder.shopee_mall_shops_url_text_view.setText(data.getUrl());

        holder.shopee_mall_shops_list_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(data.getUrl());
                v.getContext().startActivity(new Intent(Intent.ACTION_VIEW,uri));
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //Initialize variable
        ImageView shopee_mall_shops_image_view;
        TextView shopee_mall_shops_promo_text_text_view, shopee_mall_shops_url_text_view;
        MaterialCardView shopee_mall_shops_list_card_view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //Assign variable
            shopee_mall_shops_image_view = itemView.findViewById(R.id.shopee_mall_shops_image_view);
            shopee_mall_shops_promo_text_text_view = itemView.findViewById(R.id.shopee_mall_shops_promo_text_text_view);
            shopee_mall_shops_url_text_view = itemView.findViewById(R.id.shopee_mall_shops_url_text_view);
            shopee_mall_shops_list_card_view = itemView.findViewById(R.id.shopee_mall_shops_list_card_view);


        }
    }
}
