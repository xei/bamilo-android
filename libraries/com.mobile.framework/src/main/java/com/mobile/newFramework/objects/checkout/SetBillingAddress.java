/**
 *
 */
package com.mobile.newFramework.objects.checkout;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.cart.PurchaseEntity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that represents the response from the get products rating
 *
 * @author nutzer2
 * @modified sergiopereira
 *
 */
public class SetBillingAddress extends CheckoutStepObject implements Parcelable {

    private PurchaseEntity orderSummary;

    /**
     * Empty constructor
     */
    public SetBillingAddress() {
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
        // Next checkout step
        super.initialize(jsonObject);
        // Order
        orderSummary = new PurchaseEntity();
        orderSummary.initialize(jsonObject);
        return true;
    }

    public PurchaseEntity getOrderSummary() {
        return orderSummary;
    }

    public void setOrderSummary(PurchaseEntity orderSummary) {
        this.orderSummary = orderSummary;
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
        orderSummary = (PurchaseEntity) in.readValue(PurchaseEntity.class.getClassLoader());
    }

    public static final Creator<SetBillingAddress> CREATOR = new Creator<SetBillingAddress>() {
        public SetBillingAddress createFromParcel(Parcel in) {
            return new SetBillingAddress(in);
        }

        public SetBillingAddress[] newArray(int size) {
            return new SetBillingAddress[size];
        }
    };

}
