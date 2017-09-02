package com.mobile.helpers.address;

import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.Constants;
import com.mobile.service.utils.EventTask;
import com.mobile.service.utils.EventType;
import com.mobile.utils.deeplink.TargetLink;

/**
 * Helper used to get the phone prefixes.
 * @author spereira
 */
public class PhonePrefixesHelper extends SuperBaseHelper {
    
    public static String TAG = PhonePrefixesHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.GET_PHONE_PREFIXES;
    }

    @Override
    public EventTask getEventTask() {
        return EventTask.ACTION_TASK;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getPhonePrefixes);
    }

    public static Bundle createBundle(String url) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_END_POINT_KEY, "/" + TargetLink.getIdFromTargetLink(url));
        return bundle;
    }

}
