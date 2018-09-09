package com.bamilo.android.appmodule.bamiloapp.helpers.address;

import android.content.ContentValues;
import android.os.Bundle;

import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.requests.BaseRequest;
import com.bamilo.android.framework.service.requests.RequestBundle;
import com.bamilo.android.framework.service.rest.interfaces.AigApiInterface;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.EventType;

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
