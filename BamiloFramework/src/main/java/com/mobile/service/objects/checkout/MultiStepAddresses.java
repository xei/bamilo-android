package com.mobile.service.objects.checkout;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.service.objects.IJSONSerializable;
import com.mobile.service.objects.RequiredJson;
import com.mobile.service.objects.addresses.Addresses;
import com.mobile.service.objects.addresses.AddressesEntity;
import com.mobile.service.objects.cart.PurchaseEntity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that represents the response from the get products rating
 * @author spereira
 */
public class MultiStepAddresses implements IJSONSerializable, Parcelable {

    public static final String TAG = MultiStepAddresses.class.getSimpleName();

    private PurchaseEntity mOrderSummary;
    private AddressesEntity mAddresses;

    /**
     * Empty Constructor
     */
    @SuppressWarnings("unused")
    public MultiStepAddresses() {
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
        // Order
        mOrderSummary = new PurchaseEntity();
        mOrderSummary.initialize(jsonObject);
        // Addresses
        mAddresses = new AddressesEntity();
        mAddresses.initialize(jsonObject);
        return true;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.METADATA;
    }

    public PurchaseEntity getOrderSummary() {
        return mOrderSummary;
    }

    public Addresses getAddresses() {
        return mAddresses;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(mAddresses);
        dest.writeValue(mOrderSummary);
    }

    private MultiStepAddresses(Parcel in) {
        mAddresses = (AddressesEntity) in.readValue(Addresses.class.getClassLoader());
        mOrderSummary = (PurchaseEntity) in.readValue(PurchaseEntity.class.getClassLoader());
    }

    public static final Creator<MultiStepAddresses> CREATOR = new Creator<MultiStepAddresses>() {
        public MultiStepAddresses createFromParcel(Parcel in) {
            return new MultiStepAddresses(in);
        }

        public MultiStepAddresses[] newArray(int size) {
            return new MultiStepAddresses[size];
        }
    };

}
