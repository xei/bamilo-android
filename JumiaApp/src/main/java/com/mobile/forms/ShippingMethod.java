package com.mobile.forms;

import android.content.Context;
import android.view.View;

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.checkout.ShippingMethodHolder;
import com.mobile.view.R;

public class ShippingMethod {

    public ShippingMethodHolder shippingMethodHolder;

//    public ShippingMethod(String shippingMethod, String label, String deliveryTime) {
//        this.shippingMethodHolder = new ShippingMethodHolder();
//        this.shippingMethodHolder.shippingMethod = shippingMethod;
//        this.shippingMethodHolder.label = label;
//        this.shippingMethodHolder.deliveryTime = deliveryTime;
//    }
    
    public ShippingMethod() {
        this.shippingMethodHolder = new ShippingMethodHolder();
        this.shippingMethodHolder.shippingMethod = "";
        this.shippingMethodHolder.label = "";
        this.shippingMethodHolder.deliveryTime = "";
        this.shippingMethodHolder.deliveryFee = "";
    }
    
    public String getDeliveryFee() {
        return shippingMethodHolder.deliveryFee;
    }

    public void setDeliveryFee(String deliveryFee) {
        this.shippingMethodHolder.deliveryFee = deliveryFee;
    }
    
    public String getShippingMethod() {
        return shippingMethodHolder.shippingMethod;
    }

    public void setShippingMethod(String shippingMethod) {
        this.shippingMethodHolder.shippingMethod = shippingMethod;
    }

    public String getLabel() {
        return shippingMethodHolder.label;
    }

    public void setLabel(String label) {
        this.shippingMethodHolder.label = label;
    }

    public String getDeliveryTime() {
        return shippingMethodHolder.deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.shippingMethodHolder.deliveryTime = deliveryTime;
    }
    
    public View generateForm(final Context context) {
        
        View view = View.inflate(context, R.layout.checkout_shipping_fee_time, null);

        if(!shippingMethodHolder.deliveryTime.equals("")){
            TextView shippingTimeLabel = (TextView)view.findViewById(R.id.shipping_time_label);
            TextView shippingTime = (TextView)view.findViewById(R.id.shipping_time);
            shippingTimeLabel.setVisibility(View.VISIBLE);
            shippingTime.setVisibility(View.VISIBLE);
            shippingTime.setText(shippingMethodHolder.deliveryTime);
        }
        
        return view;
    }
}
