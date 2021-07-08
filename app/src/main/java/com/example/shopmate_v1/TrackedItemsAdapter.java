package com.example.shopmate_v1;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;

public class TrackedItemsAdapter extends RecyclerView.Adapter<TrackedItemsAdapter.ViewHolder> {

    public static final String PRODUCT_URL = "PRODUCT_URL";
    public static final String TRACKING_ID = "TRACKING_ID";
    public static final String TRACKED_ITEM_IMAGE = "TRACKED_ITEM_IMAGE";
    public static final String TRACKED_ITEM_NAME = "TRACKED_ITEM_NAME";
    public static final String TRACKED_ITEM_VARIATION = "TRACKED_ITEM_VARIATION";
    public static final String TRACKED_ITEM_LATEST_PRICE= "TRACKED_ITEM_LATEST_PRICE";
    public static final String TRACKED_ITEM_PLATFORM = "TRACKED_ITEM_PLATFORM";
    private static final String get_price_history_uri = "http://192.168.43.102/shopmate_v1/get_price_history.php";

    //Initialize variable
    private ArrayList<TrackedItemsData> dataArrayList;
    private Activity activity;

    //Create constructor
    public TrackedItemsAdapter(Activity activity, ArrayList<TrackedItemsData> dataArrayList){
        this.activity = activity;
        this.dataArrayList = dataArrayList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Initialize variable
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tracked_items_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Initialize variable
        TrackedItemsData data = dataArrayList.get(position);

        //Set image view
        Glide.with(activity).load(data.getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.tracked_item_imageView);

        //Set text view
        holder.tracked_item_name_textView.setText(data.getName());
        holder.tracked_item_latest_price_textView.setText("RM" + data.getLatest_price());
        holder.tracked_item_tracked_price_textView.setText("RM" + data.getTrack_price());
        holder.tracked_item_platform_textView.setText(data.getPlatform());
        holder.tracked_item_tracking_id_textView.setText(data.getUnique_id());
        holder.tracked_item_productUrl_textView.setText(data.getProduct_url());

        if(Float.parseFloat(data.getLatest_price())< Float.parseFloat(data.getTrack_price())){
            holder.tracked_item_latest_price_textView.setTextColor(Color.parseColor("#FFFF382A"));
        }

        if(data.getVariant().equalsIgnoreCase("null")){
            holder.tracked_item_variation_textView.setVisibility(View.GONE);
        }
        else{
            holder.tracked_item_variation_textView.setText(data.getVariant());
        }

        if(data.getPlatform().equalsIgnoreCase("shopee")){
            holder.tracked_item_platform_textView.setTextColor(Color.parseColor("#f1582b"));
        }
        else{
            holder.tracked_item_platform_textView.setTextColor(Color.parseColor("#0f156d"));
        }

        holder.tracked_item_list_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(),PriceHistoryActivity.class);
                    intent.putExtra(TRACKING_ID,dataArrayList.get(position).getUnique_id());
                    intent.putExtra(TRACKED_ITEM_IMAGE,dataArrayList.get(position).getImage());
                    intent.putExtra(TRACKED_ITEM_NAME,dataArrayList.get(position).getName());
                    intent.putExtra(TRACKED_ITEM_LATEST_PRICE,dataArrayList.get(position).getLatest_price());
                    intent.putExtra(TRACKED_ITEM_VARIATION,dataArrayList.get(position).getVariant());
                    intent.putExtra(TRACKED_ITEM_PLATFORM,dataArrayList.get(position).getPlatform());
                    v.getContext().startActivity(intent);
            }
        });

        holder.tracked_item_open_in_app_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(data.getProduct_url());
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
        ImageView tracked_item_imageView;
        TextView tracked_item_platform_textView, tracked_item_name_textView, tracked_item_latest_price_textView, tracked_item_tracked_price_textView;
        TextView tracked_item_tracking_id_textView, tracked_item_productUrl_textView, tracked_item_variation_textView;
        MaterialCardView tracked_item_list_card_view;
        ImageButton tracked_item_open_in_app_button;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //Assign variable
            tracked_item_imageView = itemView.findViewById(R.id.tracked_item_image_view);
            tracked_item_platform_textView = itemView.findViewById(R.id.tracked_item_platform_text_view);
            tracked_item_name_textView = itemView.findViewById(R.id.tracked_item_name_text_view);
            tracked_item_latest_price_textView = itemView.findViewById(R.id.tracked_item_latest_price_text_view);
            tracked_item_tracked_price_textView = itemView.findViewById(R.id.tracked_item_tracked_price_text_view);
            tracked_item_tracking_id_textView = itemView.findViewById(R.id.track_item_tracking_id_text_view);
            tracked_item_productUrl_textView = itemView.findViewById(R.id.track_item_product_url_text_view);
            tracked_item_variation_textView = itemView.findViewById(R.id.tracked_item_variation_text_view);
            tracked_item_list_card_view = itemView.findViewById(R.id.tracked_item_list_card_view);
            tracked_item_open_in_app_button = itemView.findViewById(R.id.tracked_item_open_in_app_button);

        }
    }
}

