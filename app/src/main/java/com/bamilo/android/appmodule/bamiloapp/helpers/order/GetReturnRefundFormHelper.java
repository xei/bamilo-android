package com.bamilo.android.appmodule.bamiloapp.helpers.order;

import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.framework.service.requests.BaseRequest;
import com.bamilo.android.framework.service.requests.RequestBundle;
import com.bamilo.android.framework.service.rest.interfaces.AigApiInterface;
import com.bamilo.android.framework.service.utils.EventType;

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
