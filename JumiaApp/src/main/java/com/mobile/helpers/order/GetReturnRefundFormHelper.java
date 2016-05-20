package com.mobile.helpers.order;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;

/**
 * Helper used to get the form with Return Methods
 */
public class GetReturnRefundFormHelper extends SuperBaseHelper {

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getReturnRefundForm);
    }

    @Override
    public EventType getEventType() {
        return EventType.RETURN_REFUND_FORM_EVENT;
    }
}
