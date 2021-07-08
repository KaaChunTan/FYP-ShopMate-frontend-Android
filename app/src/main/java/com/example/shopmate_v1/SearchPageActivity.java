package com.example.shopmate_v1;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;

public class SearchPageActivity extends AppCompatActivity {

    public static final String QUERY = "QUERY";
    public static String SEARCH_QUERY_ID = "SEARCH_QUERY_ID";

    DatabaseHelper databaseHelper;
    EditText query_editText;
    ImageButton cancel_button, search_button;
    ListView search_history_list_view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_page);

        cancel_button = findViewById(R.id.cancel_button);
        search_button = findViewById(R.id.search_button);
        query_editText = findViewById(R.id.query_edit_text);
        search_history_list_view = findViewById(R.id.search_history_list_view);
        databaseHelper = new DatabaseHelper(this);

        //disable when there is no input in the query
        search_button.setEnabled(false);
        search_button.setAlpha(0.5f);

        //set list view
        getSearchHistory();

        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchPageActivity.super.onBackPressed();
                startActivity(new Intent(getApplicationContext(),HomeInterface.class));
                overridePendingTransition(0,0);
            }
        });
        search_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openDisplaySearchResultActivity();
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

        search_history_list_view.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                query_editText.getText().clear();
                query_editText.setText(parent.getItemAtPosition(position).toString());
                openDisplaySearchResultActivity();
            }
        });

    }

    public void openDisplaySearchResultActivity() {

        String query = query_editText.getText().toString();
        addSearchHistory(query);
        SEARCH_QUERY_ID = generateRandomString();

        Intent intent = new Intent(this, DisplaySearchResultActivity.class);
        intent.putExtra(QUERY, query);
        startActivity(intent);
        finish();

    }

    private String generateRandomString() {
        // chose a Character random from this String
        String AlphaNumericString = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "0123456789"
                + "abcdefghijklmnopqrstuvxyz";

        // create StringBuffer size of AlphaNumericString
        StringBuilder sb = new StringBuilder(20);

        for (int i = 0; i < 20; i++) {

            // generate a random number between
            // 0 to AlphaNumericString variable length
            int index
                    = (int)(AlphaNumericString.length()
                    * Math.random());

            // add Character one by one in end of sb
            sb.append(AlphaNumericString
                    .charAt(index));
        }

        return sb.toString();
    }

    public void addSearchHistory(String query){
        databaseHelper.addSearchHistory(query);
    }

    public void getSearchHistory(){
        Cursor data = databaseHelper.getSearchHistory();
        ArrayList<String> listData = new ArrayList<>();
        while(data.moveToNext()){
            listData.add(data.getString(data.getColumnIndex("query")));
        }

        ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,listData);
        search_history_list_view.setAdapter(adapter);

    }
}