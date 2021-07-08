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
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.card.MaterialCardView;
import java.util.ArrayList;


public class CompareListItemAdapter extends RecyclerView.Adapter<CompareListItemAdapter.ViewHolder> {

    //Initialize variable
    private ArrayList<CompareListItemData> dataArrayList;
    private Activity activity;


    //Create constructor
    public CompareListItemAdapter(Activity activity, ArrayList<CompareListItemData> dataArrayList){
        this.activity = activity;
        this.dataArrayList = dataArrayList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Initialize variable
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.compare_list_items,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Initialize variable
        CompareListItemData data = dataArrayList.get(position);

        //Set image view
        Glide.with(activity).load(data.getImage())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.compare_list_item__image_view);

        //Set text view
        holder.compare_list_item_platform_text_view.setText(data.getPlatform());
        holder.compare_list_item_name_text_view.setText(data.getName());
        holder.compare_list_item_variation_text_view.setText(data.getVariation());

        holder.compare_list_price_text_view.setText(data.getPrice());
        holder.compare_list_price_weightage_text_view.setText("(" + String.valueOf(data.getPrice_weightage()) + "%)");
        holder.compare_list_location_text_view.setText(data.getLocation());
        holder.compare_list_location_weightage_text_view.setText("(" + String.valueOf(data.getLocation_weightage()) + "%)");
        holder.compare_list_sentiment_text_view.setText(data.getSentiment_score());
        holder.compare_list_sentiment_weightage_text_view.setText("(" + String.valueOf(data.getSentiment_weightage()) + "%)" );

        if(data.getPlatform().equalsIgnoreCase("shopee")){
            holder.compare_list_item_platform_text_view.setTextColor(Color.parseColor("#f1582b"));
        }
        else{
            holder.compare_list_item_platform_text_view.setTextColor(Color.parseColor("#0f156d"));
        }

        holder.compare_list_item_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(data.getProduct_url());
                v.getContext().startActivity(new Intent(Intent.ACTION_VIEW,uri));
            }
        });

        //set the thumb layout to be visible to the first item in the recyclerview
        if(position == 0){
            holder.thumb_up_icon_linear_layout.setVisibility(View.VISIBLE);
            holder.compare_list_item_card_view.setStrokeColor(Color.parseColor("#FFC107"));
            holder.compare_list_item_card_view.setCardBackgroundColor(Color.parseColor("#D1E2FA"));
        }
        else{
            holder.thumb_up_icon_linear_layout.setVisibility(View.GONE);
            holder.compare_list_item_card_view.setStrokeColor(Color.parseColor("#D3DADF"));
            holder.compare_list_item_card_view.setCardBackgroundColor(Color.parseColor("#FFFFFFFF"));
        }


    }

    @Override
    public int getItemCount() {
        return dataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //Initialize variable
        ImageView compare_list_item__image_view;
        TextView compare_list_item_platform_text_view, compare_list_item_name_text_view, compare_list_item_variation_text_view;
        TextView compare_list_price_weightage_text_view, compare_list_price_text_view;
        TextView compare_list_location_text_view, compare_list_location_weightage_text_view;
        TextView compare_list_sentiment_text_view, compare_list_sentiment_weightage_text_view;
        MaterialCardView compare_list_item_card_view;
        LinearLayout thumb_up_icon_linear_layout;


        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //Assign variable
            compare_list_item__image_view = itemView.findViewById(R.id.compare_list_item__image_view);
            compare_list_item_platform_text_view = itemView.findViewById(R.id.compare_list_item_platform_text_view);
            compare_list_item_name_text_view = itemView.findViewById(R.id.compare_list_item_name_text_view);
            compare_list_item_variation_text_view = itemView.findViewById(R.id.compare_list_item_variation_text_view);
            compare_list_price_weightage_text_view = itemView.findViewById(R.id.compare_list_price_weightage_text_view);
            compare_list_price_text_view = itemView.findViewById(R.id.compare_list_price_text_view);
            compare_list_location_text_view = itemView.findViewById(R.id.compare_list_location_text_view);
            compare_list_location_weightage_text_view = itemView.findViewById(R.id.compare_list_location_weightage_text_view);
            compare_list_sentiment_text_view = itemView.findViewById(R.id.compare_list_sentiment_text_view);
            compare_list_sentiment_weightage_text_view = itemView.findViewById(R.id.compare_list_sentiment_weightage_text_view);
            compare_list_item_card_view = itemView.findViewById(R.id.compare_list_item_card_view);
            thumb_up_icon_linear_layout = itemView.findViewById(R.id.thumb_up_icon_linear_layout);
        }
    }
}

