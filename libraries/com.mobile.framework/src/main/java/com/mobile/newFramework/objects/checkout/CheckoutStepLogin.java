package com.mobile.newFramework.objects.checkout;

import android.support.annotation.Nullable;

import com.mobile.newFramework.objects.customer.Customer;

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
    public boolean initialize(JSONObject jsonObject) {
        // Next checkout step
        super.initialize(jsonObject);
        // Customer
        customer = new Customer();
        return customer.initialize(jsonObject);
    }

    @Nullable
    public Customer getCustomer() {
        return customer;
    }

}
