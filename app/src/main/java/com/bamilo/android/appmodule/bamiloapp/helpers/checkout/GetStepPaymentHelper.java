package com.bamilo.android.appmodule.bamiloapp.helpers.checkout;

import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.framework.service.requests.BaseRequest;
import com.bamilo.android.framework.service.requests.RequestBundle;
import com.bamilo.android.framework.service.rest.interfaces.AigApiInterface;
import com.bamilo.android.framework.service.utils.EventType;

/**
 * Helper used to get the payment methods
 */
public class GetStepPaymentHelper extends SuperBaseHelper {

    @Override
    public EventType getEventType() {
        return EventType.GET_MULTI_STEP_PAYMENT;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getMultiStepPayment);
    }

}
