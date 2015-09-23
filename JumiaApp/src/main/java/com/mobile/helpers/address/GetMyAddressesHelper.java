package com.mobile.helpers.address;

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
public class GetMyAddressesHelper extends SuperBaseHelper {
    
    public static String TAG = GetMyAddressesHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.GET_CUSTOMER_ADDRESSES_EVENT;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getAddressesList);
    }

}