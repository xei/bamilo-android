package com.mobile.helpers.address;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;

/**
 * Helper used to create an address 
 * @author sergiopereira
 */
public class EditAddressHelper extends SuperBaseHelper {
    
    protected static String TAG = EditAddressHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.EDIT_ADDRESS_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.SMALL_TASK;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.editAddress);
    }

}
