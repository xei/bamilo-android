package com.mobile.helpers.session;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;

/**
 * Example helper
 * 
 * @author Guilherme Silva
 * 
 */
public class GetLoginFormHelper extends SuperBaseHelper {
    
    public static String TAG = GetLoginFormHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.GET_LOGIN_FORM_EVENT;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getLoginForm);
    }

}
