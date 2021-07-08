package com.example.shopmate_v1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tbuonomo.viewpagerdotsindicator.WormDotsIndicator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HomeInterface extends AppCompatActivity {

    private static final String load_main_interface_uri = "http://192.168.43.102/shopmate_v1/load_main_interface.php";
    private static final String get_user_notifications_count_uri = "http://192.168.43.102/shopmate_v1/get_user_notifications_count.php";
    private static final String CHANNEL_ID = "priceDrop_fcm";
    private static String test = "123";

    LinearLayout loading_page, home_interface_layout;
    CardView notification_alert;
    RelativeLayout notification_button_layout;
    BottomNavigationView bottom_navigation;
    EditText link_to_search_page;
    ViewPager lazada_banner_view_pager;
    WormDotsIndicator dot_indicator;
    RecyclerView recent_viewed_recycler_view;
    RecyclerView shopee_top_products_recycler_view;
    RecyclerView shopee_mall_shops_recycler_view;
    SwipeRefreshLayout home_page_refresh_layout;

    DatabaseHelper databaseHelper;
    ArrayList<SearchResultData> listData = new ArrayList<>();
    RecentViewedAdapter adapter;
    String user_id;

    private static ArrayList<ShopeeMallShopsData>  mall_shops_list = new ArrayList<>();
    ShopeeMallShopsAdapter mall_shops_adapter;
    private static ArrayList<ShopeeTopProductsData>  top_products_list = new ArrayList<>();
    ShopeeTopProductsAdapter top_products_adapter;
    private static ArrayList<LazadaBannerData>  banner_list = new ArrayList<>();
    LazadaBannerAdapter lazada_banner_adapter ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_interface);
        createNotificationChannel();

        loading_page = findViewById(R.id.loading_page);
        home_interface_layout = findViewById(R.id.home_interface_layout);
        notification_button_layout = findViewById(R.id.notification_button_layout);
        notification_alert = findViewById(R.id.notification_bell).findViewById(R.id.notication_alert);

        recent_viewed_recycler_view = findViewById(R.id.recent_viewed_recycler_view);
        bottom_navigation = findViewById(R.id.bottom_navigation);
        link_to_search_page = findViewById(R.id.link_to_search_page);
        shopee_mall_shops_recycler_view = findViewById(R.id.shopee_mall_shops_recycler_view);
        shopee_top_products_recycler_view = findViewById(R.id.shopee_top_products_recycler_view);
        lazada_banner_view_pager = findViewById(R.id.lazada_banner_view_pager);
        dot_indicator = findViewById(R.id.dot_indicator);
        home_page_refresh_layout = findViewById(R.id.home_page_refresh_layout);

        //when there is no data stored in the share d
        if(mall_shops_list.size()== 0 ||
                top_products_list.size()== 0 ||
                banner_list.size()== 0){
            loading_page.setVisibility(View.VISIBLE);
            home_interface_layout.setVisibility(View.GONE);
            scrapeData();
            //displayScrapedData();
        }
        //when there is scraped data
        else{
//            mall_shops_list = SharedPrefManager.getInstans(getApplicationContext()).getShopeeMallShops();
//            top_products_list = SharedPrefManager.getInstans(getApplicationContext()).getShopeeTopProducts();
//            banner_list = SharedPrefManager.getInstans(getApplicationContext()).getLazadaBanners();
            displayScrapedData();
        }

        //Check for the notification count
        if(!SharedPrefManager.getInstans(this).isLogin()){
            notification_alert.setVisibility(View.GONE);
        }
        else{
            user_id = SharedPrefManager.getInstans(this).getUserId();
            getUserNotificationsCount(user_id);
        }

        //recent view section
        databaseHelper = new DatabaseHelper(this);
        getRecentViewed();
        adapter = new RecentViewedAdapter(HomeInterface.this,listData);
        recent_viewed_recycler_view.setAdapter(adapter);

        //set home selected
        bottom_navigation.setSelectedItemId(R.id.home);

        //perform itemSelectedListener
        bottom_navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        return true;  // this is the current activity
                    case R.id.compare_list:
                        startActivity(new Intent(getApplicationContext(),CompareListActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.price_tracker:
                        startActivity(new Intent(getApplicationContext(),PriceTrackerActivity.class));
                        overridePendingTransition(0,0);
                        return true;
                    case R.id.profile:
                        startActivity(new Intent(getApplicationContext(), ProfilePageActivity.class));
                        overridePendingTransition(0, 0);
                        return true;
                }
                return false;
            }
        });

        link_to_search_page.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),SearchPageActivity.class));
            }
        });

        notification_button_layout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),NotificationsActivity.class));
                overridePendingTransition(R.anim.in,R.anim.out);
            }
        });

        home_page_refresh_layout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getRecentViewed();
                adapter.notifyDataSetChanged();
                home_page_refresh_layout.setRefreshing(false);
            }
        });
    }

    private void getUserNotificationsCount(String user_id) {
        RequestQueue mQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest request = new StringRequest(Request.Method.POST, get_user_notifications_count_uri
                ,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try{
                    JSONObject object = new JSONObject(response);
                    boolean error = object.getBoolean("error");
                    if(!error){
                        int notifications_count = object.getInt("result");
                        if(notifications_count>0){
                            notification_alert.setVisibility(View.VISIBLE);
                        }
                        else{
                            notification_alert.setVisibility(View.GONE);
                        }

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

    private void scrapeData() {
        RequestQueue requestQueue = Volley.newRequestQueue(this);
        StringRequest request = new StringRequest(Request.Method.GET, load_main_interface_uri,new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject object = new JSONObject(response);
                    boolean success = object.getBoolean("success");
                    if(success){
                        //get shopee top products
                        JSONObject result_array = object.getJSONObject("result").getJSONObject("shopee_top_products");
                        for (int i = 0 ; i < result_array.length();i++){
                            JSONObject item = result_array.getJSONObject(String.format("%d",i));

                            ShopeeTopProductsData data= new ShopeeTopProductsData();
                            data.setImage(item.getString("image"));
                            data.setName(item.getString("name"));
                            data.setUrl(item.getString("url"));

                            top_products_list.add(data);

                        }
                        //get shopee mall shops
                        JSONObject result_array2 = object.getJSONObject("result").getJSONObject("shopee_mall_shops");
                        for (int i = 0 ; i < result_array2.length();i++){
                            JSONObject item = result_array2.getJSONObject(String.format("%d",i));

                            ShopeeMallShopsData data= new ShopeeMallShopsData();
                            data.setImage(item.getString("image"));

                            data.setPromo_text(item.optString("promo_text"));
                            data.setUrl(item.getString("url"));

                            mall_shops_list.add(data);

                        }
                        //get lazada banner
                        JSONObject result_array3 = object.getJSONObject("result").getJSONObject("lazada_banners");
                        for (int i = 0 ; i < result_array3.length();i++){
                            JSONObject item = result_array3.getJSONObject(String.format("%d",i));

                            LazadaBannerData data= new LazadaBannerData();
                            data.setImage(item.getString("image"));

                            data.setName(item.optString("name"));
                            data.setUrl(item.getString("url"));

                            banner_list.add(data);

                        }
                    }
                    else{
                        String errorMsg = object.getString("message");
                        Toast.makeText(getApplicationContext(),errorMsg,Toast.LENGTH_LONG).show();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }

                displayScrapedData();

                //store data into the shared preferences
//                SharedPrefManager.getInstans(getApplicationContext()).saveShopeeMallsShops(mall_shops_list);
//                SharedPrefManager.getInstans(getApplicationContext()).saveShopeeTopProducts(top_products_list);
//                SharedPrefManager.getInstans(getApplicationContext()).saveLazadaBanners(banner_list);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
        request.setRetryPolicy(new DefaultRetryPolicy(
                40000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(request);
    }


    private void getRecentViewed() {
        Cursor db_data = databaseHelper.getRecentViewed();
        listData.clear();
        while(db_data.moveToNext()){
            SearchResultData data= new SearchResultData();
            data.setItem_image(db_data.getString(db_data.getColumnIndex("item_image")));
            data.setProduct_url(db_data.getString(db_data.getColumnIndex("product_url")));
            data.setItem_name(db_data.getString(db_data.getColumnIndex("item_name")));
            data.setDiscounted_price(db_data.getString(db_data.getColumnIndex("price")));
            data.setPlatform(db_data.getString(db_data.getColumnIndex("platform")));
            data.setRatingScore(db_data.getInt(db_data.getColumnIndex("rating_score")));
            data.setShop_id(db_data.getString(db_data.getColumnIndex("shop_id")));
            data.setItem_id(db_data.getString(db_data.getColumnIndex("item_id")));
            data.setLocation(db_data.getString(db_data.getColumnIndex("location")));
            data.setReview_count(db_data.getString(db_data.getColumnIndex("review_count")));

            listData.add(data);
        }

    }

    private void displayScrapedData(){

        mall_shops_adapter = new ShopeeMallShopsAdapter(HomeInterface.this,mall_shops_list);
        top_products_adapter = new ShopeeTopProductsAdapter(HomeInterface.this,top_products_list);
        lazada_banner_adapter = new LazadaBannerAdapter(HomeInterface.this,banner_list);

        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.HORIZONTAL);
        shopee_top_products_recycler_view.setLayoutManager(staggeredGridLayoutManager);

        //Set adapter
        shopee_mall_shops_recycler_view.setAdapter(mall_shops_adapter);
        shopee_top_products_recycler_view.setAdapter(top_products_adapter);
        lazada_banner_view_pager.setAdapter(lazada_banner_adapter);
        dot_indicator.setViewPager(lazada_banner_view_pager);

        loading_page.setVisibility(View.GONE);
        home_interface_layout.setVisibility(View.VISIBLE);
    }

//    @Override
//    protected void onDestroy() {
//        Log.e("first","true");
//        SharedPrefManager.getInstans(getApplicationContext()).deleteScrapedData();
//        Log.e("second","true");
//        super.onDestroy();
//        // delete the data stored in the shared preferences when the app is closed
//
//    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Hi";
            String description = "It's here";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
//        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);
//
//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify((int)(System.currentTimeMillis()/1000), mBuilder.build());
    }
}