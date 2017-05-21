package com.mobile.helpers.order;

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
 * Helper used to get the form with Return Methods
 */
public class ReturnFinishHelper extends SuperBaseHelper {

    @Override
    public EventType getEventType() {
        return EventType.RETURN_FINISH_EVENT;
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
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.setReturnFinish);
    }

    /**
     * Method used to create a request bundle.
     */
    public static Bundle createBundle(ContentValues values) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        return bundle;
    }
}
