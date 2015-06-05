package com.mobile.newFramework.objects.checkout;

import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONObject;

/**
 * Created by rsoares on 6/1/15.
 */
public abstract class CheckoutStepObject {
    private String nextStep;

    public void setCheckoutNextStep(JSONObject jsonObject){
        if(jsonObject.has(RestConstants.JSON_NATIVE_CHECKOUT_TAG)){
            // From native checkout tag
            JSONObject checkoutJson = jsonObject.optJSONObject(RestConstants.JSON_NATIVE_CHECKOUT_TAG);
            nextStep = checkoutJson.optString(RestConstants.JSON_NEXT_STEP_TAG, null);
        } else if(jsonObject.has(RestConstants.JSON_NEXT_STEP_TAG)){
            // From next step tag
            nextStep = jsonObject.optString(RestConstants.JSON_NEXT_STEP_TAG, null);
        } else if (jsonObject.has(RestConstants.JSON_DATA_TAG) &&
                jsonObject.optJSONObject(RestConstants.JSON_DATA_TAG) != null &&
                jsonObject.optJSONObject(RestConstants.JSON_DATA_TAG).has(RestConstants.JSON_NATIVE_CHECKOUT_TAG)) {
            // From data and native checkout tag
            JSONObject dataJson = jsonObject.optJSONObject(RestConstants.JSON_DATA_TAG);
            JSONObject checkoutJson = dataJson.optJSONObject(RestConstants.JSON_NATIVE_CHECKOUT_TAG);
            nextStep = checkoutJson.optString(RestConstants.JSON_NEXT_STEP_TAG, null);
        }
    }

    public String getNextStep() {
        return nextStep;
    }
}
