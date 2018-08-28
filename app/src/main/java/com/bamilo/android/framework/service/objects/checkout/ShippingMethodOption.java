package com.bamilo.android.framework.service.objects.checkout;

import com.bamilo.android.framework.service.pojo.RestConstants;

import org.json.JSONObject;

/**
 * Class used to represent a shipping option.
 *
 * @author rsoares
 * @modified sergiopereira
 */
public class ShippingMethodOption {

    public String value;
    public String label;
    public String deliveryTime;
    public long shippingFee;

    public ShippingMethodOption() {
        super();
    }

    public void initialize(JSONObject jsonObject) {
        this.label = jsonObject.optString(RestConstants.LABEL);
        this.deliveryTime = jsonObject.optString(RestConstants.DELIVERY_TIME);
        this.value = jsonObject.optString(RestConstants.VALUE);
        this.shippingFee = jsonObject.optLong(RestConstants.SHIPPING_FEE);
    }

}
