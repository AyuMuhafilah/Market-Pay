package com.example.market_pay.model;

public class MerchantModel {
    private String name;
    private int imageResId;

    public MerchantModel(String name, int imageResId) {
        this.name = name;
        this.imageResId = imageResId;
    }

    public String getName() {
        return name;
    }

    public int getImageResId() {
        return imageResId;
    }
}


