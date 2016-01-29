package com.mobile.helpers.checkout;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;

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
