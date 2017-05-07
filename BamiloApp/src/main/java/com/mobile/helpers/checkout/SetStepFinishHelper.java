package com.mobile.helpers.checkout;

import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper used to finish the checkout process.
 * @author sergiopereira
 */
public class SetStepFinishHelper extends SuperBaseHelper {
    
    public static String TAG = SetStepFinishHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.SET_MULTI_STEP_FINISH;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.ACTION_TASK;
    }

    @Override
    protected RequestBundle createRequest(Bundle args) {
        return super.createRequest(args);
    }

    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        Map<String, String> data = new HashMap<>();
        data.put(RestConstants.APP, RestConstants.ANDROID);
        data.put(RestConstants.CUSTOMER_DEVICE, args.getString(RestConstants.USER_AGENT));
        return data;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.setMultiStepFinish);
    }

    public static Bundle createBundle(String userAgent) {
        Bundle bundle = new Bundle();
        bundle.putString(RestConstants.USER_AGENT, userAgent);
        return bundle;
    }

}
