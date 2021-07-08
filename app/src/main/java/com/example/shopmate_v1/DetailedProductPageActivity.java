package com.example.shopmate_v1;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.NotificationCompat;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.transition.AutoTransition;
import android.transition.Slide;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.denzcoskun.imageslider.ImageSlider;
import com.denzcoskun.imageslider.constants.ScaleTypes;
import com.denzcoskun.imageslider.models.SlideModel;
import com.google.android.material.card.MaterialCardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DetailedProductPageActivity extends Activity {

    private static final String detailed_info_uri = "http://192.168.43.102/shopmate_v1/get_detailed_info.php";
    private static final String sentiment_predictions_uri = "http://192.168.43.102/shopmate_v1/sentiment_predictions.php";
    private static final String check_item_tracked_uri = "http://192.168.43.102/shopmate_v1/check_item_tracked.php";
    private static final String add_tracked_item_uri = "http://192.168.43.102/shopmate_v1/add_tracking_items.php";
    private static final String remove_tracked_item_uri = "http://192.168.43.102/shopmate_v1/remove_tracking_items.php";

    //Initialize variable
    String scrape_url;
    String platform;
    int rating_score;
    String location;
    String item_name;
    String item_description;
    String item_ori_price;
    String item_discounted_price;
    String review_count;
    String item_id;
    String shop_id;
    String product_url;
    List<String> item_images = new ArrayList<>();
    ArrayList<VariationData> variation_list = new ArrayList<>();
    VariationListAdapter adapter;
    String selected_variation ="";

    String total_reviews="";
    String positive_review_count="";
    List<String> positive_reviews_list = new ArrayList<>();
    String negative_review_count="";
    List<String> negative_reviews_list = new ArrayList<>();
    String sentiment_score="";

    DatabaseHelper databaseHelper;

    //Initialize widgets
    ProgressBar progress_bar;
    ImageButton cancel_button;
    ImageSlider imageSlider;
    TextView platform_text_view;
    TextView item_name_text_view;
    TextView item_discountedPrice_text_view;
    TextView item_originalPrice_text_view;
    ImageButton price_track_button;
    RatingBar ratingScore_rating_bar;
    TextView review_count_text_view;
    TextView location_text_view;
    CardView variation_card_view;
    ImageButton variation_collapse_button;
    CardView selected_variation_card_view;
    TextView selected_variation_text_view;
    LinearLayout variation_expandable_layout;
    RecyclerView variation_list_recycler_view;
    ImageButton desc_collapse_button;
    CardView description_card_view;
    LinearLayout item_description_expandable_layout;
    TextView item_description_text_view;
    CardView review_sentiment_score_card_view;
    ImageButton sentiment_score_collapse_button;
    LinearLayout review_sentiment_score_progress_bar_layout;
    LinearLayout review_sentiment_score_expandable_layout;
    MaterialCardView review_sentiment_score_box;
    TextView sentiment_score_text_view;
    TextView total_reviews_text_view;
    LinearLayout no_reviews_message_layout;
    CardView positive_review_card_view;
    TextView positive_review_count_text_view;
    ImageButton positive_review_collapse_button;
    LinearLayout positive_review_expandable_layout;
    ListView positive_review_list_view;
    CardView negative_review_card_view;
    TextView negative_review_count_text_view;
    ImageButton negative_review_collapse_button;
    LinearLayout negative_review_expandable_layout;
    ListView negative_review_list_view;
    LinearLayout error_layout;
    NestedScrollView scroll_view;
    LinearLayout toolbar;
    Button toolbar_button1;
    Button toolbar_button2;

    RequestQueue requestQueue;

    boolean isLogin = false;
    String user_id = "";
    boolean isTracked = false;
    String tracking_id = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_product_page);

        scroll_view = findViewById(R.id.scroll_view);
        toolbar = findViewById(R.id.toolbar);
        progress_bar = findViewById(R.id.progress_bar);

        progress_bar.setVisibility(View.VISIBLE);
        scroll_view.setVisibility(View.GONE);
        toolbar.setVisibility(View.GONE);


