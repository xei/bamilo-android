package com.mobile.newFramework.objects.checkout;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that represents the response from the get products rating
 */
public class CheckoutFormPayment implements IJSONSerializable, Parcelable {

    private PurchaseEntity mOrderSummary;

    private Form mForm;

    /**
     * Empty constructor
     */
    @SuppressWarnings("unused")
    public CheckoutFormPayment() {
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
        JSONObject formJSON = jsonObject.getJSONObject(RestConstants.PAYMENT_METHOD_FORM);
        mForm = new Form();
        mForm.initialize(formJSON);
        // Order
        mOrderSummary = new PurchaseEntity();
        mOrderSummary.initialize(jsonObject);
        return true;
    }

    public PurchaseEntity getOrderSummary() {
        return mOrderSummary;
    }

    public Form getForm() {
        return mForm;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
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
        dest.writeValue(mForm);

    }

    private CheckoutFormPayment(Parcel in) {
        mOrderSummary = (PurchaseEntity) in.readValue(PurchaseEntity.class.getClassLoader());
        mForm = (Form) in.readValue(Form.class.getClassLoader());

    }

    public static final Creator<CheckoutFormPayment> CREATOR = new Creator<CheckoutFormPayment>() {
        public CheckoutFormPayment createFromParcel(Parcel in) {
            return new CheckoutFormPayment(in);
        }

        public CheckoutFormPayment[] newArray(int size) {
            return new CheckoutFormPayment[size];
        }
    };

}
