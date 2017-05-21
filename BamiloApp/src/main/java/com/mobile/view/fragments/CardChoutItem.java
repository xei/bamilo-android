package com.mobile.view.fragments;

/**
 * Created by shahrooz on 2/15/17.
 */
public class CardChoutItem {

    public String getCount() {
        return count;
    }

    public void setCount(String count) {
        this.count = count;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getProduct() {
        return product;
    }

    public void setProduct(String product) {
        this.product = product;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    private String brand;
    private String product;
    private String price;
    private String count;

    public CardChoutItem(String brand, String product, String price, String count, String imageUrl) {
        this.brand = brand;
        this.product = product;
        this.price = price;
        this.count = count;
        this.imageUrl = imageUrl;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    private String imageUrl;
}
