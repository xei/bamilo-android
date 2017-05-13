package com.mobile.helpers.session;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.EventType;

/**
 * Example helper
 * 
 * @author Guilherme Silva
 * 
 */
public class GetRegisterFormHelper extends SuperBaseHelper {
    
    @Override
    public EventType getEventType() {
        return EventType.GET_REGISTRATION_FORM_EVENT;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getRegisterForm);
    }

}
