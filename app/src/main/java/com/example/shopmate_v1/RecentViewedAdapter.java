package com.example.shopmate_v1;

import android.app.Activity;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

public class RecentViewedAdapter extends RecyclerView.Adapter<RecentViewedAdapter.ViewHolder> {

    public static final String PRODUCT_URL = "PRODUCT_URL";
    public static final String RATING_SCORE = "RATING_SCORE";
    public static final String LOCATION = "LOCATION";
    public static final String SHOP_ID = "SHOP_ID";
    public static final String ITEM_ID = "ITEM_ID";
    public static final String PLATFORM = "PLATFORM";
    public static final String REVIEW_COUNT = "REVIEW_COUNT";

    //Initialize variable
    private ArrayList<SearchResultData> dataArrayList;
    private Activity activity;

    //Create constructor
    public RecentViewedAdapter(Activity activity, ArrayList<SearchResultData> dataArrayList){
        this.activity = activity;
        this.dataArrayList = dataArrayList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Initialize variable
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.recent_viewed_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecentViewedAdapter.ViewHolder holder, int position) {
        //Initialize variable
        SearchResultData data = dataArrayList.get(position);

        //Set image view
        Glide.with(activity).load(data.getItem_image())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.item_imageView);

        holder.itemName_textView.setText(data.getItem_name());
        holder.discountedPrice_textView.setText(data.getDiscounted_price());
        if(data.getPlatform().equalsIgnoreCase("Shopee")){
            //holder.recent_viewed_list_card_view.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#f1582b")));
            holder.recent_viewed_list_description.setBackgroundColor(Color.parseColor("#f1582b"));
        }
        else{
            //holder.recent_viewed_list_card_view.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor("#0f156d")));
            holder.recent_viewed_list_description.setBackgroundColor(Color.parseColor("#0f156d"));
        }

    }

    @Override
    public int getItemCount() {
        return dataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //Initialize variable
        ImageView item_imageView;
        TextView itemName_textView, discountedPrice_textView;
        MaterialCardView recent_viewed_list_card_view;
        LinearLayout recent_viewed_list_description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            //Assign variable
            recent_viewed_list_card_view = itemView.findViewById(R.id.recent_viewed_list_card_view);
            item_imageView = itemView.findViewById(R.id.item_image_view);
            itemName_textView = itemView.findViewById(R.id.itemName_text_view);
            discountedPrice_textView = itemView.findViewById(R.id.discountedPrice_text_view);
            recent_viewed_list_description = itemView.findViewById(R.id.recent_viewed_list_description);


            //set onClick listener for the item
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    Intent intent = new Intent(v.getContext(),DetailedProductPageActivity.class);
                    intent.putExtra(PRODUCT_URL,dataArrayList.get(getAdapterPosition()).getProduct_url());
                    intent.putExtra(RATING_SCORE,dataArrayList.get(getAdapterPosition()).getRatingScore());
                    intent.putExtra(LOCATION,dataArrayList.get(getAdapterPosition()).getLocation());
                    intent.putExtra(SHOP_ID,dataArrayList.get(getAdapterPosition()).getShop_id());
                    intent.putExtra(ITEM_ID,dataArrayList.get(getAdapterPosition()).getItem_id());
                    intent.putExtra(PLATFORM,dataArrayList.get(getAdapterPosition()).getPlatform());
                    intent.putExtra(REVIEW_COUNT,dataArrayList.get(getAdapterPosition()).getReview_count());
                    v.getContext().startActivity(intent);

                }
            });

        }
    }
}
