package com.mobile.helpers.account;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;

/**
 * Helper used to get the form with user info pre-filled
 * @author Paulo Carvalho
 */
public class GetUserDataFormHelper extends SuperBaseHelper {
    
    protected static String TAG = GetUserDataFormHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.EDIT_USER_DATA_FORM_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.SMALL_TASK;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getUserDataForm);
    }

}
