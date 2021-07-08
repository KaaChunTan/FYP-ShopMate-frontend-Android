package com.example.shopmate_v1;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.github.mikephil.charting.charts.LineChart;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class NotificationsActivity extends Activity {

    private static final String get_user_notifications_uri = "http://192.168.43.102/shopmate_v1/get_user_notifications.php";

    LinearLayout notification_linear_layout, notification_error_linear_layout;
    ImageButton cancel_button;
    RecyclerView notification_recycler_view;
    ArrayList<NotificationsData> dataArrayList = new ArrayList<>();
    NotificationsAdapter adapter;
    RequestQueue mQueue;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        notification_linear_layout = findViewById(R.id.notification_linear_layout);
        notification_error_linear_layout = findViewById(R.id.notification_error_linear_layout);
        cancel_button = findViewById(R.id.cancel_button);
        notification_recycler_view = findViewById(R.id.notification_recycler_view);
        notification_linear_layout.setVisibility(View.GONE);

        if(!SharedPrefManager.getInstans(this).isLogin()){
            notification_error_linear_layout.setVisibility(View.VISIBLE);
        }
        else{
            user_id = SharedPrefManager.getInstans(this).getUserId();
            getUserNotifications(user_id);
        }

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                NotificationsActivity.super.onBackPressed();
            }
        });

    }

    private void getUserNotifications(String user_id) {
        mQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest request = new StringRequest(Request.Method.POST, get_user_notifications_uri
                ,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    JSONObject object = new JSONObject(response);
                    boolean error = object.getBoolean("error");
                    if(!error){

                        JSONObject tracked_items = object.getJSONObject("result");
                        JSONArray result_array = tracked_items.getJSONArray("notifications");
                        for (int i = 0 ; i < result_array.length();i++){
                            JSONObject item = result_array.getJSONObject(i);

                            //Initialize SearchResultData
                            NotificationsData data= new NotificationsData();

                            data.setNotification_id(item.getInt("notification_id"));
                            data.setImage_url(item.getString("image_url"));
                            data.setMessage(item.getString("message"));
                            data.setDatetime(item.getString("datetime"));
                            data.setItem_name(item.getString("item_name"));
                            data.setRead(item.getInt("isRead"));


                            dataArrayList.add(data);

                        }
                    }
                    else{
                        String errorMsg = object.getString("message");
                        Toast.makeText(getApplicationContext(),errorMsg,Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();}


                //Initialize adapter
                adapter = new NotificationsAdapter(NotificationsActivity.this,dataArrayList);

                //Set adapter
                notification_recycler_view.setAdapter(adapter);
                notification_linear_layout.setVisibility(View.VISIBLE);

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
                params.put("uuid",user_id);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                40000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(request);
    }
}
