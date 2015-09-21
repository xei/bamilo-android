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
public class SetPaymentMethod extends CheckoutStepObject implements Parcelable {

    private PurchaseEntity orderSummary;

    public SetPaymentMethod() {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(orderSummary);
    }

    private SetPaymentMethod(Parcel in) {
        orderSummary = (PurchaseEntity) in.readValue(PurchaseEntity.class.getClassLoader());
    }

    public static final Creator<SetPaymentMethod> CREATOR = new Creator<SetPaymentMethod>() {
        public SetPaymentMethod createFromParcel(Parcel in) {
            return new SetPaymentMethod(in);
        }

        public SetPaymentMethod[] newArray(int size) {
            return new SetPaymentMethod[size];
        }
    };




}
