package com.mobile.newFramework.objects.checkout;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.addresses.Addresses;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that represents the response from the get products rating
 * @author spereira
 */
public class CheckoutFormBilling implements IJSONSerializable, Parcelable {

    public static final String TAG = CheckoutFormBilling.class.getSimpleName();

    private PurchaseEntity mOrderSummary;
    private Form mForm;
    private Addresses mAddresses;

    /**
     * Empty Constructor
     */
    @SuppressWarnings("unused")
    public CheckoutFormBilling() {
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
        // Form
        JSONObject jsonForm = jsonObject.getJSONObject(RestConstants.ADDRESSES);
        mForm = new Form();
        mForm.initialize(jsonForm);
        // Addresses
        JSONObject jsonList = jsonObject.getJSONObject(RestConstants.JSON_CUSTOMER_TAG).getJSONObject(RestConstants.JSON_ADDRESS_LIST_TAG);
        mAddresses = new Addresses(jsonList);
        // Order
        mOrderSummary = new PurchaseEntity();
        mOrderSummary.initialize(jsonObject);
        return true;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.FORM_ENTITY;
    }

    public PurchaseEntity getOrderSummary() {
        return mOrderSummary;
    }

    public Form getForm() {
        return mForm;
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
        dest.writeValue(mForm);
        dest.writeValue(mAddresses);
        dest.writeValue(mOrderSummary);
    }

    private CheckoutFormBilling(Parcel in) {
        mForm = (Form) in.readValue(Form.class.getClassLoader());
        mAddresses = (Addresses) in.readValue(Addresses.class.getClassLoader());
        mOrderSummary = (PurchaseEntity) in.readValue(PurchaseEntity.class.getClassLoader());
    }

    public static final Creator<CheckoutFormBilling> CREATOR = new Creator<CheckoutFormBilling>() {
        public CheckoutFormBilling createFromParcel(Parcel in) {
            return new CheckoutFormBilling(in);
        }

        public CheckoutFormBilling[] newArray(int size) {
            return new CheckoutFormBilling[size];
        }
    };

}
