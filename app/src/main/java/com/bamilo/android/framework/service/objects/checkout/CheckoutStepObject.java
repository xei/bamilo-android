package com.bamilo.android.framework.service.objects.checkout;

import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rsoares on 6/1/15.
 */
public class CheckoutStepObject implements IJSONSerializable {

    private String mNextCheckoutStep;

    public CheckoutStepObject() {
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        mNextCheckoutStep = jsonObject.getJSONObject(RestConstants.MULTI_STEP_ENTITY).getString(RestConstants.NEXT_STEP);
        return true;
    }

    public String getNextCheckoutStep() {
        return mNextCheckoutStep;
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
