package com.mobile.helpers.checkout;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.helpers.NextStepStruct;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.service.objects.checkout.CheckoutStepObject;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.pojo.RestConstants;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.Constants;
import com.mobile.service.utils.EventTask;
import com.mobile.service.utils.EventType;

/**
 * Helper used to set the shipping address
 * @author sergiopereira
 */
public class SetStepAddressesHelper extends SuperBaseHelper {

    @Override
    public EventType getEventType() {
        return EventType.SET_MULTI_STEP_ADDRESSES;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.ACTION_TASK;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.setMultiStepAddresses);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);
        CheckoutStepObject nextCheckoutStep = (CheckoutStepObject) baseResponse.getContentData();
        NextStepStruct nextStepStruct = new NextStepStruct(nextCheckoutStep);
        baseResponse.getMetadata().setData(nextStepStruct);
    }

    public static Bundle createBundle(int billing, int shipping) {
        ContentValues values = new ContentValues();
        values.put(RestConstants.ADDRESSES_BILLING_ID, billing);
        values.put(RestConstants.ADDRESSES_SHIPPING_ID, shipping);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        return bundle;
    }
}
