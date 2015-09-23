/**
 * 
 */
package com.mobile.helpers.session;

import android.net.Uri;
import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.RestUrlUtils;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;

/**
 * 
 * @author sergiopereira
 *
 */
public class GetSignUpFormHelper extends SuperBaseHelper {
    
    public static String TAG = GetSignUpFormHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.GET_SIGNUP_FORM_EVENT;
    }

    @Override
    protected String getRequestUrl(Bundle args) {
        return RestUrlUtils.completeUri(Uri.parse(EventType.GET_SIGNUP_FORM_FALLBACK_EVENT.action)).toString();
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getSignUpForm);
    }

}
