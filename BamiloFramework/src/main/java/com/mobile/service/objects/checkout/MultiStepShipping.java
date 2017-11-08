package com.mobile.service.objects.checkout;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.service.objects.IJSONSerializable;
import com.mobile.service.objects.RequiredJson;
import com.mobile.service.objects.cart.PurchaseEntity;
import com.mobile.service.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class that represents the response from the get products rating
 */
public class MultiStepShipping implements IJSONSerializable, Parcelable {

    private PurchaseEntity mOrderSummary;

    private ShippingForm mForm;

    private String estimatedDeliveryTime;
    private String deliveryNotice;

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
        this.estimatedDeliveryTime = step.estimatedDeliveryTime;
        this.deliveryNotice = step.getDeliveryNotice();
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
        estimatedDeliveryTime = jsonObject.getJSONObject(RestConstants.ESTIMATED_DELIVERY_TIME).optString(RestConstants.DELIVERY_MESSAGE);
        if (!jsonObject.isNull(RestConstants.DELIVERY_NOTICE)) {
            deliveryNotice = jsonObject.optString(RestConstants.DELIVERY_NOTICE);
        }
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
        dest.writeString(estimatedDeliveryTime);
        dest.writeString(deliveryNotice);
        dest.writeValue(mOrderSummary);
        dest.writeValue(mForm);
    }

    private MultiStepShipping(Parcel in) {
        estimatedDeliveryTime = in.readString();
        deliveryNotice = in.readString();
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

    public String getEstimatedDeliveryTime() {
        return estimatedDeliveryTime;
    }

    public void setEstimatedDeliveryTime(String estimatedDeliveryTime) {
        this.estimatedDeliveryTime = estimatedDeliveryTime;
    }

    public String getDeliveryNotice() {
        return deliveryNotice;
    }

    public void setDeliveryNotice(String deliveryNotice) {
        this.deliveryNotice = deliveryNotice;
    }
}
