package com.mobile.helpers.order;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.EventType;

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
