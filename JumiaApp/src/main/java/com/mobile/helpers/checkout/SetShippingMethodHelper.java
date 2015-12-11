/**
 * 
 */
package com.mobile.helpers.checkout;

import com.mobile.helpers.NextStepStruct;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.checkout.SetShippingMethod;
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
public class SetShippingMethodHelper extends SuperBaseHelper {
    
    @Override
    public EventType getEventType() {
        return EventType.SET_SHIPPING_METHOD_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.ACTION_TASK;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.setShippingMethod);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);
        SetShippingMethod shipping = (SetShippingMethod) baseResponse.getMetadata().getData();
        NextStepStruct shippingMethodStruct = new NextStepStruct(shipping);
        baseResponse.getMetadata().setData(shippingMethodStruct);
    }

}
