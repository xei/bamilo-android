package com.mobile.helpers.order;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;

/**
 * Helper to get the return reason form
 */
public class GetReturnReasonFormHelper extends SuperBaseHelper {

    @Override
    public EventType getEventType() {
        return EventType.GET_RETURN_REASON_FORM;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getReturnReasonForm);
    }
    
}
