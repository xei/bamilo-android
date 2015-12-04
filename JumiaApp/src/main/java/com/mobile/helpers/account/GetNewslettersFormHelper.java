package com.mobile.helpers.account;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;

/**
 * Helper used to get the form to edit an address
 */
public class GetNewslettersFormHelper extends SuperBaseHelper {
    
    // private static String TAG = GetNewslettersFormHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.GET_NEWSLETTERS_FORM_EVENT;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getNewsletterForm);
    }

}

