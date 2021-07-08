package com.example.shopmate_v1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.nfc.tech.NfcA;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationsAdapter extends RecyclerView.Adapter<NotificationsAdapter.ViewHolder> {

    private static final String mark_notification_as_read_uri = "http://192.168.43.102/shopmate_v1/mark_notification_as_read.php";

    //Initialize variable
    private ArrayList<NotificationsData> dataArrayList;
    private Activity activity;
    private RequestQueue mQueue;

    //Create constructor
    public NotificationsAdapter(Activity activity, ArrayList<NotificationsData> dataArrayList){
        this.activity = activity;
        this.dataArrayList = dataArrayList;
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //Initialize variable
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.notifications_list,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        //Initialize variable
        NotificationsData data = dataArrayList.get(position);

        //Set image view
        Glide.with(activity).load(data.getImage_url())
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(holder.notification_item_image_view);


        //Set text view
        holder.notification_message_text_view.setText(data.getMessage());
        holder.notification_item_name_text_view.setText(data.getItem_name());
        holder.notification_datetime_text_view.setText(data.getDatetime());

        if(data.isRead()==0){
            holder.notifications_list_card_view.setBackgroundColor(Color.parseColor("#ECF2FB"));
        }

        holder.notifications_list_card_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //plus set to backedn server data.getId
                markNotificationAsRead(data.getNotification_id());
                Intent intent = new Intent(v.getContext(),PriceTrackerActivity.class);
                v.getContext().startActivity(intent);
            }
        });

    }

    private void markNotificationAsRead(int notification_id) {
        mQueue = Volley.newRequestQueue(activity.getApplicationContext());

        StringRequest request = new StringRequest(Request.Method.POST, mark_notification_as_read_uri
                ,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("notification_id",(String.valueOf(notification_id)));
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                40000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(request);
    }


    @Override
    public int getItemCount() {
        return dataArrayList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        //Initialize variable
        MaterialCardView notifications_list_card_view;
        ImageView notification_item_image_view;
        TextView notification_message_text_view, notification_item_name_text_view, notification_datetime_text_view;



        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            //Assign variable
            notifications_list_card_view = itemView.findViewById(R.id.notifications_list_card_view);
            notification_item_image_view = itemView.findViewById(R.id.notification_item_image_view);
            notification_message_text_view = itemView.findViewById(R.id.notification_message_text_view);
            notification_item_name_text_view = itemView.findViewById(R.id.notification_item_name_text_view);
            notification_datetime_text_view = itemView.findViewById(R.id.notification_datetime_text_view);


        }
    }
}
