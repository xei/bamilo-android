package com.mobile.newFramework.objects.checkout;

import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.customer.Customer;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rsoares on 6/1/15.
 */
public class CheckoutStepLogin extends CheckoutStepObject {
    private Customer customer;

    public CheckoutStepLogin(){

    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        customer = new Customer(jsonObject);
        setCheckoutNextStep(jsonObject);
        return true;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return RequiredJson.METADATA;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }
}
