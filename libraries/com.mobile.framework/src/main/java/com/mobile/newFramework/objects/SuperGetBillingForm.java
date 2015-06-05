/**
 *
 */
package com.mobile.newFramework.objects;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.objects.OrderSummary;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that represents the response from the get products rating
 *
 * @author nutzer2
 */
public class SuperGetBillingForm implements IJSONSerializable, Parcelable {


    private OrderSummary orderSummary;
    private Form form;
    private Addresses addresses;

    public SuperGetBillingForm() {
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
        try {
            // Get json object
            JSONObject jsonForm = jsonObject.getJSONObject(RestConstants.JSON_BILLING_FORM_TAG);
            JSONObject jsonList = jsonObject.getJSONObject(RestConstants.JSON_CUSTOMER_TAG).getJSONObject(RestConstants.JSON_ADDRESS_LIST_TAG);
            // Create form
            form = new Form();
            form.initialize(jsonForm);
            // Create addresses
            addresses = new Addresses(jsonList);
            // Get order summary
            orderSummary = new OrderSummary(jsonObject);

        } catch (Exception e) {
            return false;
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
        dest.writeValue(form);
        dest.writeValue(addresses);
        dest.writeValue(orderSummary);

    }

    private SuperGetBillingForm(Parcel in) {
        form = (Form) in.readValue(Form.class.getClassLoader());
        addresses = (Addresses) in.readValue(Addresses.class.getClassLoader());
        orderSummary = (OrderSummary) in.readValue(OrderSummary.class.getClassLoader());

    }

    public static final Creator<SuperGetBillingForm> CREATOR = new Creator<SuperGetBillingForm>() {
        public SuperGetBillingForm createFromParcel(Parcel in) {
            return new SuperGetBillingForm(in);
        }

        public SuperGetBillingForm[] newArray(int size) {
            return new SuperGetBillingForm[size];
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

    public Addresses getAddresses() {
        return addresses;
    }

    public void setAddresses(Addresses addresses) {
        this.addresses = addresses;
    }
}
