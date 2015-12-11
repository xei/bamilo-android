/**
 * 
 */
package com.mobile.helpers.checkout;

import com.mobile.helpers.NextStepStruct;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.checkout.SetBillingAddress;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;

/**
 * Helper used to set the shipping address 
 * @author sergiopereira
 */
public class SetBillingAddressHelper extends SuperBaseHelper {

    @Override
    public EventType getEventType() {
        return EventType.SET_BILLING_ADDRESS_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.ACTION_TASK;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.setBillingAddress);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);
        SetBillingAddress billing = (SetBillingAddress) baseResponse.getMetadata().getData();
        NextStepStruct nextStepStruct = new NextStepStruct(billing);
        baseResponse.getMetadata().setData(nextStepStruct);
    }

}
