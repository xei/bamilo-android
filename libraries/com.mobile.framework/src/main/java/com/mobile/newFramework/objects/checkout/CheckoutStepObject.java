package com.mobile.newFramework.objects.checkout;

import android.support.annotation.Nullable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.output.Print;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class used to save the multi step entity.
 * @author rsoares
 */
public class CheckoutStepObject implements IJSONSerializable {

    private String mNextCheckoutStep;

    public CheckoutStepObject() {
    }

    @Override
    public boolean initialize(JSONObject jsonObject) {
        try {
            mNextCheckoutStep = jsonObject.getJSONObject(RestConstants.MULTI_STEP_ENTITY).getString(RestConstants.NEXT_STEP);
        } catch (JSONException e) {
            Print.w(CheckoutStepObject.class.getSimpleName(), "WARNING: JSE ON PARSING MULTI STPEP");
            return false;
        }
        return true;
    }

    @Nullable
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
