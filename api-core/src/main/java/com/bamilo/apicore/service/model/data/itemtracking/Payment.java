
package com.bamilo.apicore.service.model.data.itemtracking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Payment {

    @SerializedName("delivery_cost")
    @Expose
    private Integer deliveryCost;
    @SerializedName("total_cost")
    @Expose
    private Integer totalCost;
    @SerializedName("method")
    @Expose
    private String method;

    public Integer getDeliveryCost() {
        return deliveryCost;
    }

    public void setDeliveryCost(Integer deliveryCost) {
        this.deliveryCost = deliveryCost;
    }

    public Integer getTotalCost() {
        return totalCost;
    }

    public void setTotalCost(Integer totalCost) {
        this.totalCost = totalCost;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

}
