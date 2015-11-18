package com.mobile.newFramework.objects.checkout;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class that represents the response from the get products rating
 */
public class CheckoutFormShipping implements IJSONSerializable, Parcelable {

    private PurchaseEntity mOrderSummary;

    private ShippingMethodFormBuilderHolder mForm;

    private ArrayList<Fulfillment> mFulfillmentList;

    /**
     * Empty constructor
     */
    @SuppressWarnings("unused")
    public CheckoutFormShipping() {
        super();
    }

    public CheckoutFormShipping(CheckoutFormShipping shippingMethodsForm) {
        mOrderSummary = shippingMethodsForm.mOrderSummary;
        mForm = shippingMethodsForm.mForm;
        mFulfillmentList = shippingMethodsForm.mFulfillmentList;
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
        mOrderSummary = new PurchaseEntity();
        mOrderSummary.initialize(jsonObject);
        // Get fulfilment
        JSONArray fulfillmentArray = jsonObject.getJSONObject(RestConstants.CART_ENTITY).optJSONArray(RestConstants.FULFILLMENT);
        if(fulfillmentArray != null) {
            mFulfillmentList = new ArrayList<>();
            for (int i = 0; i < fulfillmentArray.length(); i++) {
                mFulfillmentList.add(new Fulfillment(fulfillmentArray.getJSONObject(i)));
            }
        }
        return true;
    }

    public PurchaseEntity getOrderSummary() {
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
        if (mFulfillmentList == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mFulfillmentList);
        }
    }

    private CheckoutFormShipping(Parcel in) {
        mOrderSummary = (PurchaseEntity) in.readValue(PurchaseEntity.class.getClassLoader());
        mForm = (ShippingMethodFormBuilderHolder) in.readValue(ShippingMethodFormBuilderHolder.class.getClassLoader());
        if (in.readByte() == 0x01) {
            mFulfillmentList = new ArrayList<>();
            in.readList(mFulfillmentList, Fulfillment.class.getClassLoader());
        } else {
            mFulfillmentList = null;
        }
    }

    public static final Creator<CheckoutFormShipping> CREATOR = new Creator<CheckoutFormShipping>() {
        public CheckoutFormShipping createFromParcel(Parcel in) {
            return new CheckoutFormShipping(in);
        }

        public CheckoutFormShipping[] newArray(int size) {
            return new CheckoutFormShipping[size];
        }
    };

    public ArrayList<Fulfillment> getmFulfillmentList() {
        return mFulfillmentList;
    }
}
