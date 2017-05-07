package com.mobile.helpers.checkout;


import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;


/**
 * Helper used to get all customer addresses 
 * @author sergiopereira
 *
 */
public class GetStepAddressesHelper extends SuperBaseHelper {
    
    public static String TAG = GetStepAddressesHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.GET_MULTI_STEP_ADDRESSES;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getMultiStepAddresses);
    }

}