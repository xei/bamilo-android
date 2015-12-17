package com.mobile.helpers.checkout;

import com.mobile.app.JumiaApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.checkout.MultiStepPayment;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;

/**
 * Helper used to get the payment methods
 */
public class GetStepPaymentHelper extends SuperBaseHelper {

    public static String TAG = GetStepPaymentHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.GET_MULTI_STEP_PAYMENT;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getMultiStepPayment);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);
        MultiStepPayment responseData = (MultiStepPayment) baseResponse.getContentData();
        JumiaApplication.setPaymentsInfoList(responseData.getForm().getFieldKeyMap().get(RestConstants.PAYMENT_METHOD).getPaymentInfoList());
    }

}
