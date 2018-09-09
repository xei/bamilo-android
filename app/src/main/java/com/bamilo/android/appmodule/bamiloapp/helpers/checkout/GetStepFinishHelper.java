package com.bamilo.android.appmodule.bamiloapp.helpers.checkout;

import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.requests.BaseRequest;
import com.bamilo.android.framework.service.requests.RequestBundle;
import com.bamilo.android.framework.service.rest.interfaces.AigApiInterface;
import com.bamilo.android.framework.service.utils.EventType;

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
