/**
 *
 */
package com.mobile.newFramework.objects.checkout;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.objects.OrderSummary;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that represents the response from the get products rating
 *
 * @author nutzer2
 */
public class SuperGetPaymentMethodsForm implements IJSONSerializable, Parcelable {


    private OrderSummary orderSummary;

    private Form form;

    public SuperGetPaymentMethodsForm() {
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
        JSONObject formJSON = jsonObject.getJSONObject("paymentMethodForm");
//            Log.d(TAG, "FORM JSON: " + formJSON.toString());
        form = new Form();
        if (!form.initialize(formJSON))
//                Log.e(TAG, "Error initializing the form using the data");

        orderSummary = new OrderSummary(jsonObject);

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
        dest.writeValue(orderSummary);
        dest.writeValue(form);

    }

    private SuperGetPaymentMethodsForm(Parcel in) {
        orderSummary = (OrderSummary) in.readValue(OrderSummary.class.getClassLoader());
        form = (Form) in.readValue(Form.class.getClassLoader());

    }

    public static final Creator<SuperGetPaymentMethodsForm> CREATOR = new Creator<SuperGetPaymentMethodsForm>() {
        public SuperGetPaymentMethodsForm createFromParcel(Parcel in) {
            return new SuperGetPaymentMethodsForm(in);
        }

        public SuperGetPaymentMethodsForm[] newArray(int size) {
            return new SuperGetPaymentMethodsForm[size];
        }
    };


    public OrderSummary getOrderSummary() {
        return orderSummary;
    }

    public void setOrderSummary(OrderSummary orderSummary) {
        this.orderSummary = orderSummary;
    }


    public Form getForm() {
        return form;
    }

    public void setForm(Form form) {
        this.form = form;
    }
}
