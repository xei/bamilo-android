package com.bamilo.android.appmodule.bamiloapp.helpers.checkout;

import android.content.ContentValues;
import android.os.Bundle;

import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.requests.BaseRequest;
import com.bamilo.android.framework.service.requests.RequestBundle;
import com.bamilo.android.framework.service.rest.interfaces.AigApiInterface;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.EventTask;
import com.bamilo.android.framework.service.utils.EventType;

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