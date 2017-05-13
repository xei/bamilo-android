package com.mobile.service.objects.checkout;

import com.mobile.service.objects.customer.Customer;

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
        super();
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        // Next checkout step
        super.initialize(jsonObject);
        // Customer
        customer = new Customer();
        return customer.initialize(jsonObject);
    }

    public Customer getCustomer() {
        return customer;
    }

}
