package com.mobile.newFramework.objects.checkout;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.forms.PaymentMethodForm;
import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that represents the checkout finish step.
 */
public class CheckoutFinish implements IJSONSerializable, Parcelable {

    private PaymentMethodForm mPaymentMethodForm;

    private String mPaymentUrl;

    private String mOrderNumber;

    /**
     * Empty constructor
     */
    @SuppressWarnings("unused")
    public CheckoutFinish() {
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
        // Get order number
        mOrderNumber = jsonObject.getString(RestConstants.JSON_ORDER_NUMBER_TAG);
        // Get payment url
        mPaymentUrl = null;
        // Validate payment content
        if (jsonObject.has(RestConstants.PAYMENT)) {
            // Form
            mPaymentMethodForm = new PaymentMethodForm();
            mPaymentMethodForm.initialize(jsonObject);
            // Order
            if (jsonObject.has(RestConstants.JSON_ORDER_NUMBER_TAG)) {
                mOrderNumber = jsonObject.optString(RestConstants.JSON_ORDER_NUMBER_TAG);
            }
            // Get url
            JSONObject jsonPayment = jsonObject.optJSONObject(RestConstants.PAYMENT);
            if (jsonPayment != null && jsonPayment.length() > 0) {
                String paymentUrl = jsonPayment.optString(RestConstants.URL);
                if (paymentUrl != null) {
                    mPaymentUrl = paymentUrl;
                }
            }
        }
        return true;
    }

    public String getOrderNumber() {
        return mOrderNumber;
    }

    public PaymentMethodForm getPaymentMethodForm() {
        return mPaymentMethodForm;
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
        dest.writeValue(mPaymentMethodForm);
        dest.writeString(mPaymentUrl);

    }

    private CheckoutFinish(Parcel in) {
        mPaymentMethodForm = (PaymentMethodForm) in.readValue(PaymentMethodForm.class.getClassLoader());
        mPaymentUrl = in.readString();

    }

    public static final Creator<CheckoutFinish> CREATOR = new Creator<CheckoutFinish>() {
        public CheckoutFinish createFromParcel(Parcel in) {
            return new CheckoutFinish(in);
        }

        public CheckoutFinish[] newArray(int size) {
            return new CheckoutFinish[size];
        }
    };


}
