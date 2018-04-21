package com.mobile.helpers.checkout;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.EventType;

/**
 * Get Shopping Cart Items helper
 *
 * @author Manuel Silva
 *
 */
public class GetStepFinishHelper extends SuperBaseHelper {

    @Override
    public EventType getEventType() {
        return EventType.GET_MULTI_STEP_FINISH;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getMultiStepFinish);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);
    }

}
