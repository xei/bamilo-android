package com.mobile.newFramework.objects.checkout;

import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONObject;

/**
 * Class used to represent a shipping option.
 *
 * @author rsoares
 * @modified sergiopereira
 */
public class ShippingMethodOption {

    public String shippingMethod;
    public String label;
    public String deliveryTime;
    public long shippingFee;

    public ShippingMethodOption() {
        this.shippingMethod = "";
        this.label = "";
        this.deliveryTime = "";
    }

    public void initialize(String key, JSONObject jsonObject) {
        this.shippingMethod = key;
        this.label = jsonObject.optString(RestConstants.LABEL);
        this.deliveryTime = jsonObject.optString(RestConstants.DELIVERY_TIME);
        this.shippingFee = jsonObject.optLong(RestConstants.SHIPPING_FEE);
    }

}