//        scrape_url = "https://shopee.com.my/api/v2/item/get?itemid=8702242378&shopid=277364473";
//        platform = "Shopee";
//        rating_score = 0;
//        location = "akdfaskdfhnaklsdnfklandklfnalksdf";
//        shop_id = "277364473";
//        item_id = "8702242378";
//        review_count = "110";

//        scrape_url = "https://www.lazada.com.my/products/sandisk-ultra-luxe-32gb-64gb-128gb-256gb-usb-31-flash-drive-i1313494664-s4031392180.html";
//        platform = "Lazada";
//        rating_score = 3;
//        location = "akdfaskdfhnaklsdnfklandklfnalksdf";
//        shop_id = "229";
//        item_id = "1313494664";
//        review_count = "184";

        Intent intent = getIntent();
        scrape_url = intent.getStringExtra(SearchResultAdapter.PRODUCT_URL);
        rating_score = intent.getIntExtra(SearchResultAdapter.RATING_SCORE,0);
        location = intent.getStringExtra(SearchResultAdapter.LOCATION);
        platform = intent.getStringExtra(SearchResultAdapter.PLATFORM);
        shop_id = intent.getStringExtra(SearchResultAdapter.SHOP_ID);
        item_id = intent.getStringExtra(SearchResultAdapter.ITEM_ID);
        review_count = intent.getStringExtra(SearchResultAdapter.REVIEW_COUNT);

        requestQueue = Volley.newRequestQueue(getApplicationContext());
        databaseHelper = new DatabaseHelper(this);

        setButtonsListener();
        getDetailedInfoData();



    }

    private void setButtonsListener() {

        cancel_button = this.findViewById(R.id.cancel_button);
        price_track_button = this.findViewById(R.id.price_track_button);
        desc_collapse_button = findViewById(R.id.desc_collapse_button);
        variation_collapse_button = findViewById(R.id.variation_collapse_button);
        sentiment_score_collapse_button = findViewById(R.id.sentiment_score_collapse_button);
        positive_review_collapse_button = findViewById(R.id.positive_review_collapse_button);
        negative_review_collapse_button = findViewById(R.id.negative_review_collapse_button);

        description_card_view = findViewById(R.id.description_card_view);
        item_description_expandable_layout = findViewById(R.id.item_description_expandable_layout);
        variation_card_view = findViewById(R.id.variation_card_view);
        selected_variation_card_view = findViewById(R.id.selected_variation_card_view);
        variation_expandable_layout = findViewById(R.id.variation_expandable_layout);
        review_sentiment_score_card_view = findViewById(R.id.review_sentiment_score_card_view);
        review_sentiment_score_expandable_layout = findViewById(R.id.review_sentiment_score_expandable_layout);
        positive_review_expandable_layout = findViewById(R.id.positive_review_expandable_layout);
        negative_review_expandable_layout = findViewById(R.id.negative_review_expandable_layout);

        toolbar_button1 = findViewById(R.id.toolbar_button1);
        toolbar_button2 = findViewById(R.id.toolbar_button2);


        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DetailedProductPageActivity.super.onBackPressed();
            }
        });

        price_track_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(SharedPrefManager.getInstans(getApplicationContext()).isLogin()){
                    if(isTracked){
                        //this is to remove from the tracked list
                        removeTrackingItem();
                    }
                    else{
                        // this to add into the tracked list
                        addTrackingItem();
                    }
                }
                else{
                    Intent intent = new Intent(DetailedProductPageActivity.this,LoginActivity.class);
                    startActivity(intent);
                }
            }
        });

        desc_collapse_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(item_description_expandable_layout.getVisibility()==View.GONE){
                    item_description_expandable_layout.setVisibility(View.VISIBLE);
                    desc_collapse_button.setBackgroundResource(R.drawable.up_arrow);
                }
                else{
                    item_description_expandable_layout.setVisibility(View.GONE);
                    desc_collapse_button.setBackgroundResource(R.drawable.down_arrow);
                }

            }
        });

        variation_collapse_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(variation_expandable_layout.getVisibility()==View.GONE){
                    selected_variation_card_view.setVisibility(View.GONE);
                    variation_expandable_layout.setVisibility(View.VISIBLE);
                    variation_collapse_button.setBackgroundResource(R.drawable.up_arrow);
                }
                else{
                    selected_variation_card_view.setVisibility(View.VISIBLE);
                    variation_expandable_layout.setVisibility(View.GONE);
                    variation_collapse_button.setBackgroundResource(R.drawable.down_arrow);
                }
            }
        });

        sentiment_score_collapse_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //this is for case no review
                if(total_reviews.equalsIgnoreCase("")){
                    if(no_reviews_message_layout.getVisibility()==View.GONE){
                        no_reviews_message_layout.setVisibility(View.VISIBLE);
                        sentiment_score_collapse_button.setBackgroundResource(R.drawable.up_arrow);
                    }
                    else{
                        no_reviews_message_layout.setVisibility(View.GONE);
                        sentiment_score_collapse_button.setBackgroundResource(R.drawable.down_arrow);
                    }
                }
                //this is for case has sentiment score
                else{
                    if(review_sentiment_score_expandable_layout.getVisibility()==View.GONE){
                        review_sentiment_score_expandable_layout.setVisibility(View.VISIBLE);
                        sentiment_score_collapse_button.setBackgroundResource(R.drawable.up_arrow);
                    }
                    else{
                        review_sentiment_score_expandable_layout.setVisibility(View.GONE);
                        sentiment_score_collapse_button.setBackgroundResource(R.drawable.down_arrow);
                    }
                }

            }
        });

        positive_review_collapse_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(positive_review_expandable_layout.getVisibility()==View.GONE){
                    positive_review_expandable_layout.setVisibility(View.VISIBLE);
                    positive_review_collapse_button.setBackgroundResource(R.drawable.up_arrow);
                }
                else{
                    positive_review_expandable_layout.setVisibility(View.GONE);
                    positive_review_collapse_button.setBackgroundResource(R.drawable.down_arrow);
                }
            }
        });

        negative_review_collapse_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(negative_review_expandable_layout.getVisibility()==View.GONE){
                    negative_review_expandable_layout.setVisibility(View.VISIBLE);
                    negative_review_collapse_button.setBackgroundResource(R.drawable.up_arrow);
                }
                else{
                    negative_review_expandable_layout.setVisibility(View.GONE);
                    negative_review_collapse_button.setBackgroundResource(R.drawable.down_arrow);
                }
            }
        });

        toolbar_button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //so that all the information needed is completed, waited the sentiment analysis to finished
                if (!sentiment_score.equalsIgnoreCase("")){
                    //if the compare list is empty, the SP is -1
                    if(SharedPrefManager.getInstans(v.getContext()).getCompareListID().equals("-1")){
                        SharedPrefManager.getInstans(v.getContext()).setCompareListID(SearchPageActivity.SEARCH_QUERY_ID);
                        databaseHelper.addCompareListItem(item_images.get(0),item_name,selected_variation_text_view.getText().toString(),item_discounted_price,platform,sentiment_score,location,item_id,product_url);
                        Toast.makeText(v.getContext(),"This item is added into the compare list.",Toast.LENGTH_LONG).show();
                    }
                    //if the SP id is same with the query id, then check whether the item is inside the compare list
                    else if(SharedPrefManager.getInstans(v.getContext()).getCompareListID().equals(SearchPageActivity.SEARCH_QUERY_ID)){
                        //if the item exists in the compare list
                        if(databaseHelper.checkIfCompareListItemExists(platform,item_id)){
                            Toast.makeText(v.getContext(),"This item already in the compare list.",Toast.LENGTH_LONG).show();
                        }
                        else{
                            databaseHelper.addCompareListItem(item_images.get(0),item_name,selected_variation_text_view.getText().toString(),item_discounted_price,platform,sentiment_score,location,item_id,product_url);
                            Toast.makeText(v.getContext(),"This item is added into the compare list.",Toast.LENGTH_LONG).show();
                        }
                    }
                    //the compare list contains other item which not under the current search query
                    else{
                        final AlertDialog dialog = new AlertDialog.Builder(v.getContext())
                                .setTitle("ShopMate Compare List")
                                .setMessage("There are other items which are not under this search query.")
                                .setPositiveButton("Clear", null)
                                .setNegativeButton("Cancel", null)
                                .show();

                        Button positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
                        positiveButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                databaseHelper.clearCompareListTable();
                                SharedPrefManager.getInstans(v.getContext()).setCompareListID(SearchPageActivity.SEARCH_QUERY_ID);
                                databaseHelper.addCompareListItem(item_images.get(0),item_name,selected_variation_text_view.getText().toString(),item_discounted_price,platform,sentiment_score,location,item_id,product_url);
                                Toast.makeText(v.getContext(), "The list is cleared and this item is added.", Toast.LENGTH_SHORT).show();
                                dialog.dismiss();
                            }
                        });
                    }
                }
                else{
                    Log.e("cannot add to compare list,","sentiment analysis is not finished");
                    Toast.makeText(v.getContext(),"Please wait sentiment analysis to be finished.",Toast.LENGTH_LONG).show();
                }

            }
        });

        toolbar_button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gotoUrl(product_url);
            }
        });
    }

    private void removeTrackingItem() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, remove_tracked_item_uri
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("remove tracking item","successful!!");

                    JSONObject object = new JSONObject(response);
                    boolean error = object.getBoolean("error");
                    if(!error){
                        String msg = object.getString("msg");
                        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                        isTracked = false;
                        price_track_button.setBackgroundResource(R.drawable.unfilled_heart);
                    }
                    else{
                        String error_msg = object.getString("error_msg");
                        Toast.makeText(getApplicationContext(),error_msg,Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("error",e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("uuid",tracking_id);
                params.put("user_id",user_id);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }

    private void addTrackingItem() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, add_tracked_item_uri
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject object = new JSONObject(response);
                    boolean error = object.getBoolean("error");
                    if(!error){
                        Log.d("add tracking item","successful!!");
                        String msg = object.getString("msg");
                        Toast.makeText(getApplicationContext(),msg,Toast.LENGTH_LONG).show();
                        isTracked = true;
                        price_track_button.setBackgroundResource(R.drawable.filled_heart);
                    }
                    else{
                        String error_msg = object.getString("error_msg");
                        Toast.makeText(getApplicationContext(),error_msg,Toast.LENGTH_LONG).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("error",e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("name",item_name);
                params.put("url",scrape_url);
                params.put("product_url",product_url);
                params.put("track_price",item_discounted_price.replace("RM",""));
                params.put("image",item_images.get(0));
                params.put("platform",platform);
                params.put("item_id",item_id);
                params.put("user_id",SharedPrefManager.getInstans(getApplicationContext()).getUserId());
                params.put("price",item_discounted_price.replace("RM",""));
                if(variation_list.size()!=0){
                    params.put("variant", (String) selected_variation_text_view.getText());
                }
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }


    private void gotoUrl(String product_url) {
        Uri uri = Uri.parse(product_url);
        startActivity(new Intent(Intent.ACTION_VIEW,uri));
    }

    private void getDetailedInfoData() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, detailed_info_uri
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {

                    JSONObject object = new JSONObject(response);
                    try{
                        JSONArray array = object.getJSONArray("images");
                        for(int i=0;i<array.length();i++){

                            item_images.add(array.getString(i));
                        }
                        item_name = object.getString("name");
                        item_description = object.getString("description");
                        item_ori_price = object.getString("original_price");
                        item_discounted_price = object.getString("discounted_price");
                        product_url = object.getString("product_url");



                        JSONArray var_array = object.getJSONArray("variation");
                        for(int i=0;i<var_array.length();i++){
                            JSONArray variation_array = var_array.getJSONArray(i);
                            VariationData data = new VariationData();
                            data.setVariation_name(variation_array.getString(0));
                            data.setVariation_ori_price(variation_array.getString(1));
                            data.setVariation_disc_price(variation_array.getString(2));

                            //set the selected variation
                            if(variation_array.getString(1).equalsIgnoreCase(item_ori_price) &&
                                    variation_array.getString(2).equalsIgnoreCase(item_discounted_price)){
                                selected_variation = variation_array.getString(0);
                                data.setIs_selected(true);
                            }

                            variation_list.add(data);
                        }

                        checkIfUserLogin();
                        if(isLogin){
                            checkIfItemTracked(user_id,item_id,platform);
                        }

                        displayDetailedInfoData();
                        getSentimentData();

                    }
                    catch(Exception e) {
                        boolean success = object.getBoolean("success");
                        String errorMsg = object.getString("message");
                        Toast.makeText(getApplicationContext(),errorMsg,Toast.LENGTH_LONG).show();
                        displayErrorLayout();

                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("url",scrape_url);
                params.put("platform",platform);
                return params;
            }
        };

        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        requestQueue.add(stringRequest);

    }

    private void checkIfUserLogin() {
        if (SharedPrefManager.getInstans(this).isLogin()){
            isLogin = true;
            user_id = SharedPrefManager.getInstans(this).getUserId();
        }
    }

    private void displayErrorLayout() {
        error_layout = findViewById(R.id.error_layout);
        scroll_view = findViewById(R.id.scroll_view);
        toolbar = findViewById(R.id.toolbar);

        scroll_view.setVisibility(View.GONE);
        toolbar.setVisibility(View.GONE);
        progress_bar.setVisibility(View.GONE);
        error_layout.setVisibility(View.VISIBLE);


    }

    private void displayDetailedInfoData() {
        progress_bar = findViewById(R.id.progress_bar);
        imageSlider = findViewById(R.id.image_slider);
        platform_text_view = findViewById(R.id.platform_text_view);
        item_name_text_view = findViewById(R.id.item_name_text_view);
        item_discountedPrice_text_view = findViewById(R.id.item_discountedPrice_text_view);
        item_originalPrice_text_view = findViewById(R.id.item_originalPrice_text_view);
        ratingScore_rating_bar = findViewById(R.id.ratingScore_rating_bar);
        review_count_text_view= findViewById(R.id.review_count_text_view);
        location_text_view = findViewById(R.id.location_text_view);
        item_description_text_view = findViewById(R.id.item_description_text_view);
        variation_card_view = findViewById(R.id.variation_card_view);
        variation_list_recycler_view = findViewById(R.id.variation_list_recycler_view);
        selected_variation_text_view = findViewById(R.id.selected_variation_text_view);
        price_track_button = findViewById(R.id.price_track_button);


        //set image slider
        List<SlideModel> image_list = new ArrayList<>();
        for(int i=0;i<item_images.size();i++){
            image_list.add(new SlideModel(item_images.get(i), ScaleTypes.CENTER_INSIDE));
        }
        imageSlider.setImageList(image_list);

        //set platform
        platform_text_view.setText(platform);
        String platform_text_color = (platform.equalsIgnoreCase("Shopee")) ?"#f1582b":"#0f156d";
        platform_text_view.setTextColor(Color.parseColor(platform_text_color));

        //set item name
        item_name_text_view.setText(item_name);

        //set prices
        item_discountedPrice_text_view.setText(item_discounted_price);
        if(item_ori_price.equalsIgnoreCase("0")){
            item_originalPrice_text_view.setText("");
        }
        else{
            item_originalPrice_text_view.setText(item_ori_price);
            item_originalPrice_text_view.setPaintFlags(item_originalPrice_text_view.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);

        }

        //set rating bar
        ratingScore_rating_bar.setRating(rating_score);
        Log.e("review count",review_count);
        review_count_text_view.setText("("+review_count+")");
        location_text_view.setText(location);

//        //set the price tracking button
//        if(isLogin){
//           if(isTracked){
//               Log.e("tracked item","ya, it is tracked!!");
//               price_track_button.setBackgroundResource(R.drawable.filled_heart);
//           }
//           else{
//               Log.e("tracked item","nope, it is not tracked!!");
//               price_track_button.setBackgroundResource(R.drawable.unfilled_heart);
//           }
//        }

        //set description
        item_description_text_view.setText(item_description);

        //set variation
        if(variation_list.size()!=0){
            //set dummy data when there is error from Shopee API where the no selected variation matches the variation list
            if(selected_variation.equalsIgnoreCase("")){
                selected_variation_text_view.setText(variation_list.get(0).getVariation_name());
            }
            else{
                selected_variation_text_view.setText(selected_variation);
            }


            //set recycler view
            adapter = new VariationListAdapter(DetailedProductPageActivity.this,variation_list);
            StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.HORIZONTAL);
            variation_list_recycler_view.setLayoutManager(staggeredGridLayoutManager);
            variation_list_recycler_view.setAdapter(adapter);

            //set events that will occur on each item click in recyclerview
            adapter.setOnItemClickListener(new VariationListAdapter.OnItemsClickListener() {
                @Override
                public void onItemClick(VariationData variation) {
                    selected_variation_text_view.setText(variation.getVariation_name());
                    item_discountedPrice_text_view.setText(variation.getVariation_disc_price());
                    if(variation.getVariation_ori_price().equalsIgnoreCase("0")){
                        item_originalPrice_text_view.setText("");
                    }
                    else{
                        item_originalPrice_text_view.setText(variation.getVariation_ori_price());
                        item_originalPrice_text_view.setPaintFlags(item_originalPrice_text_view.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);

                    }
                }
            });

        }
        else{
            variation_card_view.setVisibility(View.GONE);
        }

        progress_bar.setVisibility(View.GONE);
        scroll_view.setVisibility(View.VISIBLE);
        toolbar.setVisibility(View.VISIBLE);



    }

    //send request to the server to check if this item has been tracked
    private void checkIfItemTracked(String user_id, String item_id, String platform) {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, check_item_tracked_uri
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.e("check item tracked","got call the method!!");
                    JSONObject object = new JSONObject(response);
                    boolean error = object.getBoolean("error");
                    if(!error){
                        isTracked = object.getBoolean("isTracked");
                        if(isTracked){

                            tracking_id = object.getString("tracking_id");
                            Log.e("tracked item","ya, it is tracked!!");
                            Log.e("tracked item ID ",tracking_id);
                            //set the price tracking button
                            price_track_button.setBackgroundResource(R.drawable.filled_heart);

                            }
                        else{
                            Log.e("tracked item","nope, it is not tracked!!");
                            price_track_button.setBackgroundResource(R.drawable.unfilled_heart);
                        }
                        }

                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("error",e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("user_id",user_id);
                params.put("item_id",item_id);
                params.put("platform",platform);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                30000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }


    private void getSentimentData() {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, sentiment_predictions_uri
                , new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    Log.d("ahah","successful!!");

                    JSONObject object = new JSONObject(response);
                    boolean error = object.getBoolean("error");
                    if(!error){
                        total_reviews = object.getString("total_review");
                        positive_review_count = object.getString("positive_review_count");
                        negative_review_count = object.getString("negative_review_count");
                        sentiment_score = object.getString("sentiment_score");

                        JSONArray pos_rev = object.getJSONArray("positive_review");
                        for(int i=0;i<pos_rev.length();i++){

                            positive_reviews_list.add(pos_rev.getString(i));
                        }
                        JSONArray neg_rev = object.getJSONArray("negative_review");
                        for(int i=0;i<neg_rev.length();i++){

                            negative_reviews_list.add(neg_rev.getString(i));
                        }
//                        Log.e("sentiment_score",sentiment_score);
//                        Log.e("total_reviews", total_reviews);

                    }
                    else{
                        if (sentiment_score.equalsIgnoreCase("")){
                            sentiment_score = "0.0";
                        }
//                        Log.e("sentiment_score",sentiment_score);
//                        Log.e("total_reviews", total_reviews);
                    }

                    displaySentimentData();


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.e("error",e.getMessage());
                    Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_LONG).show();
                    //get the message
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String,String> getParams(){
                Map<String,String> params = new HashMap<>();
                params.put("item_id",item_id);
                params.put("shop_id",shop_id);
                params.put("review_count",review_count);
                params.put("platform",platform);
                return params;
            }
        };
        stringRequest.setRetryPolicy(new DefaultRetryPolicy(
                100000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(stringRequest);
    }

    private void displaySentimentData() {

        review_sentiment_score_progress_bar_layout = findViewById(R.id.review_sentiment_score_progress_bar_layout);
        no_reviews_message_layout = findViewById(R.id.no_reviews_message_layout);
        review_sentiment_score_expandable_layout = findViewById(R.id.review_sentiment_score_expandable_layout);
        review_sentiment_score_box = findViewById(R.id.review_sentiment_score_box);
        sentiment_score_text_view = findViewById(R.id.sentiment_score_text_view);
        total_reviews_text_view = findViewById(R.id.total_reviews_text_view);
        positive_review_card_view = findViewById(R.id.positive_review_card_view);
        positive_review_count_text_view = findViewById(R.id.positive_review_count_text_view);
        positive_review_list_view  = findViewById(R.id.positive_review_list_view);
        negative_review_card_view = findViewById(R.id.negative_review_card_view);
        negative_review_count_text_view = findViewById(R.id.negative_review_count_text_view);
        negative_review_list_view = findViewById(R.id.negative_review_list_view);

        review_sentiment_score_progress_bar_layout.setVisibility(View.GONE);
        if(total_reviews.equalsIgnoreCase("")){
            no_reviews_message_layout.setVisibility(View.VISIBLE);
        }
        else{
            //set sentiment score card view layout
            review_sentiment_score_expandable_layout.setVisibility(View.VISIBLE);
            sentiment_score_text_view.setText(sentiment_score);
            String box_color = (Float.parseFloat(sentiment_score)>=2.5)?"#7CD622":"#FFFF382A";
            review_sentiment_score_box.setCardBackgroundColor(Color.parseColor(box_color));
            total_reviews_text_view.setText(total_reviews);

            //shown when only got positive review
            if(!positive_review_count.equalsIgnoreCase("0")){
                positive_review_count_text_view.setText("("+positive_review_count+")");
                positive_review_card_view.setVisibility(View.VISIBLE);
                ArrayAdapter<String> pos_rev_adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,positive_reviews_list);
                positive_review_list_view.setAdapter(pos_rev_adapter);
            }

            if(!negative_review_count.equalsIgnoreCase("0")){
                negative_review_count_text_view.setText("("+negative_review_count+")");
                negative_review_card_view.setVisibility(View.VISIBLE);
                ArrayAdapter<String> neg_rev_adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,negative_reviews_list);
                negative_review_list_view.setAdapter(neg_rev_adapter);
            }



        }
    }


}