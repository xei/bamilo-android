package com.mobile.helpers.session;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;

/**
 * Helper to get the forgot password form
 */
public class GetForgotPasswordFormHelper extends SuperBaseHelper {

    public static String TAG = GetForgotPasswordFormHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.GET_FORGET_PASSWORD_FORM_EVENT;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getForgotPasswordForm);
    }
    
}
