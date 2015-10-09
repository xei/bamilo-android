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
public class SetShippingMethod extends CheckoutStepObject implements Parcelable {

    private PurchaseEntity orderSummary;

    public SetShippingMethod() {
        super();
    }

    public SetShippingMethod(PurchaseEntity orderSummary) {
        this.orderSummary = orderSummary;
    }

    public SetShippingMethod(SetShippingMethod checkoutStepObject) {
        super(checkoutStepObject);
        this.orderSummary = checkoutStepObject.orderSummary;
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
        // Get order summary from response
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

    private SetShippingMethod(Parcel in) {
        orderSummary = (PurchaseEntity) in.readValue(PurchaseEntity.class.getClassLoader());
    }

    public static final Creator<SetShippingMethod> CREATOR = new Creator<SetShippingMethod>() {
        public SetShippingMethod createFromParcel(Parcel in) {
            return new SetShippingMethod(in);
        }

        public SetShippingMethod[] newArray(int size) {
            return new SetShippingMethod[size];
        }
    };



}
