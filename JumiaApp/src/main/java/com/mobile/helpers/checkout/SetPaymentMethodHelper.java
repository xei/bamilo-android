package com.mobile.helpers.checkout;

import com.mobile.helpers.NextStepStruct;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.checkout.SetPaymentMethod;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;

/**
 * Helper used to set the shipping address
 */
public class SetPaymentMethodHelper extends SuperBaseHelper {
    
    private static String TAG = SetPaymentMethodHelper.class.getSimpleName();
    
    @Override
    public EventType getEventType() {
        return EventType.SET_PAYMENT_METHOD_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.ACTION_TASK;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.setPaymentMethod);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);
        SetPaymentMethod responseData = (SetPaymentMethod) baseResponse.getMetadata().getData();
        NextStepStruct nextStepStruct = new NextStepStruct(responseData);
        baseResponse.getMetadata().setData(nextStepStruct);
        Print.i(TAG, "ORDER SUMMARY: " + responseData.getOrderSummary().toString());
    }

}
