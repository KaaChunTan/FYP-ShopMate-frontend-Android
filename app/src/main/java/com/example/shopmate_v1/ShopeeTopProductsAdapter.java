package com.example.shopmate_v1;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.net.Uri;
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

public class ShopeeTopProductsAdapter extends RecyclerView.Adapter<ShopeeTopProductsAdapter.ViewHolder> {

    //Initialize variable
    private ArrayList<ShopeeTopProductsData> dataArrayList;
    private Activity activity;

    //Create constructor
    public ShopeeTopProductsAdapter(Activity activity, ArrayList<ShopeeTopProductsData> dataArrayList){
        this.activity = activity;
        this.dataArrayList = dataArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Initialize variable
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.shopee_top_products_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ShopeeTopProductsAdapter.ViewHolder holder, int position) {
        //Initialize variable
        ShopeeTopProductsData data = dataArrayList.get(position);

        //Set image view
        Glide.with(activity).load(data.getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.shopee_top_products_image_view);

        holder.shopee_top_products_name_text_view.setText(data.getName());
        holder.shopee_top_products_url_text_view.setText(data.getUrl());
        holder.shopee_top_products_list_card_view.setOnClickListener(new View.OnClickListener() {
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
        ImageView shopee_top_products_image_view;
        TextView shopee_top_products_name_text_view, shopee_top_products_url_text_view;
        MaterialCardView shopee_top_products_list_card_view;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //Assign variable
            shopee_top_products_image_view = itemView.findViewById(R.id.shopee_top_products_image_view);
            shopee_top_products_name_text_view = itemView.findViewById(R.id.shopee_top_products_name_text_view);
            shopee_top_products_url_text_view = itemView.findViewById(R.id.shopee_top_products_url_text_view);
            shopee_top_products_list_card_view = itemView.findViewById(R.id.shopee_top_products_list_card_view);


        }
    }
}
