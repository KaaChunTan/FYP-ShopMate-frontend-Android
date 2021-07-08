package com.example.shopmate_v1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.content.Intent;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PriceTrackerActivity extends AppCompatActivity {

    private static final String get_tracking_items_uri = "http://192.168.43.102/shopmate_v1/get_tracking_items.php";
    private static final String remove_tracking_items_uri = "http://192.168.43.102/shopmate_v1/remove_tracking_items.php";

    LinearLayout price_tracker_linear_layout;
    ImageButton cancel_button;
    BottomNavigationView bottom_navigation;
    RecyclerView recyclerView;
    ArrayList<TrackedItemsData> dataArrayList = new ArrayList<>();
    TrackedItemsAdapter adapter;
    RequestQueue mQueue;
    String user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_price_tracker);

        price_tracker_linear_layout = findViewById(R.id.price_tracker_linear_layout);
        cancel_button = findViewById(R.id.cancel_button);
        recyclerView = findViewById(R.id.tracked_item_recycler_view);
        bottom_navigation = findViewById(R.id.bottom_navigation);
        bottom_navigation.setSelectedItemId(R.id.price_tracker);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        if(!SharedPrefManager.getInstans(this).isLogin()){
            finish();
            Intent intent = new Intent(PriceTrackerActivity.this, ProfilePageActivity.class);
            startActivity(intent);
        }
        else{
            user_id = SharedPrefManager.getInstans(this).getUserId();
            price_tracker_linear_layout.setVisibility(View.GONE);
            getTrackingItems(user_id);
        }


        //set Onclick listener
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottom_navigation.getSelectedItemId() == R.id.home) {

                } else {
                    PriceTrackerActivity.super.onBackPressed();
                    bottom_navigation.setSelectedItemId(R.id.home);
                }
            }
        });



        //perform itemSelectedListener
        bottom_navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        startActivity(new Intent(getApplicationContext(), HomeInterface.class));
                        overridePendingTransition(0, 0);
                        return true;  // this is the current activity
                    case R.id.compare_list:
                        startActivity(new Intent(getApplicationContext(), CompareListActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.price_tracker:
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(), ProfilePageActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            String uuid = dataArrayList.get(viewHolder.getAdapterPosition()).getUnique_id();
            removeTrackingItems(user_id, uuid);
            dataArrayList.remove(viewHolder.getAdapterPosition());
            adapter.notifyDataSetChanged();
        }
    };

    @Override
    public void onBackPressed() {
        if (bottom_navigation.getSelectedItemId() == R.id.home) {
            super.onBackPressed();
        } else {
            bottom_navigation.setSelectedItemId(R.id.home);
        }
    }

    private void getTrackingItems(String user_id) {
        mQueue = Volley.newRequestQueue(getApplicationContext());
        //String keyword_search = "protex shower";

        StringRequest request = new StringRequest(Request.Method.POST, get_tracking_items_uri
                ,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{

                    JSONObject object = new JSONObject(response);
                    boolean error = object.getBoolean("error");
                    if(!error){

                        JSONObject tracked_items = object.getJSONObject("result");
                        JSONArray result_array = tracked_items.getJSONArray("tracked_items");
                        for (int i = 0 ; i < result_array.length();i++){
                            JSONObject item = result_array.getJSONObject(i);

                            //Initialize SearchResultData
                            TrackedItemsData data= new TrackedItemsData();

                            data.setUnique_id(item.getString("unique_id"));
                            data.setName(item.getString("name"));
                            data.setProduct_url(item.getString("product_url"));
                            data.setImage(item.getString("image"));
                            data.setTrack_price(item.getString("track_price"));
                            data.setLatest_price(item.getString("latest_price").replace('\n',' '));
                            data.setVariant(item.getString("variant"));
                            data.setPlatform(item.getString("platform"));


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
                adapter = new TrackedItemsAdapter(PriceTrackerActivity.this,dataArrayList);


                //Set adapter
                recyclerView.setAdapter(adapter);
                price_tracker_linear_layout.setVisibility(View.VISIBLE);

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
                params.put("user_id",user_id);
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                40000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(request);
    }

    private void removeTrackingItems(String user_id, String uuid) {
        mQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest request = new StringRequest(Request.Method.POST, remove_tracking_items_uri
                ,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{

                    JSONObject object = new JSONObject(response);
                    boolean error = object.getBoolean("error");
                    if(!error){
                        String msg = object.getString("msg");
                        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                    }
                    else{
                        String errorMsg = object.getString("error_msg");
                        Toast.makeText(getApplicationContext(),errorMsg,Toast.LENGTH_LONG).show();
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
                params.put("user_id",user_id);
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
}