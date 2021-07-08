package com.example.shopmate_v1;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TableRow.LayoutParams;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.math.BigDecimal;
import java.security.PrivilegedExceptionAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

public class CompareListActivity extends Activity {

    ArrayList<CompareListItemData> compare_list_items = new ArrayList<>();
    CompareListItemAdapter adapter;
    DatabaseHelper databaseHelper;

    CardView compare_list_card_view;
    LinearLayout compare_list_error_layout;
    ImageButton cancel_button;
    BottomNavigationView bottom_navigation;
    RecyclerView compare_list_recycler_view;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compare_list);

        compare_list_card_view = findViewById(R.id.compare_list_card_view);
        compare_list_error_layout = findViewById(R.id.compare_list_error_layout);
        cancel_button = findViewById(R.id.cancel_button);
        compare_list_recycler_view = findViewById(R.id.compare_list_recycler_view);
        bottom_navigation = findViewById(R.id.bottom_navigation);
        bottom_navigation.setSelectedItemId(R.id.compare_list);
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchHelperCallback);
        itemTouchHelper.attachToRecyclerView(compare_list_recycler_view);


        databaseHelper = new DatabaseHelper(this);
        compare_list_items = databaseHelper.getCompareListItem();   //get list item from the sqlite database

        if(compare_list_items.size()==0){
            //show the error layout
            compare_list_error_layout.setVisibility(View.VISIBLE);
            compare_list_card_view.setVisibility(View.GONE);
        }
        else{
            setWeightAlgo();
            calculateRecommendScore();
            //Initialize adapter
            adapter = new CompareListItemAdapter(CompareListActivity.this,compare_list_items);
            //Set adapter
            compare_list_recycler_view.setAdapter(adapter);

        for(int i = 0; i<compare_list_items.size();i++){
            Log.e("sent score",String.valueOf(compare_list_items.get(i).getSentiment_score()));
            Log.e("recom score",String.valueOf(compare_list_items.get(i).getRecommend_score()));
        }
        }
//

        //set Onclick listener
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottom_navigation.getSelectedItemId() == R.id.home) {

                } else {
                    CompareListActivity.super.onBackPressed();
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
                        return true;
                    case R.id.price_tracker:
                        startActivity(new Intent(getApplicationContext(), PriceTrackerActivity.class));
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


    }

    ItemTouchHelper.SimpleCallback itemTouchHelperCallback = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.RIGHT | ItemTouchHelper.LEFT) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

            String platform = compare_list_items.get(viewHolder.getAdapterPosition()).getPlatform();
            String item_id = compare_list_items.get(viewHolder.getAdapterPosition()).getItem_id();
            databaseHelper.deleteCompareListItem(platform,item_id);
            compare_list_items.remove(viewHolder.getAdapterPosition());

            if(compare_list_items.size()==0){
                //show the error layout
                SharedPrefManager.getInstans(compare_list_recycler_view.getContext()).setCompareListID("-1");
                compare_list_error_layout.setVisibility(View.VISIBLE);
                compare_list_card_view.setVisibility(View.GONE);
            }

            else{
                //calculate again
                setWeightAlgo();
                calculateRecommendScore();
                adapter.notifyDataSetChanged();
            }



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

    private void calculateRecommendScore() {

        //weightage ratio for price, location, sentiment score is 20:20:60
        for(int i = 0; i<compare_list_items.size();i++){
            float sentiment_score = Float.parseFloat(compare_list_items.get(i).getSentiment_score());
            float sentiment_weightage = (sentiment_score / 5) *60;
            float price_weightage = (compare_list_items.get(i).getPrice_weight() * 20);
            float location_weightage = (compare_list_items.get(i).getLocation_weight() * 20);

            //round floating-point numbers
            price_weightage = BigDecimal.valueOf(price_weightage)
                    .setScale(1, BigDecimal.ROUND_HALF_DOWN)
                    .floatValue();
            sentiment_weightage = BigDecimal.valueOf(sentiment_weightage)
                    .setScale(1, BigDecimal.ROUND_HALF_DOWN)
                    .floatValue();


            //bonus mark for those with good ratings
            //ratings between 3.0 to 3.9 no additional mark or penalty
            if(sentiment_score >= 4.0){
                sentiment_weightage = sentiment_weightage + 10;
            }
            //penalty mark for those with bad ratings
            //it does not apply to those no sentiment score (null)
            else if(sentiment_score < 3.0 && sentiment_score > 0.0){
                sentiment_weightage = sentiment_weightage - 10;
            }
            float score = price_weightage + location_weightage + sentiment_weightage;
            compare_list_items.get(i).setPrice_weightage(price_weightage);
            compare_list_items.get(i).setLocation_weightage(location_weightage);
            compare_list_items.get(i).setSentiment_weightage(sentiment_weightage);
            compare_list_items.get(i).setRecommend_score(score);
        }
        
        sortDisplayResult();
    }

    //sort the list to be displayed based on the calculated recommend score
    private void sortDisplayResult() {

        Collections.sort(compare_list_items, new Comparator<CompareListItemData>() {
            @Override
            public int compare(CompareListItemData o1, CompareListItemData o2) {
                return Float.compare(o1.getRecommend_score(),o2.getRecommend_score());
            }
        });

        Collections.reverse(compare_list_items);
    }

    private void setWeightAlgo() {
        setPriceWeights();
        setLocationWeights();
    }

    private void setLocationWeights() {

        //overseas product has lower weight
        //local product has higher weight
        for(int i = 0; i<compare_list_items.size();i++){
            String location = compare_list_items.get(i).getLocation().toLowerCase();
            float weight;
            if(location.contains("china") || location.contains("mainland china")|| location.contains("south korea") || location.contains("united states")){
               weight  = 0.5f;
            }
            else{
                weight = 1.0f;
            }
            compare_list_items.get(i).setLocation_weight(weight);
        }
    }

    private void setPriceWeights() {
        Collections.sort(compare_list_items, new Comparator<CompareListItemData>() {
            @Override
            public int compare(CompareListItemData o1, CompareListItemData o2) {
                return Float.compare(Float.parseFloat(o1.getPrice().replace("RM","")),Float.parseFloat(o2.getPrice().replace("RM","")));
            }
        });

        //set weights according to the rank sorted based on the price
        //Algorithm to calculate price described as below:
        //get the lowest price as the ref price
        //calculate how much deviate from the ref price to determine what is the weight
        //negative weight is possible
        //this is to solve the scenario like below:
        // RM20 + 3.0/5.0 **RECOMMEND when set the weight in the order of 1.0, 0.5, 0.25, ...
        // RM21 + 3.5/5.0 **RECOMMEND when set the weight according to the deviation to the ref price
        float reference_price = Float.parseFloat(compare_list_items.get(0).getPrice().replace("RM",""));
        for(int i = 0; i<compare_list_items.size();i++){
            float temp_price =  Float.parseFloat(compare_list_items.get(i).getPrice().replace("RM",""));
            float deviation_percent = (temp_price - reference_price) / reference_price;
            float weight = 1 - deviation_percent;
            compare_list_items.get(i).setPrice_weight(weight);
        }
    }
}