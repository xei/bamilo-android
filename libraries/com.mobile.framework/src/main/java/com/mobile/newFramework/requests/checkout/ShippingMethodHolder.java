package com.mobile.newFramework.requests.checkout;

import com.mobile.framework.rest.RestConstants;

import org.json.JSONObject;

/**
 * Created by rsoares on 6/2/15.
 */
public class ShippingMethodHolder {

    public String shippingMethod;
    public String label;
    public String deliveryTime;
    public String deliveryFee;

    public ShippingMethodHolder(String shippingMethod, String label, String deliveryTime) {
        this.shippingMethod = shippingMethod;
        this.label = label;
        this.deliveryTime = deliveryTime;
    }

    public ShippingMethodHolder() {
        this.shippingMethod = "";
        this.label = "";
        this.deliveryTime = "";
        this.deliveryFee = "";
    }

    public void initialize(String key, JSONObject jsonObject){
        this.shippingMethod = key;
        this.label = jsonObject.optString(RestConstants.JSON_LABEL_TAG);
        this.deliveryTime = jsonObject.optString(RestConstants.JSON_DELIVERY_TIME);
        // Missing delivery fee

    }

}
