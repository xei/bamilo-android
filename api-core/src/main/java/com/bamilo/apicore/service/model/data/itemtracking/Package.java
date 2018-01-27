
package com.bamilo.apicore.service.model.data.itemtracking;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Package {

    @SerializedName("products")
    @Expose
    private List<Product> products = null;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("delivery_time")
    @Expose
    private String deliveryTime;
    @SerializedName("delay")
    @Expose
    private Delay delay;

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public Delay getDelay() {
        return delay;
    }

    public void setDelay(Delay delay) {
        this.delay = delay;
    }
}
