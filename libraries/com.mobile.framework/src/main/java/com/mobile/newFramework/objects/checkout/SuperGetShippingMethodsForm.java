/**
 *
 */
package com.mobile.newFramework.objects.checkout;

import android.os.Parcel;
import android.os.Parcelable;


import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.orders.OrderSummary;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.requests.checkout.ShippingMethodFormBuilderHolder;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that represents the response from the get products rating
 *
 * @author nutzer2
 */
public class SuperGetShippingMethodsForm implements IJSONSerializable, Parcelable {

    private OrderSummary orderSummary;

    private ShippingMethodFormBuilderHolder form;

    public SuperGetShippingMethodsForm() {
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
     * )
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        // Get shipping methods
        form = new ShippingMethodFormBuilderHolder(jsonObject.getJSONObject(RestConstants.JSON_SHIPPING_METHOD_TAG));

        JSONObject cartJSON = jsonObject.optJSONObject(RestConstants.JSON_CART_TAG);

        if(cartJSON != null)
//                Log.d(TAG, "CAT JSON: " + cartJSON.toString());

        // Get order
        orderSummary = new OrderSummary(jsonObject);

        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {

        return null;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return RequiredJson.METADATA;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(orderSummary);

    }

    private SuperGetShippingMethodsForm(Parcel in) {
        orderSummary = (OrderSummary) in.readValue(OrderSummary.class.getClassLoader());
    }

    public static final Creator<SuperGetShippingMethodsForm> CREATOR = new Creator<SuperGetShippingMethodsForm>() {
        public SuperGetShippingMethodsForm createFromParcel(Parcel in) {
            return new SuperGetShippingMethodsForm(in);
        }

        public SuperGetShippingMethodsForm[] newArray(int size) {
            return new SuperGetShippingMethodsForm[size];
        }
    };


    public OrderSummary getOrderSummary() {
        return orderSummary;
    }

    public void setOrderSummary(OrderSummary orderSummary) {
        this.orderSummary = orderSummary;
    }

    public ShippingMethodFormBuilderHolder getForm() {
        return form;
    }

    public void setForm(ShippingMethodFormBuilderHolder form) {
        this.form = form;
    }
}
