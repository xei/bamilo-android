package com.mobile.helpers;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.Constants;
import com.mobile.service.utils.EventTask;
import com.mobile.service.utils.EventType;
import com.mobile.utils.deeplink.TargetLink;

/**
 * Helper used to perform a submission for any kind of form regardless the type and is waiting a void response (Callback<BaseResponse<Void>>).<br>
 * Please don't use this helper if you have different formEvents to be handled on the same context.
 * @author Manuel Silva
 * @modified sergiopereira
 */
public class SubmitFormHelper extends SuperBaseHelper {

    @Override
    public EventType getEventType() {
        return EventType.SUBMIT_FORM;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.ACTION_TASK;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.submitFormAction);
    }

    public static Bundle createBundle(String endpoint, ContentValues values) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_END_POINT_KEY, "/" + TargetLink.getIdFromTargetLink(endpoint));
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        return bundle;
    }

}
