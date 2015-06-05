/**
 *
 */
package com.mobile.newFramework.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.objects.OrderSummary;
import com.mobile.newFramework.objects.checkout.CheckoutStepObject;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that represents the response from the get products rating
 *
 * @author nutzer2
 */
public class SuperSetBillingAddress extends CheckoutStepObject implements Parcelable {

    private OrderSummary orderSummary;

    public SuperSetBillingAddress() {
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

        // Get order summary from response
        try {
            orderSummary = new OrderSummary(jsonObject);
            setCheckoutNextStep(jsonObject);
//            Log.i(TAG, "ORDER SUMMARY: " + orderSummary.toString());
        } catch (NullPointerException | JSONException e) {
//            Log.w(TAG, "WARNING: PARSING ORDER RESPONSE", e);
            return false;
        }
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

    private SuperSetBillingAddress(Parcel in) {
        orderSummary = (OrderSummary) in.readValue(OrderSummary.class.getClassLoader());

    }

    public static final Creator<SuperSetBillingAddress> CREATOR = new Creator<SuperSetBillingAddress>() {
        public SuperSetBillingAddress createFromParcel(Parcel in) {
            return new SuperSetBillingAddress(in);
        }

        public SuperSetBillingAddress[] newArray(int size) {
            return new SuperSetBillingAddress[size];
        }
    };


    public OrderSummary getOrderSummary() {
        return orderSummary;
    }

    public void setOrderSummary(OrderSummary orderSummary) {
        this.orderSummary = orderSummary;
    }

}
