/**
 *
 */
package com.mobile.newFramework.objects.checkout;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.orders.OrderSummary;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that represents the response from the get products rating
 *
 * @author nutzer2
 */
public class SetPaymentMethod extends CheckoutStepObject implements Parcelable {

    private OrderSummary orderSummary;

    public SetPaymentMethod() {
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
        // Get and set next step
        super.initialize(jsonObject);
        orderSummary = new OrderSummary(jsonObject);
        return true;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(orderSummary);
    }

    private SetPaymentMethod(Parcel in) {
        orderSummary = (OrderSummary) in.readValue(OrderSummary.class.getClassLoader());
    }

    public static final Creator<SetPaymentMethod> CREATOR = new Creator<SetPaymentMethod>() {
        public SetPaymentMethod createFromParcel(Parcel in) {
            return new SetPaymentMethod(in);
        }

        public SetPaymentMethod[] newArray(int size) {
            return new SetPaymentMethod[size];
        }
    };


    public OrderSummary getOrderSummary() {
        return orderSummary;
    }

}
