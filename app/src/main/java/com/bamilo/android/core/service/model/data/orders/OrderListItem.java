package com.bamilo.android.core.service.model.data.orders;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

/**
 * Created on 12/30/2017.
 */

public class OrderListItem {

    @SerializedName("number")
    @Expose
    private String orderNumber;

    @SerializedName("date")
    @Expose
    private String orderDate;

    @SerializedName("total")
    @Expose
    private String totalPrice;

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(String orderDate) {
        this.orderDate = orderDate;
    }

    public String getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(String totalPrice) {
        this.totalPrice = totalPrice;
    }
}
