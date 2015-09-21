package com.mobile.newFramework.objects.checkout;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rsoares on 6/1/15.
 */
public class CheckoutStepObject implements IJSONSerializable {

    private String nextStep;

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        nextStep = jsonObject.getJSONObject(RestConstants.MULTI_STEP_ENTITY).getString(RestConstants.JSON_NEXT_STEP_TAG);
        return true;
    }

    public String getNextStep() {
        return nextStep;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return RequiredJson.METADATA;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

}
