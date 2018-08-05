
package com.bamilo.apicore.service.model.data.itemtracking;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Package {

    @SerializedName("products")
    @Expose
    private List<PackageItem> packageItems = null;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("delivery_time")
    @Expose
    private String deliveryTime;
    @SerializedName("delay")
    @Expose
    private Delay delay;

    @SerializedName("delivery_type")
    @Expose
    private DeliveryType deliveryType;

    public List<PackageItem> getPackageItems() {
        return packageItems;
    }

    public void setPackageItems(List<PackageItem> packageItems) {
        this.packageItems = packageItems;
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

    public DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }
}
