package com.example.shopmate_v1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class SearchResultAdapter extends RecyclerView.Adapter<SearchResultAdapter.ViewHolder> {

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
    public SearchResultAdapter(Activity activity, ArrayList<SearchResultData> dataArrayList){
        this.activity = activity;
        this.dataArrayList = dataArrayList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Initialize variable
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row_main,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Initialize variable
        SearchResultData data = dataArrayList.get(position);

        //Set image view
        Glide.with(activity).load(data.getItem_image())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.item_imageView);


        //Set text view
        holder.itemName_textView.setText(data.getItem_name());
        holder.platform_textView.setText(data.getPlatform());
        holder.discountedPrice_textView.setText(data.getDisplay_price());
        holder.originalPrice_textView.setText(data.getOriginal_price());
        holder.originalPrice_textView.setPaintFlags(holder.originalPrice_textView.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        holder.location_textView.setText(data.getLocation());
        holder.productUrl_textView.setText(data.getProduct_url());
        holder.shop_id_textView.setText(data.getShop_id());
        holder.item_id_textView.setText(data.getItem_id());
        holder.platform_textView.setText(data.getPlatform());
        holder.review_count_textView.setText("("+data.getReview_count()+")");

        holder.ratingScore_ratingBar.setRating(data.getRatingScore());

        if(data.getPlatform().equalsIgnoreCase("shopee")){
            holder.platform_textView.setTextColor(Color.parseColor("#f1582b"));
        }
        else{
            holder.platform_textView.setTextColor(Color.parseColor("#0f156d"));
        }
    }


    @Override
    public int getItemCount() {
        return dataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //Initialize variable
        ImageView item_imageView;
        TextView itemName_textView, platform_textView, discountedPrice_textView, originalPrice_textView, location_textView, productUrl_textView;
        TextView shop_id_textView, item_id_textView, review_count_textView;
        RatingBar ratingScore_ratingBar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //Assign variable
            item_imageView = itemView.findViewById(R.id.item_image_view);
            platform_textView = itemView.findViewById(R.id.platform_text_view);
            itemName_textView = itemView.findViewById(R.id.itemName_text_view);
            discountedPrice_textView = itemView.findViewById(R.id.discountedPrice_text_view);
            originalPrice_textView = itemView.findViewById(R.id.originalPrice_text_view);
            location_textView = itemView.findViewById(R.id.location_text_view);
            ratingScore_ratingBar = itemView.findViewById(R.id.ratingScore_rating_bar);
            productUrl_textView = itemView.findViewById(R.id.productUrl_text_view);
            shop_id_textView = itemView.findViewById(R.id.shop_id_text_view);
            item_id_textView = itemView.findViewById(R.id.item_id_text_view);
            review_count_textView = itemView.findViewById(R.id.review_count_text_view);

            //set onClick listener for the item
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    DatabaseHelper databaseHelper;
                    databaseHelper = new DatabaseHelper(v.getContext());

                    databaseHelper.addRecentViewed(dataArrayList.get(getAdapterPosition()).getItem_name(),
                            dataArrayList.get(getAdapterPosition()).getItem_image(),
                            dataArrayList.get(getAdapterPosition()).getDiscounted_price(),
                            dataArrayList.get(getAdapterPosition()).getPlatform(),
                            dataArrayList.get(getAdapterPosition()).getProduct_url(),
                            dataArrayList.get(getAdapterPosition()).getRatingScore(),
                            dataArrayList.get(getAdapterPosition()).getLocation(),
                            dataArrayList.get(getAdapterPosition()).getItem_id(),
                            dataArrayList.get(getAdapterPosition()).getShop_id(),
                            dataArrayList.get(getAdapterPosition()).getReview_count());



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
