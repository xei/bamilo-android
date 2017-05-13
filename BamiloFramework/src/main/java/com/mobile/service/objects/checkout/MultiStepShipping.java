package com.mobile.service.objects.checkout;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.service.objects.IJSONSerializable;
import com.mobile.service.objects.RequiredJson;
import com.mobile.service.objects.cart.PurchaseEntity;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class that represents the response from the get products rating
 */
public class MultiStepShipping implements IJSONSerializable, Parcelable {

    private PurchaseEntity mOrderSummary;

    private ShippingForm mForm;

    /**
     * Empty constructor
     */
    @SuppressWarnings("unused")
    public MultiStepShipping() {
        super();
    }

    public MultiStepShipping(MultiStepShipping step) {
        this.mOrderSummary = step.mOrderSummary;
        this.mForm = step.mForm;
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
        // Get shipping form
        mForm = new ShippingForm();
        mForm.initialize(jsonObject);
        // Order
        mOrderSummary = new PurchaseEntity();
        mOrderSummary.initialize(jsonObject);
        return true;
    }

    public ArrayList<Fulfillment> getFulfillmentList() {
        return mOrderSummary.getFulfillmentList();
    }

    public PurchaseEntity getOrderSummary() {
        return mOrderSummary;
    }

    public ShippingForm getForm() {
        return mForm;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.METADATA;
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mOrderSummary);
        dest.writeValue(mForm);
    }

    private MultiStepShipping(Parcel in) {
        mOrderSummary = (PurchaseEntity) in.readValue(PurchaseEntity.class.getClassLoader());
        mForm = (ShippingForm) in.readValue(ShippingForm.class.getClassLoader());
    }

    public static final Creator<MultiStepShipping> CREATOR = new Creator<MultiStepShipping>() {
        public MultiStepShipping createFromParcel(Parcel in) {
            return new MultiStepShipping(in);
        }

        public MultiStepShipping[] newArray(int size) {
            return new MultiStepShipping[size];
        }
    };

}
