package com.bamilo.apicore.service.model.data.itemtracking;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DeliveryType {

    @SerializedName("description")
    @Expose
    private String dropShipDescription;


    public String getDropShipDescription() {
        return dropShipDescription;
    }

    public void setDropShipDescription(String dropShipDescription) {
        this.dropShipDescription = dropShipDescription;
    }
}
