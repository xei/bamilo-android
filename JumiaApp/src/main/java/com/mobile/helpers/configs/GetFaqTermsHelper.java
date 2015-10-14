package com.mobile.helpers.configs;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;

public class GetFaqTermsHelper extends SuperBaseHelper{

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getFaqTerms);
    }

    @Override
    public EventType getEventType() {
        return EventType.GET_FAQ_TERMS;
    }


}
