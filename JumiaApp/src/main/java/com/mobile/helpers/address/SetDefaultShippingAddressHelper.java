package com.mobile.helpers.address;

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
