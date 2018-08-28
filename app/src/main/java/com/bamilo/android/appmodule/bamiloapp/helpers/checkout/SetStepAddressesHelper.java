package com.bamilo.android.appmodule.bamiloapp.helpers.checkout;

import android.content.ContentValues;
import android.os.Bundle;

import com.bamilo.android.appmodule.bamiloapp.helpers.NextStepStruct;
import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.framework.service.objects.checkout.CheckoutStepObject;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.requests.BaseRequest;
import com.bamilo.android.framework.service.requests.RequestBundle;
import com.bamilo.android.framework.service.rest.interfaces.AigApiInterface;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.EventTask;
import com.bamilo.android.framework.service.utils.EventType;

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
