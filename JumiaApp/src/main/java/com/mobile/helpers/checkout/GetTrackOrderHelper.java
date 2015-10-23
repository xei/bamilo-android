package com.mobile.helpers.checkout;

import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
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
    protected Map<String, String> getRequestData(Bundle args) {
        Map<String, String> data = new HashMap<>();
        data.put(ORDER_NR, args.getString(ORDER_NR));
        return data;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
//        new TrackOrder(requestBundle, this).execute();
        new BaseRequest(requestBundle, this).execute(AigApiInterface.trackOrder);
    }


    /**
     * Method used to create a request bundle.
     */
    public static Bundle createBundle(String orderNumber) {
        // Item data
        Bundle bundle = new Bundle();
        bundle.putString(GetTrackOrderHelper.ORDER_NR, orderNumber);
        return bundle;
    }

}