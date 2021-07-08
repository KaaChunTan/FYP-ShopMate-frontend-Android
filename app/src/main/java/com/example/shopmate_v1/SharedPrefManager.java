package com.example.shopmate_v1;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.android.material.shadow.ShadowRenderer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

public class SharedPrefManager {

    private static SharedPrefManager SPManager;
    private static Context context;

    private static final String SP_USER = "shopMate_users";
    private static final String SP_COMPARE_LIST = "compare_list_data";
    private static final String KEY_ID ="id";
    private static final String KEY_USERNAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_TOKEN = "fcm_token";
    private static final String COMPARE_LIST_CURRENT_ID = "compare_list_id";
    
    private static final String SHOPEE_MALL_SHOPS = "shopee_mall_shops";
    private static final String SHOPEE_TOP_PRODUCTS = "shopee_top_products";
    private static final String LAZADA_BANNERS = "lazada_banners";

    private  SharedPrefManager(Context context){
        this.context = context;
    }

    public static synchronized SharedPrefManager getInstans(Context context){
        if(SPManager == null){
            SPManager = new SharedPrefManager(context);
        }
        return SPManager;
    }

    public void userLogin(String id, String name, String email){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_USER,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_ID,id);
        editor.putString(KEY_USERNAME,name);
        editor.putString(KEY_EMAIL,email);
        editor.apply();
    }

    public boolean isLogin(){
        SharedPreferences sharedPreferences =  context.getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
        if(sharedPreferences.getString(KEY_ID,null)!=null){
            return true;
        }
        return false;
    }

    public boolean logout(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_USER, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
        return true;
    }

    public String getUserId(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_USER,Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_ID,null);
    }

    public String getUsername(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_USER,Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_USERNAME,null);
    }

    public String getUserEmail(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_USER,Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_EMAIL,null);
    }

    public boolean saveFCMtoken(String token){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_USER,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_TOKEN,token);
        editor.apply();
        return true;
    }

    public String getFCMtoken(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_USER,Context.MODE_PRIVATE);
        return sharedPreferences.getString(KEY_TOKEN,null);
    }

    public String getCompareListID(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_COMPARE_LIST, Context.MODE_PRIVATE);
        return sharedPreferences.getString(COMPARE_LIST_CURRENT_ID,"-1");
    }

    public void setCompareListID(String id){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_COMPARE_LIST,Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(COMPARE_LIST_CURRENT_ID,id);
        editor.apply();
    }


    public void removeCompareListID(){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_COMPARE_LIST, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(COMPARE_LIST_CURRENT_ID,"-1");
        editor.apply();
    }


