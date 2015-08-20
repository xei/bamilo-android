/**
 *
 */
package com.mobile.newFramework.objects.checkout;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.orders.OrderSummary;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that represents the response from the get products rating
 *
 * @author nutzer2
 */
public class SetBillingAddress extends CheckoutStepObject implements Parcelable {

    private OrderSummary orderSummary;

    /**
     * Empty constructor
     */
    public SetBillingAddress() {
        // ...
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
        super.initialize(jsonObject);
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

    private SetBillingAddress(Parcel in) {
        orderSummary = (OrderSummary) in.readValue(OrderSummary.class.getClassLoader());
    }

    public static final Creator<SetBillingAddress> CREATOR = new Creator<SetBillingAddress>() {
        public SetBillingAddress createFromParcel(Parcel in) {
            return new SetBillingAddress(in);
        }

        public SetBillingAddress[] newArray(int size) {
            return new SetBillingAddress[size];
        }
    };


    public OrderSummary getOrderSummary() {
        return orderSummary;
    }

    public void setOrderSummary(OrderSummary orderSummary) {
        this.orderSummary = orderSummary;
    }

}
