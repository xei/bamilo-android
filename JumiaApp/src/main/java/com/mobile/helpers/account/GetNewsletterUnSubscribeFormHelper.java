package com.mobile.helpers.account;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;

/**
 * Helper used to get the form to un subscribe newsletters.
 */
public class GetNewsletterUnSubscribeFormHelper extends SuperBaseHelper {

    @Override
    public EventType getEventType() {
        return EventType.GET_NEWSLETTER_UN_SUBSCRIBE_FORM;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getNewsletterUnSubscribeForm);
    }

}
