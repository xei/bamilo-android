package com.mobile.newFramework.objects.user;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.checkout.CheckoutService;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rsoares on 6/1/15.
 */
public class LoginCustomer extends CheckoutService implements IJSONSerializable{
    private Customer customer;

    public LoginCustomer(){

    }

    public LoginCustomer(JSONObject jsonObject) throws JSONException {
        initialize(jsonObject);
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
