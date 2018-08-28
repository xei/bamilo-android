package com.bamilo.android.appmodule.bamiloapp.helpers.checkout;

import android.os.Bundle;

import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.requests.BaseRequest;
import com.bamilo.android.framework.service.requests.RequestBundle;
import com.bamilo.android.framework.service.rest.interfaces.AigApiInterface;
import com.bamilo.android.framework.service.utils.EventTask;
import com.bamilo.android.framework.service.utils.EventType;

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
