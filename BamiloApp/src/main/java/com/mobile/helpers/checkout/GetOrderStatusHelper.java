package com.mobile.helpers.checkout;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.service.pojo.RestConstants;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.Constants;
import com.mobile.service.utils.EventTask;
import com.mobile.service.utils.EventType;

/**
 * Order status helper
 * @author spereira
 */
public class GetOrderStatusHelper extends SuperBaseHelper {

    @Override
    public EventType getEventType() {
        return EventType.TRACK_ORDER_EVENT;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.trackOrder);
    }

    public static Bundle createBundle(String orderNr, EventTask task) {
        ContentValues values = new ContentValues();
        values.put(RestConstants.ORDERNR, orderNr);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_PATH_KEY, values);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TASK, task);
        return bundle;
    }
}