package com.bamilo.android.appmodule.bamiloapp.helpers.account;

import android.os.Bundle;

import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.requests.BaseRequest;
import com.bamilo.android.framework.service.requests.RequestBundle;
import com.bamilo.android.framework.service.rest.interfaces.AigApiInterface;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.appmodule.bamiloapp.utils.deeplink.TargetLink;

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

    public static Bundle createBundle(String action) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_END_POINT_KEY, "/" + RestConstants.FORMS + "/" + TargetLink.getIdFromTargetLink(action));
        return bundle;
    }

}
