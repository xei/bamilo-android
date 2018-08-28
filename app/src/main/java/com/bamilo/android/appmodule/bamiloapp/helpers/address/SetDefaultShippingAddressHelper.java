package com.bamilo.android.appmodule.bamiloapp.helpers.address;

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
 * Created by rsoares on 2/25/15.
 */
public class SetDefaultShippingAddressHelper extends SuperBaseHelper {

    public static String TAG = SetDefaultShippingAddressHelper.class.getSimpleName();

    public static final String ID = RestConstants.ID;

    public static final String TYPE = RestConstants.TYPE;

    @Override
    public EventType getEventType() {
        return EventType.SET_DEFAULT_SHIPPING_ADDRESS;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.ACTION_TASK;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.setDefaultShippingAddress);
    }

    public static Bundle createBundle(int id) {
        ContentValues values = new ContentValues();
        values.put(SetDefaultShippingAddressHelper.ID, id);
        values.put(SetDefaultShippingAddressHelper.TYPE, "shipping");
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        return bundle;
    }

}
