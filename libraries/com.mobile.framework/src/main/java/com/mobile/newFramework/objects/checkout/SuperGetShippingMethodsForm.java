/**
 *
 */
package com.mobile.newFramework.objects.checkout;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.objects.OrderSummary;
import com.mobile.framework.rest.RestConstants;
import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that represents the response from the get products rating
 *
 * @author nutzer2
 */
public class SuperGetShippingMethodsForm implements IJSONSerializable, Parcelable {


    private OrderSummary orderSummary;

    private JSONObject formJSON;

    public SuperGetShippingMethodsForm() {
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

            // Get shipping methods
            formJSON = jsonObject.getJSONObject(RestConstants.JSON_SHIPPING_METHOD_TAG);
//            Log.d(TAG, "FORM JSON: " + formJSON.toString());
            //FIXME has a lot of dependencies on the view app section including R elements and adapters. Should be parsed on the view (fragment) side
//            ShippingMethodFormBuilder form = new ShippingMethodFormBuilder();
//            if (!form.initialize(formJSON))
//                Log.e(TAG, "Error initializing the form using the data");

            // Get cart
            JSONObject cartJSON = jsonObject.optJSONObject(RestConstants.JSON_CART_TAG);

            if(cartJSON != null)
//                Log.d(TAG, "CAT JSON: " + cartJSON.toString());

            // Get order
            orderSummary = new OrderSummary(jsonObject);
//            bundle.putParcelable(Constants.BUNDLE_ORDER_SUMMARY_KEY, orderSummary);
//
//            bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, form);

        } catch (JSONException e) {
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
        dest.writeValue(orderSummary);
        dest.writeValue(formJSON);

    }

    private SuperGetShippingMethodsForm(Parcel in) {
        orderSummary = (OrderSummary) in.readValue(OrderSummary.class.getClassLoader());
        formJSON = (JSONObject) in.readValue(JSONObject.class.getClassLoader());

    }

    public static final Creator<SuperGetShippingMethodsForm> CREATOR = new Creator<SuperGetShippingMethodsForm>() {
        public SuperGetShippingMethodsForm createFromParcel(Parcel in) {
            return new SuperGetShippingMethodsForm(in);
        }

        public SuperGetShippingMethodsForm[] newArray(int size) {
            return new SuperGetShippingMethodsForm[size];
        }
    };


    public OrderSummary getOrderSummary() {
        return orderSummary;
    }

    public void setOrderSummary(OrderSummary orderSummary) {
        this.orderSummary = orderSummary;
    }


    public JSONObject getFormJSON() {
        return formJSON;
    }

    public void setFormJSON(JSONObject formJSON) {
        this.formJSON = formJSON;
    }
}
