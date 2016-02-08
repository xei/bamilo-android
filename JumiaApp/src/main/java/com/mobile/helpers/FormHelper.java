package com.mobile.helpers;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;
import com.mobile.utils.deeplink.TargetLink;

/**
 * Helper used to perform a request for any kind of form regardless the type.
 * Please don't use this helper if you have different formEvents to be handled on the same context.
 * @author Manuel Silva
 */
public class FormHelper extends SuperBaseHelper {
    
    protected static String TAG = FormHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.FORM_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.ACTION_TASK;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.editAddress);
    }

    public static Bundle createBundle(String endpoint, ContentValues values) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_END_POINT_KEY, "/" + TargetLink.getIdFromTargetLink(endpoint));
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        return bundle;
    }

}
