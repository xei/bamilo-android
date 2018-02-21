package com.mobile.helpers.address;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.service.pojo.RestConstants;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.Constants;
import com.mobile.service.utils.EventType;

/**
 * Created on 11/21/2017.
 */

public class GetAddressHelper extends SuperBaseHelper {
    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getAddress);
    }

    @Override
    public EventType getEventType() {
        return EventType.GET_EVENT_EVENT;
    }

    public static Bundle createBundle(String addressId) {
        ContentValues values = new ContentValues();
        values.put(RestConstants.ID, addressId);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_PATH_KEY, values);
        return bundle;
    }
}
