/**
 *
 */
package com.mobile.newFramework.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.objects.OrderSummary;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that represents the response from the get products rating
 *
 * @author nutzer2
 */
public class SuperSetBillingAddress implements IJSONSerializable, Parcelable {


    private OrderSummary orderSummary;

    private FragmentType fragmentType;

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
        // Get and set next step
        //FIXME
        fragmentType = CheckoutStepManager.getNextCheckoutStep(jsonObject);
        // Get order summary from response
        try {
            orderSummary = new OrderSummary(jsonObject);
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
        // TODO Auto-generated method stub
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
        dest.writeValue(fragmentType);

    }

    private SuperSetBillingAddress(Parcel in) {
        orderSummary = (OrderSummary) in.readValue(OrderSummary.class.getClassLoader());
        fragmentType = (FragmentType) in.readValue(FragmentType.class.getClassLoader());

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


    public FragmentType getFragmentType() {
        return fragmentType;
    }

    public void setFragmentType(FragmentType fragmentType) {
        this.fragmentType = fragmentType;
    }
}
