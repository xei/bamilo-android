package com.bamilo.android.appmodule.bamiloapp.helpers.session;

import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.framework.service.requests.BaseRequest;
import com.bamilo.android.framework.service.requests.RequestBundle;
import com.bamilo.android.framework.service.rest.interfaces.AigApiInterface;
import com.bamilo.android.framework.service.utils.EventType;

/**
 * Helper to get the forgot password form
 */
public class GetForgotPasswordFormHelper extends SuperBaseHelper {

    @Override
    public EventType getEventType() {
        return EventType.GET_FORGET_PASSWORD_FORM_EVENT;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getForgotPasswordForm);
    }
    
}
