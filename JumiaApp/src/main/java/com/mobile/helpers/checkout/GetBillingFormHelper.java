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
public class GetBillingFormHelper extends SuperBaseHelper {
    
    public static String TAG = GetBillingFormHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.GET_BILLING_FORM_EVENT;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getBillingAddressForm);
    }

}