//    public ArrayList getShopeeMallShops(){
//        //https://www.geeksforgeeks.org/how-to-save-arraylist-to-sharedpreferences-in-android/
//        // method to load arraylist from shared prefs
//        // initializing our shared prefs with name as
//        // shared preferences.
//        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
//
//        // creating a variable for gson.
//        //gson is open source for conversion between java object and json
//        Gson gson = new Gson();
//
//        // below line is to get to string present from our
//        // shared prefs if not present setting it as null.
//        String json = sharedPreferences.getString(SHOPEE_MALL_SHOPS, null);
//
//        // below line is to get the type of our array list.
//        Type type = new TypeToken<ArrayList<ShopeeMallShopsData>>() {}.getType();
//
//        // in below line we are getting data from gson
//        // and saving it to our array list
//        ArrayList<ShopeeMallShopsData> mall_shops_list = gson.fromJson(json, type);
//
//        // checking below if the array list is empty or not
//        if (mall_shops_list == null) {
//            // if the array list is empty
//            // creating a new array list.
//            mall_shops_list = new ArrayList<>();
//        }
//        return mall_shops_list;
//    }
//
//    public void saveShopeeMallsShops(ArrayList<ShopeeMallShopsData> mall_shops_list){
//        // method for saving the data in array list.
//        // creating a variable for storing data in
//        // shared preferences.
//        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
//
//        // creating a variable for editor to
//        // store data in shared preferences.
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        // creating a new variable for gson.
//        Gson gson = new Gson();
//
//        // getting data from gson and storing it in a string.
//        String json = gson.toJson(mall_shops_list);
//
//        // below line is to save data in shared
//        // prefs in the form of string.
//        editor.putString(SHOPEE_MALL_SHOPS, json);
//
//        // below line is to apply changes
//        // and save data in shared prefs.
//        editor.apply();
//    }
//
//    public ArrayList getShopeeTopProducts(){
//        //https://www.geeksforgeeks.org/how-to-save-arraylist-to-sharedpreferences-in-android/
//        // method to load arraylist from shared prefs
//        // initializing our shared prefs with name as
//        // shared preferences.
//        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
//
//        // creating a variable for gson.
//        //gson is open source for conversion between java object and json
//        Gson gson = new Gson();
//
//        // below line is to get to string present from our
//        // shared prefs if not present setting it as null.
//        String json = sharedPreferences.getString(SHOPEE_TOP_PRODUCTS, null);
//
//        // below line is to get the type of our array list.
//        Type type = new TypeToken<ArrayList<ShopeeTopProductsData>>() {}.getType();
//
//        // in below line we are getting data from gson
//        // and saving it to our array list
//        ArrayList<ShopeeTopProductsData> top_products_list = gson.fromJson(json, type);
//
//        // checking below if the array list is empty or not
//        if (top_products_list == null) {
//            // if the array list is empty
//            // creating a new array list.
//            top_products_list = new ArrayList<>();
//        }
//        return top_products_list;
//    }
//
//    public void saveShopeeTopProducts(ArrayList<ShopeeTopProductsData> top_products_list){
//        // method for saving the data in array list.
//        // creating a variable for storing data in
//        // shared preferences.
//        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
//
//        // creating a variable for editor to
//        // store data in shared preferences.
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        // creating a new variable for gson.
//        Gson gson = new Gson();
//
//        // getting data from gson and storing it in a string.
//        String json = gson.toJson(top_products_list);
//
//        // below line is to save data in shared
//        // prefs in the form of string.
//        editor.putString(SHOPEE_TOP_PRODUCTS, json);
//
//        // below line is to apply changes
//        // and save data in shared prefs.
//        editor.apply();
//    }
//
//    public ArrayList getLazadaBanners(){
//        //https://www.geeksforgeeks.org/how-to-save-arraylist-to-sharedpreferences-in-android/
//        // method to load arraylist from shared prefs
//        // initializing our shared prefs with name as
//        // shared preferences.
//        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
//
//        // creating a variable for gson.
//        //gson is open source for conversion between java object and json
//        Gson gson = new Gson();
//
//        // below line is to get to string present from our
//        // shared prefs if not present setting it as null.
//        String json = sharedPreferences.getString(LAZADA_BANNERS, null);
//
//        // below line is to get the type of our array list.
//        Type type = new TypeToken<ArrayList<LazadaBannerData>>() {}.getType();
//
//        // in below line we are getting data from gson
//        // and saving it to our array list
//        ArrayList<LazadaBannerData> banner_list = gson.fromJson(json, type);
//
//        // checking below if the array list is empty or not
//        if (banner_list == null) {
//            // if the array list is empty
//            // creating a new array list.
//            banner_list = new ArrayList<>();
//        }
//        return banner_list;
//    }
//
//    public void saveLazadaBanners(ArrayList<LazadaBannerData> banner_list){
//        // method for saving the data in array list.
//        // creating a variable for storing data in
//        // shared preferences.
//        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME,Context.MODE_PRIVATE);
//
//        // creating a variable for editor to
//        // store data in shared preferences.
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//
//        // creating a new variable for gson.
//        Gson gson = new Gson();
//
//        // getting data from gson and storing it in a string.
//        String json = gson.toJson(banner_list);
//
//        // below line is to save data in shared
//        // prefs in the form of string.
//        editor.putString(LAZADA_BANNERS, json);
//
//        // below line is to apply changes
//        // and save data in shared prefs.
//        editor.apply();
//    }
//
//    public void deleteScrapedData(){
//        SharedPreferences sharedPreferences = context.getSharedPreferences(SP_NAME, Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sharedPreferences.edit();
//        editor.remove(SHOPEE_TOP_PRODUCTS);
//        editor.remove(SHOPEE_MALL_SHOPS);
//        editor.remove(LAZADA_BANNERS);
//        editor.apply();
//    }
}
