package com.mobile.newFramework.objects.checkout;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.orders.OrderSummary;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that represents the response from the get products rating
 */
public class CheckoutFormShipping implements IJSONSerializable, Parcelable {

    private OrderSummary mOrderSummary;

    private ShippingMethodFormBuilderHolder mForm;

    /**
     * Empty constructor
     */
    @SuppressWarnings("unused")
    public CheckoutFormShipping() {
        super();
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
        mForm = new ShippingMethodFormBuilderHolder(jsonObject.getJSONObject(RestConstants.JSON_SHIPPING_METHOD_TAG));
        // Order
        mOrderSummary = new OrderSummary();
        mOrderSummary.initialize(jsonObject);
        return true;
    }

    public OrderSummary getOrderSummary() {
        return mOrderSummary;
    }

    public ShippingMethodFormBuilderHolder getForm() {
        return mForm;
    }

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
        dest.writeValue(mOrderSummary);

    }

    private CheckoutFormShipping(Parcel in) {
        mOrderSummary = (OrderSummary) in.readValue(OrderSummary.class.getClassLoader());
    }

    public static final Creator<CheckoutFormShipping> CREATOR = new Creator<CheckoutFormShipping>() {
        public CheckoutFormShipping createFromParcel(Parcel in) {
            return new CheckoutFormShipping(in);
        }

        public CheckoutFormShipping[] newArray(int size) {
            return new CheckoutFormShipping[size];
        }
    };

}
