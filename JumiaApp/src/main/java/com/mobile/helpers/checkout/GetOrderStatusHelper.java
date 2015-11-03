package com.mobile.helpers.checkout;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;

/**
 * Order status helper
 */
public class GetOrderStatusHelper extends SuperBaseHelper {

//    public static String TAG = GetOrderStatusHelper.class.getSimpleName();

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
        new BaseRequest(requestBundle, this).execute(AigApiInterface.trackOrder);
    }

    public static Bundle createBundle(String orderNr) {
        ContentValues values = new ContentValues();
        values.put(RestConstants.ORDER_NR, orderNr);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        return bundle;
    }
}