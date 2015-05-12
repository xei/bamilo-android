package com.mobile.forms;

import android.content.Context;
import android.view.View;

import com.mobile.components.customfontviews.TextView;
import com.mobile.framework.rest.RestConstants;
import com.mobile.view.R;

import org.json.JSONObject;

public class ShippingMethod {
    private String shippingMethod;
    private String label;
    private String deliveryTime;
    private String deliveryFee;
    
    public ShippingMethod(String shippingMethod, String label, String deliveryTime) {
        this.shippingMethod = shippingMethod;
        this.label = label;
        this.deliveryTime = deliveryTime;
    }
    
    public ShippingMethod() {
        this.shippingMethod = "";
        this.label = "";
        this.deliveryTime = "";
        this.deliveryFee = "";
    }
    
    public String getDeliveryFee() {
        return deliveryFee;
    }

    public void setDeliveryFee(String deliveryFee) {
        this.deliveryFee = deliveryFee;
    }
    
    public String getShippingMethod() {
        return shippingMethod;
    }

    public void setShippingMethod(String shippingMethod) {
        this.shippingMethod = shippingMethod;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }
    
    public void initialize(String key, JSONObject jsonObject){
        this.shippingMethod = key;
        this.label = jsonObject.optString(RestConstants.JSON_LABEL_TAG);
        this.deliveryTime = jsonObject.optString(RestConstants.JSON_DELIVERY_TIME);
        // Missing delivery fee
        
    }
    
    public View generateForm(final Context context) {
        
        View view = View.inflate(context, R.layout.checkout_shipping_fee_time, null);

        if(!deliveryTime.equals("")){
            TextView shippingTimeLabel = (TextView)view.findViewById(R.id.shipping_time_label);
            TextView shippingTime = (TextView)view.findViewById(R.id.shipping_time);
            shippingTimeLabel.setVisibility(View.VISIBLE);
            shippingTime.setVisibility(View.VISIBLE);
            shippingTime.setText(deliveryTime);
        }
        
        return view;
    }
}
