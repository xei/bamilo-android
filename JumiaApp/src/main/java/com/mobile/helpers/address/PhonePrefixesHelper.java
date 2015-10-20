package com.mobile.helpers.address;

import android.net.Uri;
import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.RestUrlUtils;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;

/**
 * Helper used to get the postal codes for a specific city
 */
public class PhonePrefixesHelper extends SuperBaseHelper {
    
    public static String TAG = PhonePrefixesHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.GET_PHONE_PREFIXES;
    }

    @Override
    protected String getRequestUrl(Bundle args) {
        return RestUrlUtils.completeUri(Uri.parse(args.getString(Constants.BUNDLE_URL_KEY))).toString();
    }

    @Override
    public EventTask getEventTask() {
        return EventTask.SMALL_TASK;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getPhonePrefixes);
    }

    public static Bundle createBundle(String url) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, url);
        return bundle;
    }

}
