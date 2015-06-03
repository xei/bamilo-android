/**
 *
 */
package com.mobile.newFramework.objects.checkout;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.rest.RestConstants;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.forms.PaymentMethodForm;
import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that represents the response from the get products rating
 *
 * @author nutzer2
 */
public class SuperCheckoutFinish implements IJSONSerializable, Parcelable {


    private PaymentMethodForm mPaymentMethodForm;

    private String mPaymentUrl;

    private String mOrderNumber;

    public SuperCheckoutFinish() {
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

        Form mPaymentForm = null;
        // Get payment url
        mPaymentUrl = null;


        // Validate payment content
        if (jsonObject.has("payment")) {
            JSONObject jsonPayment = jsonObject.optJSONObject("payment");
            mPaymentMethodForm = new PaymentMethodForm();
            mPaymentMethodForm.initialize(jsonObject);
            if(jsonObject.has(RestConstants.JSON_ORDER_NUMBER_TAG)){
                   mOrderNumber = jsonObject.optString(RestConstants.JSON_ORDER_NUMBER_TAG);
            }
            if (jsonPayment != null && jsonPayment.length() > 0) {
                /*TODO doing shit
                JSONObject paymentForm = jsonPayment.optJSONObject("form");
                if(paymentForm != null) {
                    mPaymentForm = new Form();
                    mPaymentForm.initialize(paymentForm);
                }
                */
                // Get url
                String paymentUrl = jsonPayment.optString("url");
                if(paymentUrl != null) {
                    mPaymentUrl = paymentUrl;
                }
            }
        }

        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        // TODO Auto-generated method stub
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
        dest.writeValue(mPaymentMethodForm);
        dest.writeString(mPaymentUrl);

    }

    private SuperCheckoutFinish(Parcel in) {
        mPaymentMethodForm = (PaymentMethodForm) in.readValue(PaymentMethodForm.class.getClassLoader());
        mPaymentUrl = in.readString();

    }

    public static final Creator<SuperCheckoutFinish> CREATOR = new Creator<SuperCheckoutFinish>() {
        public SuperCheckoutFinish createFromParcel(Parcel in) {
            return new SuperCheckoutFinish(in);
        }

        public SuperCheckoutFinish[] newArray(int size) {
            return new SuperCheckoutFinish[size];
        }
    };

    public String getPaymentUrl() {
        return mPaymentUrl;
    }

    public void setPaymentUrl(String paymentUrl) {
        this.mPaymentUrl = paymentUrl;
    }

    public String getOrderNumber() {
        return mOrderNumber;
    }

    public void setOrderNumber(String mOrderNumber) {
        this.mOrderNumber = mOrderNumber;
    }

    public PaymentMethodForm getPaymentMethodForm() {
        return mPaymentMethodForm;
    }

    public void setPaymentMethodForm(PaymentMethodForm mPaymentMethodForm) {
        this.mPaymentMethodForm = mPaymentMethodForm;
    }
}
