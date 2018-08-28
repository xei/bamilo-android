package com.bamilo.android.appmodule.bamiloapp.helpers.products;

import android.os.Bundle;

import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.framework.service.requests.BaseRequest;
import com.bamilo.android.framework.service.requests.RequestBundle;
import com.bamilo.android.framework.service.rest.interfaces.AigApiInterface;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.EventTask;
import com.bamilo.android.framework.service.utils.EventType;

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
