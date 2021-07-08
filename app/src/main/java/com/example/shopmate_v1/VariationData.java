package com.example.shopmate_v1;

public class VariationData{

    private String variation_name;
    private String variation_ori_price;
    private String variation_disc_price;
    private boolean is_selected;

    public boolean getIs_selected() {
        return is_selected;
    }

    public void setIs_selected(boolean is_selected) {
        this.is_selected = is_selected;
    }

    public String getVariation_name() {
        return variation_name;
    }

    public void setVariation_name(String variation_name) {
        this.variation_name = variation_name;
    }

    public String getVariation_ori_price() {
        return variation_ori_price;
    }

    public void setVariation_ori_price(String variation_ori_price) {
        this.variation_ori_price = variation_ori_price;
    }

    public String getVariation_disc_price() {
        return variation_disc_price;
    }

    public void setVariation_disc_price(String variation_disc_price) {
        this.variation_disc_price = variation_disc_price;
    }
}
