/**
 * 
 */
package com.mobile.helpers.checkout;

import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.checkout.CheckoutFinish;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper used to ...
 * @author sergiopereira
 */
public class SetStepFinishHelper extends SuperBaseHelper {
    
    public static String TAG = SetStepFinishHelper.class.getSimpleName();
    
    public static final String USER_AGENT = "user_agent";

    @Override
    public EventType getEventType() {
        return EventType.SET_MULTI_STEP_FINISH;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.ACTION_TASK;
    }

    @Override
    protected RequestBundle createRequest(Bundle args) {
        return super.createRequest(args);
    }

    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        Map<String, String> data = new HashMap<>();
        data.put("app", "android");
        data.put("customer_device", args.getString(USER_AGENT));
        return data;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.setMultiStepFinish);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);
        CheckoutFinish checkoutFinish = (CheckoutFinish)baseResponse.getContentData();
        JumiaApplication.INSTANCE.setPaymentMethodForm(checkoutFinish.getPaymentMethodForm());
        JumiaApplication.INSTANCE.getPaymentMethodForm().setOrderNumber(checkoutFinish.getOrderNumber());
    }

}
