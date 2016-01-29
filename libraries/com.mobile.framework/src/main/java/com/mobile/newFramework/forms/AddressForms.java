package com.mobile.newFramework.forms;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by alexandrapires on 10/14/15.
 * <p/>
 * This class is used to keep two separate Form instances when creating addresses
 */
public class AddressForms implements IJSONSerializable {

    private Form mShippingForm;

    private Form mBillingForm;

    /**
     * Empty constructor
     */
    public AddressForms() {
        mShippingForm = new Form();
        mBillingForm = new Form();
    }

    /**
     * Return shipping address form
     */
    public Form getShippingForm() {
        return mShippingForm;
    }

    /**
     * Return billing address form
     */
    public Form getBillingForm() {
        return mBillingForm;
    }


    /**
     * Initialize both instances from a jsonObject Form
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        return mShippingForm.initialize(jsonObject) && mBillingForm.initialize(jsonObject);
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.METADATA;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }


}
