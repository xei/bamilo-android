package com.mobile.helpers.checkout;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;

import java.util.HashMap;
import java.util.Map;

/**
 * Track order helper
 */
public class GetTrackOrderHelper extends SuperBaseHelper {

    private static String TAG = GetTrackOrderHelper.class.getSimpleName();

    public static final String ORDER_NR = "ordernr";

    @Override
    public EventType getEventType() {
        return EventType.TRACK_ORDER_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.SMALL_TASK;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
//        new TrackOrder(requestBundle, this).execute();
        new BaseRequest(requestBundle, this).execute(AigApiInterface.trackOrder);
    }

    public static Bundle createBundle(String orderNr) {
        ContentValues values = new ContentValues();
        values.put(ORDER_NR, orderNr);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        return bundle;
    }
}