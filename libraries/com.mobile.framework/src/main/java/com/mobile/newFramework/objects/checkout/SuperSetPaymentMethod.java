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
public class SuperSetPaymentMethod extends CheckoutStepObject implements Parcelable {


    private OrderSummary orderSummary;

//    private FragmentType fragmentType;

    public SuperSetPaymentMethod() {
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
        setCheckoutNextStep(jsonObject);
        orderSummary = new OrderSummary(jsonObject);
//            Log.i(TAG, "ORDER SUMMARY: " + orderSummary.toString());
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
//        dest.writeValue(fragmentType);

    }

    private SuperSetPaymentMethod(Parcel in) {
        orderSummary = (OrderSummary) in.readValue(OrderSummary.class.getClassLoader());
//        fragmentType = (FragmentType) in.readValue(FragmentType.class.getClassLoader());

    }

    public static final Creator<SuperSetPaymentMethod> CREATOR = new Creator<SuperSetPaymentMethod>() {
        public SuperSetPaymentMethod createFromParcel(Parcel in) {
            return new SuperSetPaymentMethod(in);
        }

        public SuperSetPaymentMethod[] newArray(int size) {
            return new SuperSetPaymentMethod[size];
        }
    };


    public OrderSummary getOrderSummary() {
        return orderSummary;
    }

    public void setOrderSummary(OrderSummary orderSummary) {
        this.orderSummary = orderSummary;
    }
}