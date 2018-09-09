package com.bamilo.android.appmodule.bamiloapp.helpers.account;

import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.framework.service.requests.BaseRequest;
import com.bamilo.android.framework.service.requests.RequestBundle;
import com.bamilo.android.framework.service.rest.interfaces.AigApiInterface;
import com.bamilo.android.framework.service.utils.EventType;

/**
 * Helper used to get the form to subscribe newsletters.
 */
public class GetNewslettersFormHelper extends SuperBaseHelper {

    @Override
    public EventType getEventType() {
        return EventType.GET_NEWSLETTER_PREFERENCES_FORM_EVENT;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getNewsletterForm);
    }

}

