package com.mobile.helpers.products;

import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;

import java.util.ArrayList;

/**
 * Validate one or more products on the API and actualize their information
 *
 * @author Paulo Carvalho
 */
public class ValidateProductHelper extends SuperBaseHelper {

    protected static final String TAG = ValidateProductHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.VALIDATE_PRODUCTS;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.ACTION_TASK;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.validateProducts);
    }

    /**
     * Method used to create a request bundle.
     */
    public static Bundle createBundle(ArrayList<String> skuList) {

        // Request data
        Bundle bundle = new Bundle();
        bundle.putStringArrayList(Constants.BUNDLE_ARRAY_KEY, skuList);
        return bundle;
    }
}
