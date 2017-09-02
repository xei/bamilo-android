package com.mobile.service.objects.product;

import com.mobile.service.objects.IJSONSerializable;
import com.mobile.service.objects.RequiredJson;
import com.mobile.service.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

public class DeliveryTime implements IJSONSerializable {
    private String sku;
    private String deliveryMessage;
    private Date deliveryTime;
    private String tehranDeliveryTime;
    private String otherCitiesDeliveryTime;

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        sku = jsonObject.optString(RestConstants.SKU);
        deliveryMessage = jsonObject.optString(RestConstants.DELIVERY_MESSAGE);
        tehranDeliveryTime = jsonObject.optString(RestConstants.DELIVERY_ZONE_ONE);
        otherCitiesDeliveryTime = jsonObject.optString(RestConstants.DELIVERY_ZONE_TWO);
        deliveryTime = new Date(jsonObject.optLong(RestConstants.DELIVERY_TIME) * 1000);
        return true;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.NONE;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getDeliveryMessage() {
        return deliveryMessage;
    }

    public void setDeliveryMessage(String deliveryMessage) {
        this.deliveryMessage = deliveryMessage;
    }

    public Date getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(Date deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public String getTehranDeliveryTime() {
        return tehranDeliveryTime;
    }

    public void setTehranDeliveryTime(String tehranDeliveryTime) {
        this.tehranDeliveryTime = tehranDeliveryTime;
    }

    public String getOtherCitiesDeliveryTime() {
        return otherCitiesDeliveryTime;
    }

    public void setOtherCitiesDeliveryTime(String otherCitiesDeliveryTime) {
        this.otherCitiesDeliveryTime = otherCitiesDeliveryTime;
    }
}
