package com.example.shopmate_v1;

import java.util.HashMap;

public class SearchResultData {

    private String item_image;
    private String platform;
    private String item_name;
    private String discounted_price;
    private String original_price;
    private String display_price;
    private HashMap<String,String> variation_price = new HashMap<>();
    private String location;
    private int ratingScore;
    private String product_url;

    private String shop_id;
    private String item_id;
    private String review_count;

    private float price_float;

    public HashMap<String, String> getVariation_price() {
        return variation_price;
    }

    public void setVariation_price(HashMap<String, String> variation_price) {
        this.variation_price = variation_price;
    }

    public String getDisplay_price() {
        return display_price;
    }

    public void setDisplay_price(String display_price) {
        this.display_price = display_price;
        setPrice_float(display_price);
    }

    public String getShop_id() {
        return shop_id;
    }

    public void setShop_id(String shop_id) {
        this.shop_id = shop_id;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getReview_count() {
        return review_count;
    }

    public void setReview_count(String review_count) {
        this.review_count = review_count;
    }


    public String getItem_image() {
        return item_image;
    }

    public void setItem_image(String item_image) {
        this.item_image = item_image;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getItem_name() {
        return item_name;
    }

    public void setItem_name(String item_name) {
        this.item_name = item_name;
    }

    public String getDiscounted_price() {
        return discounted_price;
    }

    public void setDiscounted_price(String discounted_price) {
        this.discounted_price = discounted_price;
    }

    public String getOriginal_price() {
        return original_price;
    }

    public void setOriginal_price(String original_price) {
        this.original_price = original_price;
    }

    public String getLocation() {
        return location;
    }

    public String getProduct_url() {
        return product_url;
    }

    public void setProduct_url(String product_url) {
        this.product_url = product_url;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public int getRatingScore() {
        return ratingScore;
    }

    public void setRatingScore(int ratingScore) {
        this.ratingScore = ratingScore;
    }

    public float getPrice_float() {
        return price_float;
    }

    public void setPrice_float(String price) {
        String price_extracted = price.substring(2);
        Float price_float = Float.valueOf(price_extracted).floatValue();
        this.price_float = price_float;
    }
}
