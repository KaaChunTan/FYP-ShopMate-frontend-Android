package com.example.shopmate_v1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class PriceHistoryActivity extends AppCompatActivity {

    private static final String get_price_history_uri = "http://192.168.43.102/shopmate_v1/get_price_history.php";

    String tracking_id,tracked_item_image, tracked_item_name,tracked_item_variation,tracked_item_platform, tracked_item_latest_prie;
    long reference_timestamp;
    ArrayList<String> date_list = new ArrayList<>();
    ArrayList<Float> price_list = new ArrayList<>();
    ArrayList<Float> converted_timeStamp_list = new ArrayList<>();

    LinearLayout tracked_item_info_linear_layout;
    ImageButton cancel_button;
    ImageView tracked_item_imageView;
    TextView tracked_item_platform_textView, tracked_item_name_textView, tracked_item_latest_price_textView,tracked_item_variation_textView;
    LineChart price_history_line_chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_history);
        tracked_item_info_linear_layout = findViewById(R.id.tracked_item_info_linear_layout);
        cancel_button = findViewById(R.id.cancel_button);

        Intent intent = getIntent();
        tracking_id = intent.getStringExtra(TrackedItemsAdapter.TRACKING_ID);
        tracked_item_image = intent.getStringExtra(TrackedItemsAdapter.TRACKED_ITEM_IMAGE);
        tracked_item_name = intent.getStringExtra(TrackedItemsAdapter.TRACKED_ITEM_NAME);
        tracked_item_latest_prie = intent.getStringExtra(TrackedItemsAdapter.TRACKED_ITEM_LATEST_PRICE);
        tracked_item_platform = intent.getStringExtra(TrackedItemsAdapter.TRACKED_ITEM_PLATFORM);
        tracked_item_variation = intent.getStringExtra(TrackedItemsAdapter.TRACKED_ITEM_VARIATION);
        displayTrackedItemInfo();
        getPriceHistory(tracking_id);

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PriceHistoryActivity.super.onBackPressed();
            }
        });

    }

    private void getPriceHistory(String uuid) {
        RequestQueue mQueue = Volley.newRequestQueue(this);

        StringRequest request = new StringRequest(Request.Method.POST, get_price_history_uri
                ,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{

                    JSONObject object = new JSONObject(response);
                    boolean error = object.getBoolean("error");
                    if(!error){

                        JSONObject tracked_items = object.getJSONObject("result");
                        JSONArray result_array = tracked_items.getJSONArray("price_tracking");
                        for (int i = 0 ; i < result_array.length();i++){
                            JSONObject item = result_array.getJSONObject(i);

                            price_list.add(Float.parseFloat(item.getString("price").replace("\n","")));
                            date_list.add(item.getString("track_date"));
                        }
                        converted_timeStamp_list = convertToTimeStampArray(date_list);

                        displayLineGraph();

                    }
                    else{
                        String errorMsg = object.getString("message");
                        //Toast.makeText(getApplicationContext(),errorMsg,Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();}


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
                params.put("uuid",uuid);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                40000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(request);
    }

    public ArrayList<Float> convertToTimeStampArray(ArrayList<String> date_list){
        Timestamp ts = Timestamp.valueOf(date_list.get(0)); //this is to get the first timestamp
        reference_timestamp = ts.getTime()/1000;
        ArrayList<Float> converted_timeStamp_list = new ArrayList<>();

        for (int i = 0; i<date_list.size(); i++){
            Timestamp ts2 = Timestamp.valueOf(date_list.get(i));
            long x_new = ts2.getTime()/1000 - reference_timestamp;
            float x = (float)x_new;
            converted_timeStamp_list.add(x);
        }
        return converted_timeStamp_list;
    }

    public void displayTrackedItemInfo(){
        tracked_item_info_linear_layout.setVisibility(View.GONE);

        tracked_item_imageView = findViewById(R.id.tracked_item_image_view);
        tracked_item_platform_textView = findViewById(R.id.tracked_item_platform_text_view);
        tracked_item_name_textView = findViewById(R.id.tracked_item_name_text_view);
        tracked_item_latest_price_textView = findViewById(R.id.tracked_item_latest_price_text_view);
        tracked_item_variation_textView = findViewById(R.id.tracked_item_variation_text_view);

        Glide.with(this).load(tracked_item_image)
                .diskCacheStrategy(DiskCacheStrategy.ALL)
                .into(tracked_item_imageView);

        //Set text view
        tracked_item_name_textView.setText(tracked_item_name);
        tracked_item_latest_price_textView.setText("RM" + tracked_item_latest_prie);
        tracked_item_platform_textView.setText(tracked_item_platform);


        if(tracked_item_variation.equalsIgnoreCase("null")){
            tracked_item_variation_textView.setVisibility(View.GONE);
        }
        else{
            tracked_item_variation_textView.setText(tracked_item_variation);
        }

        if(tracked_item_platform.equalsIgnoreCase("shopee")){
            tracked_item_platform_textView.setTextColor(Color.parseColor("#f1582b"));
        }
        else{
            tracked_item_platform_textView.setTextColor(Color.parseColor("#0f156d"));
        }
        tracked_item_info_linear_layout.setVisibility(View.VISIBLE);
    }

    public void displayLineGraph(){
        price_history_line_chart = findViewById(R.id.price_history_line_chart);
        price_history_line_chart.setDragEnabled(true);
        price_history_line_chart.setScaleEnabled(false);
        price_history_line_chart.getLegend().setEnabled(false);
        price_history_line_chart.getDescription().setEnabled(false);
        price_history_line_chart.setDrawGridBackground(true);

        YAxis mYAxis = price_history_line_chart.getAxisLeft();
        mYAxis.setEnabled(true);
        mYAxis.setDrawGridLines(false);
        //mYAxis.setLabelCount(10, true);
        //mYAxis.setGranularity(10f);
        //mYAxis.setAxisMaximum(200f);
        //mYAxis.setAxisMinimum(0f);

        YAxis rightYAxis = price_history_line_chart.getAxisRight();
        rightYAxis.setEnabled(false);


        ArrayList<Entry> yValues = new ArrayList<>();

        Iterator<Float> it1 = price_list.iterator();
        Iterator<Float> it2 = converted_timeStamp_list.iterator();

        while (it1.hasNext() && it2.hasNext()) {
            Float a = it2.next();
            Float b = it1.next();
            Log.e("yValues: ",a.toString()+" and " + b.toString());
            yValues.add(new Entry(a,b));
        }
        LineDataSet set = new LineDataSet(yValues,"");
        set.setMode(LineDataSet.Mode.HORIZONTAL_BEZIER);

        set.setFillAlpha(110);
        if(tracked_item_platform.equalsIgnoreCase("shopee")){
            set.setColor(Color.parseColor("#f1582b"));
        }
        else{
            set.setColor(Color.parseColor("#0f156d"));
        }
        set.setCircleColor(Color.BLACK);
        set.setDrawCircleHole(false);
        set.setLineWidth(3f);
        set.setValueTextSize(10f);
        set.setValueTextColor(Color.BLACK);

        ArrayList<ILineDataSet> dataset = new ArrayList<>();
        dataset.add(set);

        LineData data = new LineData( dataset);
        price_history_line_chart.setData(data);
        price_history_line_chart.invalidate();

        IAxisValueFormatter xAxisFormatter = new DateAxisValueFormatter(reference_timestamp);
        XAxis xAxis = price_history_line_chart.getXAxis();
        xAxis.setValueFormatter(xAxisFormatter);
        xAxis.setLabelCount(price_list.size(),true);
        xAxis.setDrawGridLines(false);

    }
}