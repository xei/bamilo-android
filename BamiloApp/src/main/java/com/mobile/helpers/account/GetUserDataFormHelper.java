package com.mobile.helpers.account;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.EventTask;
import com.mobile.service.utils.EventType;

/**
 * Helper used to get the form with user info pre-filled
 * @author Paulo Carvalho
 */
public class GetUserDataFormHelper extends SuperBaseHelper {
    
    protected static String TAG = GetUserDataFormHelper.class.getSimpleName();

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getUserDataForm);
    }

    @Override
    public EventType getEventType() {
        return EventType.EDIT_USER_DATA_FORM_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.ACTION_TASK;
    }

}