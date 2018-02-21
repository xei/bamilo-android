package com.mobile.helpers.session;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.Constants;
import com.mobile.service.utils.EventTask;
import com.mobile.service.utils.EventType;

/**
 * Created by mohsen on 2/6/18.
 */

public class MobileVerificationHelper extends SuperBaseHelper {

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.verifyPhoneNumber);
    }

    @Override
    public EventType getEventType() {
        return EventType.MOBILE_VERIFICATION_EVENT;
    }

    @Override
    public EventTask getEventTask() {
        return EventTask.ACTION_TASK;
    }

    public static Bundle createBundle(ContentValues params) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_END_POINT_KEY, "/" + EventType.MOBILE_VERIFICATION_EVENT.action);
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, params);
        return bundle;
    }
}
