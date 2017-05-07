package com.mobile.newFramework.objects.checkout;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.cart.PurchaseEntity;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that represents the response from the get products rating
 */
public class MultiStepPayment implements IJSONSerializable, Parcelable {

    private PurchaseEntity mOrderSummary;

    private Form mForm;

    /**
     * Empty constructor
     */
    @SuppressWarnings("unused")
    public MultiStepPayment() {
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
        // Get form
        mForm = new Form();
        mForm.initialize(jsonObject);
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

    private MultiStepPayment(Parcel in) {
        mOrderSummary = (PurchaseEntity) in.readValue(PurchaseEntity.class.getClassLoader());
        mForm = (Form) in.readValue(Form.class.getClassLoader());

    }

    public static final Creator<MultiStepPayment> CREATOR = new Creator<MultiStepPayment>() {
        public MultiStepPayment createFromParcel(Parcel in) {
            return new MultiStepPayment(in);
        }

        public MultiStepPayment[] newArray(int size) {
            return new MultiStepPayment[size];
        }
    };

}
