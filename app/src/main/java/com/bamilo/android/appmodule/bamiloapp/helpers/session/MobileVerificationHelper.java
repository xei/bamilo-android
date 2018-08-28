package com.bamilo.android.appmodule.bamiloapp.helpers.session;

import android.content.ContentValues;
import android.os.Bundle;

import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.framework.service.requests.BaseRequest;
import com.bamilo.android.framework.service.requests.RequestBundle;
import com.bamilo.android.framework.service.rest.interfaces.AigApiInterface;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.EventTask;
import com.bamilo.android.framework.service.utils.EventType;

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
