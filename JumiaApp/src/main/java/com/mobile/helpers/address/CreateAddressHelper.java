/**
 * 
 */
package com.mobile.helpers.address;

import android.net.Uri;
import android.os.Bundle;

import com.mobile.helpers.NextStepStruct;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.checkout.CheckoutStepObject;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.RestUrlUtils;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;

/**
 * Helper used to create an address 
 * @author sergiopereira
 */
public class CreateAddressHelper extends SuperBaseHelper {
    
    public static String TAG = CreateAddressHelper.class.getSimpleName();
    
    public static final String IS_FROM_SIGNUP = "fromSignup";
    
    public static final String IS_BILLING = "isBilling";

    @Override
    public EventType getEventType() {
        return EventType.CREATE_ADDRESS_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.ACTION_TASK;
    }

    @Override
    protected String getEndPoint(Bundle args) {
        EventType type = getEventType();
        if(args.getBoolean(IS_FROM_SIGNUP) && !args.getBoolean(IS_BILLING)){
            type = EventType.CREATE_ADDRESS_SIGNUP_EVENT;
        }
        return RestUrlUtils.completeUri(Uri.parse(type.action)).toString();
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.createAddress);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);
        CheckoutStepObject checkoutStep = (CheckoutStepObject) baseResponse.getMetadata().getData();
        NextStepStruct nextStepStruct = new NextStepStruct(checkoutStep);
        baseResponse.getMetadata().setData(nextStepStruct);
    }

}
