package com.mobile.newFramework.objects.checkout;

import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.customer.Customer;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * CheckoutStepLogin
 *
 * @author rsoares
 * @modified sergiopereira
 */
public class CheckoutStepLogin extends CheckoutStepObject {

    private Customer customer;

    public CheckoutStepLogin() {
        // ...
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        // Next step
        super.initialize(jsonObject);
        // Customer
        customer = new Customer();
        customer.initialize(jsonObject);
        return true;
    }

    public Customer getCustomer() {
        return customer;
    }

    @Override
    public JSONObject toJSON() {
        return super.toJSON();
    }

    @Override
    public RequiredJson getRequiredJson() {
        return super.getRequiredJson();
    }


}
