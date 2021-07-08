package com.example.shopmate_v1;

public class TrackedItemsData {

    private String unique_id;
    private String name;
    private String product_url;
    private String track_price;
    private String latest_price;
    private String image;
    private String variant;
    private String platform;

    public String getUnique_id() {
        return unique_id;
    }

    public void setUnique_id(String unique_id) {
        this.unique_id = unique_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProduct_url() {
        return product_url;
    }

    public void setProduct_url(String product_url) {
        this.product_url = product_url;
    }

    public String getTrack_price() {
        return track_price;
    }

    public void setTrack_price(String track_price) {
        this.track_price = track_price;
    }

    public String getLatest_price() {
        return latest_price;
    }

    public void setLatest_price(String latest_price) {
        this.latest_price = latest_price;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getVariant() {
        return variant;
    }

    public void setVariant(String variant) {
        this.variant = variant;
    }

    public String getPlatform() {
        return platform;
    }

    public void setPlatform(String platform) {
        this.platform = platform;
    }


}
