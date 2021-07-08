package com.example.shopmate_v1;

public class CompareListItemData {

    private String image;
    private String name;
    private String variation;
    private String price;
    private String location;
    private String platform;
    private String item_id;
    private String product_url;
    private String sentiment_score;
    private float price_weight;
    private float location_weight;
    private float price_weightage;
    private float location_weightage;
    private float sentiment_weightage;
    private float recommend_score;


    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVariation() {
        return variation;
    }

    public void setVariation(String variation) {
        this.variation = variation;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getItem_id() {
        return item_id;
    }

    public void setItem_id(String item_id) {
        this.item_id = item_id;
    }

    public String getSentiment_score() {
        return sentiment_score;
    }

    public void setSentiment_score(String sentiment_score) {
        this.sentiment_score = sentiment_score;
    }

    public float getPrice_weight() {
        return price_weight;
    }

    public void setPrice_weight(float price_weight) {
        this.price_weight = price_weight;
    }

    public float getLocation_weight() {
        return location_weight;
    }

    public void setLocation_weight(float location_weight) {
        this.location_weight = location_weight;
    }

    public float getRecommend_score() {
        return recommend_score;
    }

    public void setRecommend_score(float recommend_score) {
        this.recommend_score = recommend_score;
    }

    public float getPrice_weightage() {
        return price_weightage;
    }

    public void setPrice_weightage(float price_weightage) {
        this.price_weightage = price_weightage;
    }

    public float getLocation_weightage() {
        return location_weightage;
    }

    public void setLocation_weightage(float location_weightage) {
        this.location_weightage = location_weightage;
    }

    public float getSentiment_weightage() {
        return sentiment_weightage;
    }

    public void setSentiment_weightage(float sentiment_weightage) {
        this.sentiment_weightage = sentiment_weightage;
    }

    public String getProduct_url() {
        return product_url;
    }

    public void setProduct_url(String product_url) {
        this.product_url = product_url;
    }
}
