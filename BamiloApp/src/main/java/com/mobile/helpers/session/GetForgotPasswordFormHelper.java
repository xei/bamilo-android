package com.mobile.helpers.session;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.EventType;

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
