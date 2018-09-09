package com.bamilo.android.appmodule.bamiloapp.helpers.account;

import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.framework.service.requests.BaseRequest;
import com.bamilo.android.framework.service.requests.RequestBundle;
import com.bamilo.android.framework.service.rest.interfaces.AigApiInterface;
import com.bamilo.android.framework.service.utils.EventTask;
import com.bamilo.android.framework.service.utils.EventType;

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
