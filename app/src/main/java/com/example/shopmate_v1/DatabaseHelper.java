package com.example.shopmate_v1;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.util.Log;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DB_VERSION = 1;
    private static final String DB_NAME = "SHOPMATE";
    private static final String SEARCH_HISTORY_TABLE = "SEARCH_HISTORY";
    private static final String RECENT_VIEWED_TABLE = "RECENT_VIEWED";
    private static final String COMPARE_LIST_ITEM_TABLE = "COMPARE_LIST_ITEM";

    private static final String QUERY = "query";
    private static final String PRODUCT_URL = "product_url";
    private static final String ITEM_IMAGE = "item_image";
    private static final String ITEM_NAME = "item_name";
    private static final String PRICE = "price";
    private static final String PLATFORM = "platform";
    private static final String RATING_SCORE = "rating_score";
    private static final String LOCATION = "location";
    private static final String SHOP_ID = "shop_id";
    private static final String ITEM_ID = "item_id";
    private static final String REVIEW_COUNT = "review_count";
    private static final String VARIATION = "variation";
    private static final String SENTIMENT_SCORE = "sentiment_score";


    private static final String CREATE_SEARCH_HISTORY_TABLE = "CREATE TABLE " + SEARCH_HISTORY_TABLE +"(ID INTEGER PRIMARY KEY AUTOINCREMENT, " + QUERY + " TEXT)";
    private static final String CREATE_RECENT_VIEWED_TABLE = "CREATE TABLE " + RECENT_VIEWED_TABLE + "(" + "ID INTEGER PRIMARY KEY AUTOINCREMENT, " + ITEM_NAME + " TEXT NOT NULL, " +
            ITEM_IMAGE + " TEXT, " + PRICE + " TEXT NOT NULL, " + PLATFORM + " TEXT NOT NULL, " + PRODUCT_URL + " TEXT NOT NULL, " + RATING_SCORE + " INTEGER, " + LOCATION + " TEXT, "
            + ITEM_ID + " TEXT, " + SHOP_ID + " TEXT, " + REVIEW_COUNT + " TEXT" +")";

    private static final String CREATE_COMPARE_LIST_ITEM_TABLE = "CREATE TABLE " + COMPARE_LIST_ITEM_TABLE + "(" + "ID INTEGER PRIMARY KEY AUTOINCREMENT, " + ITEM_IMAGE + " TEXT, " + ITEM_NAME + " TEXT NOT NULL, " +
            VARIATION + " TEXT, " + PRICE + " TEXT NOT NULL, " + PLATFORM + " TEXT NOT NULL, "  + SENTIMENT_SCORE + " TEXT, " + LOCATION + " TEXT, "
            + ITEM_ID + " TEXT, " + PRODUCT_URL + " TEXT NOT NULL "+ ")";

    public DatabaseHelper(Context context) {
        super(context,DB_NAME,null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_SEARCH_HISTORY_TABLE);
        db.execSQL(CREATE_RECENT_VIEWED_TABLE);
        db.execSQL(CREATE_COMPARE_LIST_ITEM_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + SEARCH_HISTORY_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + RECENT_VIEWED_TABLE);
        db.execSQL("DROP TABLE IF EXISTS " + COMPARE_LIST_ITEM_TABLE);
        onCreate(db);
    }

    //add search history
    public void addSearchHistory(String query){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(QUERY, query);

        //delete duplicate values
        db.delete(SEARCH_HISTORY_TABLE,"query = " + "\""+ query +"\"",null);
        // Inserting Row
        db.insert(SEARCH_HISTORY_TABLE, null, values);
        //2nd argument is String containing nullColumnHack
        db.close(); // Closing database connection

    }

    public Cursor getSearchHistory(){
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + SEARCH_HISTORY_TABLE + " ORDER BY ID DESC LIMIT 5";
        Cursor search_history = db.rawQuery(query, null);
        return search_history; //need to move cursor in the activity part


    }

    //add recent viewed item
    void addRecentViewed(String item_name, String item_image, String price, String platform, String product_url, int rating_score, String location, String item_id, String shop_id, String review_count){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ITEM_NAME,item_name);
        values.put(ITEM_IMAGE,item_image);
        values.put(PRICE,price);
        values.put(PLATFORM,platform);
        values.put(PRODUCT_URL, product_url);
        values.put(RATING_SCORE,rating_score);
        values.put(LOCATION, location);
        values.put(SHOP_ID, shop_id);
        values.put(ITEM_ID, item_id);
        values.put(REVIEW_COUNT, review_count);

        db.delete(RECENT_VIEWED_TABLE,"shop_id = " + "\""+ shop_id +"\"",null);
        db.insert(RECENT_VIEWED_TABLE, null, values);
        db.close();
        Log.e("added scucc","yes!!!!");
    }

    public Cursor getRecentViewed(){
        SQLiteDatabase db = this.getReadableDatabase();


        String query = "SELECT * FROM " + RECENT_VIEWED_TABLE + " ORDER BY ID DESC LIMIT 15";
        Cursor recent_viewed = db.rawQuery(query, null);
        return recent_viewed;
    }

    //add compare list item
    void addCompareListItem(String item_image, String item_name,String variation, String price, String platform, String sentiment_score, String location, String item_id, String product_url){
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(ITEM_IMAGE,item_image);
        values.put(ITEM_NAME,item_name);
        values.put(VARIATION,variation);
        values.put(PRICE,price);
        values.put(PLATFORM,platform);
        values.put(SENTIMENT_SCORE,sentiment_score);
        values.put(LOCATION, location);
        values.put(ITEM_ID, item_id);
        values.put(PRODUCT_URL, product_url);

        db.insert(COMPARE_LIST_ITEM_TABLE, null, values);
        db.close();
        Log.e("added compare list item data success","yes!!!!");
    }


    //get compare list item data
    public ArrayList<CompareListItemData> getCompareListItem() {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + COMPARE_LIST_ITEM_TABLE;
        Cursor cursor = db.rawQuery(query, null);

        ArrayList<CompareListItemData> compareListItemArrayList = new ArrayList<>();

        // moving our cursor to first position.
        if (cursor != null && cursor.moveToFirst()) {
            do {
                // on below line we are adding the data from cursor to our array list.
                CompareListItemData data = new CompareListItemData();
                data.setImage(cursor.getString(1));
                data.setName(cursor.getString(2));
                data.setVariation(cursor.getString(3));
                data.setPrice(cursor.getString(4));
                data.setPlatform(cursor.getString(5));
                data.setSentiment_score(cursor.getString(6));
                data.setLocation(cursor.getString(7));
                data.setItem_id(cursor.getString(8));
                data.setProduct_url(cursor.getString(9));

                compareListItemArrayList.add(data);
            } while (cursor.moveToNext());
            // moving our cursor to next.
        }
        // at last closing our cursor
        // and returning our array list.
        cursor.close();
        return compareListItemArrayList;
    }

    //delete compare list item
    void deleteCompareListItem(String platform, String item_id){
        SQLiteDatabase db = this.getWritableDatabase();

        db.delete(COMPARE_LIST_ITEM_TABLE,"item_id = " + "\""+ item_id +"\"" + " and " + "platform= " + "\""+ platform +"\"",null);
        db.close();
    }

    //clear compare list item table
    void clearCompareListTable(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DELETE FROM "+ COMPARE_LIST_ITEM_TABLE);
        db.close();
        Log.e("cleared compare list item data success","yes!!!!");
    }

    //to return the compare list item count
    public long getCompareListItemCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        long count = DatabaseUtils.queryNumEntries(db, COMPARE_LIST_ITEM_TABLE);
        db.close();
        return count;
    }

    //to check whether a compare list item exists in the table
    public boolean checkIfCompareListItemExists(String platform, String item_id) {
        SQLiteDatabase db = this.getReadableDatabase();

        String query = "SELECT * FROM " + COMPARE_LIST_ITEM_TABLE + " WHERE " + "item_id = " + "\""+ item_id +"\"" + " AND " + "platform= " + "\""+ platform +"\"";
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.getCount() <= 0){
            cursor.close();
            return false;
        }
        cursor.close();

        return true;
    }
}
