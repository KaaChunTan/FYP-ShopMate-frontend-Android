package com.example.shopmate_v1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.file.attribute.UserPrincipalLookupService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class DisplaySearchResultActivity extends AppCompatActivity {

    private static final String search_items_uri = "http://192.168.43.102/shopmate_v1/search_items.php";

    //Initialize variable
    EditText query_editText;
    ImageButton cancel_button, search_button;
    NestedScrollView nestedScrollView;
    RecyclerView recyclerView;
    ProgressBar progressBar;
    ArrayList<SearchResultData>  dataArrayList = new ArrayList<>();
    ArrayList<SearchResultData>  displaySearchDataArrayList = new ArrayList<>();
    SearchResultAdapter adapter;
    LinearLayout error_layout;

    CardView filter_card_view;
    LinearLayout filter_expandable_layout;
    ImageButton filter_collapse_button;
    TextInputLayout variation_text_input_layout, platform_text_input_layout;
    AutoCompleteTextView default_variation_autoCompleteTextView, default_platform_autoCompleteTextView;
    MaterialCardView set_filter_button;
    String platform;
    ArrayList<String> platform_list = new ArrayList<>();
    ArrayList<String> variation_list = new ArrayList<>();
    ArrayAdapter<String> platform_list_adapter;
    ArrayAdapter<String> variation_list_adapter;

    LinearLayout statistics_linear_layout;
    TextView statistics_median_value, statistics_min_value, statistics_max_value;
    ProgressBar statistics_progress_bar;

    DatabaseHelper databaseHelper;
    String keyword_search;
    RequestQueue mQueue;
    HashMap<String,StatisticsValueData> shopee_variation_statistics = new HashMap<>();
    HashMap<String,StatisticsValueData> all_variation_statistics = new HashMap<>();
    HashMap<String,StatisticsValueData> lazada_variation_statistics = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Assign variable
        nestedScrollView = findViewById(R.id.scroll_view);
        recyclerView = findViewById(R.id.recycler_view);
        progressBar = findViewById(R.id.progress_bar);
        query_editText = findViewById(R.id.query_edit_text);
        cancel_button = findViewById(R.id.cancel_button);
        search_button = findViewById(R.id.search_button);
        error_layout = findViewById(R.id.error_layout);

        filter_card_view = findViewById(R.id.filter_card_view);
        filter_expandable_layout = findViewById(R.id.filter_expandable_layout);
        filter_collapse_button = findViewById(R.id.filter_collapse_button);
        variation_text_input_layout = findViewById(R.id.variation_text_input_layout);
        platform_text_input_layout = findViewById(R.id.platform_text_input_layout);
        default_variation_autoCompleteTextView = findViewById(R.id.default_variation_autoCompleteTextView);
        default_platform_autoCompleteTextView = findViewById(R.id.default_platform_autoCompleteTextView);
        set_filter_button = findViewById(R.id.set_filter_button);

        statistics_linear_layout = findViewById(R.id.statistics_linear_layout);
        statistics_median_value = findViewById(R.id.statistics_median_value);
        statistics_min_value = findViewById(R.id.statistics_min_value);
        statistics_max_value = findViewById(R.id.statistics_max_value);
        statistics_progress_bar = findViewById(R.id.statistics_progress_bar);

        //Get intent from the parent activity
        Intent intent = getIntent();
        keyword_search = intent.getStringExtra(SearchPageActivity.QUERY);

        //Set edit text to the query string from intent
        setQueryEditText(keyword_search);

        Log.e("query id:", SearchPageActivity.SEARCH_QUERY_ID);

//        //Initialize adapter
//        adapter = new SearchResultAdapter(DisplaySearchResultActivity.this,dataArrayList);
//
//        //Set layout manager
//        StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
//        recyclerView.setLayoutManager(staggeredGridLayoutManager);
//
//        //Set adapter
//        recyclerView.setAdapter(adapter);

        //set Onclick listener
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DisplaySearchResultActivity.super.onBackPressed();
            }
        });

        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                keyword_search = query_editText.getText().toString();
                addSearchHistory(keyword_search);
                dataArrayList.clear();
                displaySearchDataArrayList.clear();
                adapter.notifyDataSetChanged();

                platform_list.clear();
                variation_list.clear();
                platform_list_adapter.notifyDataSetChanged();
                variation_list_adapter.notifyDataSetChanged();

                //when user enter new query on this page and search item
                getData();
                hideKeyboard(DisplaySearchResultActivity.this);
            }
        });

        query_editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.length()!=0)
                {
                    search_button.setEnabled(true);
                    search_button.setAlpha(1f);
                }
                else{
                    search_button.setEnabled(false);
                    search_button.setAlpha(0.5f);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        nestedScrollView.setOnScrollChangeListener(new NestedScrollView.OnScrollChangeListener() {
//            @Override
//            public void onScrollChange(NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
//                //check condition
//                if(scrollY == v.getChildAt(0).getMeasuredHeight() - v.getMeasuredHeight()){
//                    //when reach last item position
//                    //increase page size
//                    page++;
//                    //call method
//                    getData(page);
//                }
//            }
//        });



        getData();
        //displayFilterLayout();


    }

    public void addSearchHistory(String query){
        databaseHelper = new DatabaseHelper(this);
        databaseHelper.addSearchHistory(query);
    }

    private void setQueryEditText(String keyword_search) {

        query_editText.setText(keyword_search);

    }

    private void disableInput(){
        query_editText.setEnabled(false);
        search_button.setEnabled(false);
        search_button.setAlpha(0.5f);
        error_layout.setVisibility(View.GONE);
        filter_card_view.setVisibility(View.GONE);
    }


    private void getData() {

        disableInput();


        mQueue = Volley.newRequestQueue(getApplicationContext());

        StringRequest request = new StringRequest(Request.Method.POST, search_items_uri
                ,new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        try{

                            JSONObject object = new JSONObject(response);
                            boolean success = object.getBoolean("success");
                            if(success){
                                JSONObject result_array = object.getJSONObject("result").getJSONObject("items");
                                for (int i = 0 ; i < result_array.length();i++){
                                    JSONObject item = result_array.getJSONObject(String.format("%d",i));

                                    //Initialize SearchResultData
                                    SearchResultData data= new SearchResultData();
                                    data.setItem_image(item.getString("image"));
                                    data.setItem_name(item.getString("name"));
                                    data.setDiscounted_price(item.getString("price"));
                                    data.setDisplay_price(item.getString("price"));
                                    if(item.getString("price_before_discount").equalsIgnoreCase("null")){
                                        data.setOriginal_price("");
                                    }

                                    else{
                                        data.setOriginal_price(item.getString("price_before_discount"));
                                    }

                                    data.setLocation(item.getString("location"));

                                    if((int)(Float.parseFloat(item.getString("rating")))<1){
                                        data.setRatingScore(0);
                                    }

                                    else{
                                        data.setRatingScore((int)(Float.parseFloat(item.getString("rating"))));
                                    }

                                    data.setProduct_url(item.getString("scrapeUrl"));

                                    Log.d("Item name: ", item.getString("name"));
                                    Log.d("Image url: ", item.getString("image"));

                                    data.setPlatform(item.getString("platform"));
                                    data.setItem_id(item.getString("item_id"));
                                    data.setShop_id(item.getString("shop_id"));
                                    data.setReview_count(item.getString("review_count"));

                                    //get the variation data from the json and save into the search result data
                                    HashMap<String, String> temp = new HashMap<>();
                                    try{
                                        JSONArray variation_array = item.getJSONArray("variation_list");

                                        for (int j = 0 ; j < variation_array.length();j++){
                                            temp.put(variation_array.getJSONArray(j).getString(0),variation_array.getJSONArray(j).getString(1));
                                        }
                                        data.setVariation_price(temp);
                                    }
                                    catch (JSONException e){
                                        data.setVariation_price(temp);
                                    }

                                    dataArrayList.add(data);
                                    displaySearchDataArrayList.add(data);

                                }

                                //get shopee variation statistics data
                                JSONObject shopee_statistics_object = object.getJSONObject("result").getJSONObject("shopee_statistics");
                                Log.e("this is the shopee statistics json", shopee_statistics_object.toString());
                                Iterator<String> keys= shopee_statistics_object.keys();  //iterate the keys in json without knowing their name
                                do{
                                    StatisticsValueData data = new StatisticsValueData();
                                    String variation_name = keys.next();
                                    JSONObject statistics = shopee_statistics_object.getJSONObject(variation_name);
                                    data.setMax(statistics.getDouble("max"));
                                    data.setMin(statistics.getDouble("min"));
                                    data.setMedian(statistics.getDouble("median"));

                                    shopee_variation_statistics.put(variation_name,data);

                                }while(keys.hasNext());

                                //get all statistics data
                                JSONObject all_statistics_object = object.getJSONObject("result").getJSONObject("all_statistics");
                                Log.e("this is the all statistics json", all_statistics_object.toString());
                                Iterator<String> keys2= all_statistics_object.keys();  //iterate the keys in json without knowing their name
                                do{
                                    StatisticsValueData data = new StatisticsValueData();
                                    String variation_name = keys2.next();
                                    JSONObject statistics = all_statistics_object.getJSONObject(variation_name);
                                    data.setMax(statistics.getDouble("max"));
                                    data.setMin(statistics.getDouble("min"));
                                    data.setMedian(statistics.getDouble("median"));

                                    all_variation_statistics.put(variation_name,data);

                                }while(keys.hasNext());

                                //get lazada variation statistics data
                                JSONObject lazada_statistics_object = object.getJSONObject("result").getJSONObject("lazada_statistics");
                                Log.e("this is the statistics json", lazada_statistics_object.toString());
                                Iterator<String> keys3= lazada_statistics_object.keys();  //iterate the keys in json without knowing their name
                                do{
                                    StatisticsValueData data = new StatisticsValueData();
                                    String variation_name = keys3.next();
                                    JSONObject statistics = lazada_statistics_object.getJSONObject(variation_name);
                                    data.setMax(statistics.getDouble("max"));
                                    data.setMin(statistics.getDouble("min"));
                                    data.setMedian(statistics.getDouble("median"));

                                    lazada_variation_statistics.put(variation_name,data);

                                }while(keys.hasNext());

                                sortData(dataArrayList);
                                sortData(displaySearchDataArrayList);
                                //Initialize adapter
                                adapter = new SearchResultAdapter(DisplaySearchResultActivity.this,displaySearchDataArrayList);

                                StaggeredGridLayoutManager staggeredGridLayoutManager = new StaggeredGridLayoutManager(2, LinearLayoutManager.VERTICAL);
                                recyclerView.setLayoutManager(staggeredGridLayoutManager);

                                //Set adapter
                                recyclerView.setAdapter(adapter);
                                query_editText.setEnabled(true);
                                //progressBar.setVisibility(View.GONE);
                                displayResultLayout();
                                displayFilterLayout();

                            }
                            else{
                                String errorMsg = object.getString("message");
                                Toast.makeText(getApplicationContext(),errorMsg,Toast.LENGTH_LONG).show();
                                displayErrorLayout();
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
                params.put("keyword_search",keyword_search);
                Log.e("the search query is accepted and it starts searching the items","yes!");
                return params;
            }
        };
        request.setRetryPolicy(new DefaultRetryPolicy(
                120000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        mQueue.add(request);
    }

    private void displayErrorLayout() {
        nestedScrollView.setVisibility(View.GONE);
        statistics_linear_layout.setVisibility(View.GONE);
        error_layout.setVisibility(View.VISIBLE);

    }
    private void displayResultLayout() {
        nestedScrollView.setVisibility(View.VISIBLE);
        statistics_linear_layout.setVisibility(View.VISIBLE);
        error_layout.setVisibility(View.GONE);

    }

    private void sortData(ArrayList<SearchResultData> displaySearchDataArrayList) {

        Collections.sort(displaySearchDataArrayList, new Comparator<SearchResultData>() {
            @Override
            public int compare(SearchResultData o1, SearchResultData o2) {
                return Float.compare(o1.getPrice_float(),o2.getPrice_float());
            }
        });
    }

    //hide keyboard after user submitting query at the same page
    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    //display the filter section
    private void displayFilterLayout(){

        //set for the platform and variation drop down menu items
        platform_list.add("All");
        platform_list.add("Shopee");
        platform_list.add("Lazada");

        platform_list_adapter = new ArrayAdapter<>(this, R.layout.drop_down_items,platform_list);
        default_platform_autoCompleteTextView.setAdapter(platform_list_adapter);
        default_platform_autoCompleteTextView.setText(platform_list_adapter.getItem(0),false);


        variation_list.add("None");
        variation_list_adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.drop_down_items,variation_list);
        default_variation_autoCompleteTextView.setAdapter(variation_list_adapter);
        default_variation_autoCompleteTextView.setText(variation_list_adapter.getItem(0),false);

        filter_card_view.setVisibility(View.VISIBLE);


        //to set the variation drop down menu items depending the choice of the platform filter
        default_platform_autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                platform = platform_list_adapter.getItem(position);

                if (platform.equalsIgnoreCase("Shopee") && shopee_variation_statistics.size()>0){

                    //get variation name from the statistics hashmap
                    ArrayList<String> variation_list = new ArrayList<>(shopee_variation_statistics.keySet());

                    ArrayAdapter<String> variation_list_adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.drop_down_items,variation_list);
                    variation_list_adapter.notifyDataSetChanged();
                    default_variation_autoCompleteTextView.setAdapter(variation_list_adapter);
                    default_variation_autoCompleteTextView.setText(variation_list_adapter.getItem(0),false);
                }
                else{
                    ArrayList<String> variation_list = new ArrayList<String>();
                    variation_list.add("None");
                    ArrayAdapter<String> variation_list_adapter = new ArrayAdapter<>(getApplicationContext(), R.layout.drop_down_items,variation_list);
                    variation_list_adapter.notifyDataSetChanged();
                    default_variation_autoCompleteTextView.setAdapter(variation_list_adapter);
                    default_variation_autoCompleteTextView.setText(variation_list_adapter.getItem(0),false);
                }

            }
        });

        set_filter_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFilterResult(default_platform_autoCompleteTextView.getText().toString(),default_variation_autoCompleteTextView.getText().toString());
            }
        });

        filter_collapse_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(filter_expandable_layout.getVisibility()==View.GONE){
                    filter_expandable_layout.setVisibility(View.VISIBLE);
                    statistics_linear_layout.setVisibility(View.GONE);
                    filter_collapse_button.setBackgroundResource(R.drawable.up_arrow);
                }
                else{
                    filter_expandable_layout.setVisibility(View.GONE);
                    filter_collapse_button.setBackgroundResource(R.drawable.down_arrow);
                }

            }
        });
    }

    private void showFilterResult(String platform, String variation) {
        if(!variation.equalsIgnoreCase("None")){
            displaySearchDataArrayList.clear();
            for (int i=0 ;i<dataArrayList.size();i++){
                if(dataArrayList.get(i).getPlatform().equalsIgnoreCase("Shopee")){
                    for (String name : dataArrayList.get(i).getVariation_price().keySet()) {
                        if(name.equalsIgnoreCase(variation)){
                            dataArrayList.get(i).setDisplay_price(dataArrayList.get(i).getVariation_price().get(name));
                            displaySearchDataArrayList.add(dataArrayList.get(i));
                            break;
                        }
                    }
                }
            }
            if(displaySearchDataArrayList.size()>0){
                adapter.notifyDataSetChanged();
                showStatisticsResult(platform, variation);
                sortData(displaySearchDataArrayList);
                displayResultLayout();
            }
            else{
                displayErrorLayout();
            }

        }
        else{
            switch (platform){
                case "All":
                    displaySearchDataArrayList.clear();
                    for (int i=0 ;i<dataArrayList.size();i++){
                        if(dataArrayList.get(i).getPlatform().equalsIgnoreCase("Shopee")){
                            //change back to their normal display price for those who has filtered by Shopee before
                            dataArrayList.get(i).setDisplay_price(dataArrayList.get(i).getDiscounted_price());
                        }
                        displaySearchDataArrayList.add(dataArrayList.get(i));
                    }
                    if(displaySearchDataArrayList.size()>0){
                        adapter.notifyDataSetChanged();
                        showStatisticsResult(platform, variation);
                        sortData(displaySearchDataArrayList);
                        displayResultLayout();
                    }
                    else{
                        displayErrorLayout();
                    }
                    break;
                case "Shopee":
                    displaySearchDataArrayList.clear();
                    for (int i=0 ;i<dataArrayList.size();i++){
                        if(dataArrayList.get(i).getPlatform().equalsIgnoreCase("Shopee")){
                            dataArrayList.get(i).setDisplay_price(dataArrayList.get(i).getDiscounted_price());
                            displaySearchDataArrayList.add(dataArrayList.get(i));
                        }
                    }
                    if(displaySearchDataArrayList.size()>0){
                        adapter.notifyDataSetChanged();
                        showStatisticsResult(platform, variation);
                        sortData(displaySearchDataArrayList);
                        displayResultLayout();
                    }
                    else{
                        displayErrorLayout();
                    }
                    break;
                case "Lazada":
                    displaySearchDataArrayList.clear();
                    for (int i=0 ;i<dataArrayList.size();i++){
                        if(dataArrayList.get(i).getPlatform().equalsIgnoreCase("Lazada")){
                            displaySearchDataArrayList.add(dataArrayList.get(i));
                        }
                    }
                    if(displaySearchDataArrayList.size()>0){
                        adapter.notifyDataSetChanged();
                        showStatisticsResult(platform, variation);
                        sortData(displaySearchDataArrayList);
                        displayResultLayout();
                    }
                    else{
                        displayErrorLayout();
                    }

                    break;
            }

        }
    }

    private void showStatisticsResult(String platform, String variation) {
        statistics_linear_layout.setVisibility(View.VISIBLE);
        switch (platform){
            case "All":
                statistics_median_value.setText("RM "+ String.format("%.2f",all_variation_statistics.get("None").getMedian()));
                statistics_min_value.setText("RM "+ String.format("%.2f",all_variation_statistics.get("None").getMin()));
                statistics_max_value.setText("RM "+ String.format("%.2f",all_variation_statistics.get("None").getMax()));

                statistics_median_value.setTextColor(Color.parseColor("#afdafa"));
                statistics_progress_bar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#afdafa")));
                statistics_progress_bar.setMin((int) all_variation_statistics.get("None").getMin());
                statistics_progress_bar.setMax((int) all_variation_statistics.get("None").getMax());
                statistics_progress_bar.setProgress((int) all_variation_statistics.get("None").getMedian());
                break;
            case "Shopee":
                statistics_median_value.setText("RM "+ String.format("%.2f",shopee_variation_statistics.get(variation).getMedian()));
                statistics_min_value.setText("RM "+ String.format("%.2f",shopee_variation_statistics.get(variation).getMin()));
                statistics_max_value.setText("RM "+ String.format("%.2f",shopee_variation_statistics.get(variation).getMax()));

                statistics_median_value.setTextColor(Color.parseColor("#f1582b"));
                statistics_progress_bar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#f1582b")));
                statistics_progress_bar.setMin((int) shopee_variation_statistics.get(variation).getMin());
                statistics_progress_bar.setMax((int) shopee_variation_statistics.get(variation).getMax());
                statistics_progress_bar.setProgress((int) shopee_variation_statistics.get(variation).getMedian());
                break;
            case "Lazada":
                statistics_median_value.setText("RM "+ String.format("%.2f",lazada_variation_statistics.get("None").getMedian()));
                statistics_min_value.setText("RM "+ String.format("%.2f",lazada_variation_statistics.get("None").getMin()));
                statistics_max_value.setText("RM "+ String.format("%.2f",lazada_variation_statistics.get("None").getMax()));

                statistics_median_value.setTextColor(Color.parseColor("#0f156d"));
                statistics_progress_bar.setProgressTintList(ColorStateList.valueOf(Color.parseColor("#0f156d")));
                statistics_progress_bar.setMin((int) lazada_variation_statistics.get("None").getMin());
                statistics_progress_bar.setMax((int) lazada_variation_statistics.get("None").getMax());
                statistics_progress_bar.setProgress((int) lazada_variation_statistics.get("None").getMedian());
                break;
        }
    }
}