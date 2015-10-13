package com.mobile.helpers.configs;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.utils.EventType;

/**
 * Created by rsoares on 10/13/15.
 */
public class GetFaqTermsHelper extends SuperBaseHelper{

    @Override
    protected void onRequest(RequestBundle requestBundle) {

    }

    @Override
    public EventType getEventType() {
        return EventType.GET_FAQ_TERMS;
    }
}
