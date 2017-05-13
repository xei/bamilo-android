package com.mobile.helpers.address;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.service.pojo.RestConstants;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.EventTask;
import com.mobile.service.utils.EventType;

/**
 * Created by rsoares on 2/25/15.
 */
public class SetDefaultBillingAddressHelper extends SuperBaseHelper {

    public static String TAG = SetDefaultBillingAddressHelper.class.getSimpleName();

    public static final String ID = RestConstants.ID;

    public static final String TYPE = RestConstants.TYPE;

    @Override
    public EventType getEventType() {
        return EventType.SET_DEFAULT_BILLING_ADDRESS;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.ACTION_TASK;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.setDefaultBillingAddress);
    }

}
