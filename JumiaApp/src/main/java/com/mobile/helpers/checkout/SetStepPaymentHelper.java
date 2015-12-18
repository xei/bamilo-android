package com.mobile.helpers.checkout;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.helpers.NextStepStruct;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.checkout.CheckoutStepObject;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;
import com.mobile.utils.deeplink.TargetLink;

/**
 * Helper used to set the shipping address
 */
public class SetStepPaymentHelper extends SuperBaseHelper {
    
    @Override
    public EventType getEventType() {
        return EventType.SET_MULTI_STEP_PAYMENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.ACTION_TASK;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.setMultiStepPayment);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);
        CheckoutStepObject nextStep = (CheckoutStepObject) baseResponse.getContentData();
        NextStepStruct nextStepStruct = new NextStepStruct(nextStep);
        baseResponse.getMetadata().setData(nextStepStruct);
    }

    public static Bundle createBundle(String endpoint, ContentValues values) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_END_POINT_KEY, "/" + TargetLink.getIdFromTargetLink(endpoint));
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        return bundle;
    }

}
