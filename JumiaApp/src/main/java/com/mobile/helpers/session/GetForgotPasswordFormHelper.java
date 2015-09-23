package com.mobile.helpers.session;

import android.net.Uri;
import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.forms.FormData;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.RestUrlUtils;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;

/**
 * Helper to get the forgot password form
 */
public class GetForgotPasswordFormHelper extends SuperBaseHelper {

    private static String TAG = GetForgotPasswordFormHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.GET_FORGET_PASSWORD_FORM_EVENT;
    }

    @Override
    protected String getRequestUrl(Bundle args) {
        String url = EventType.GET_FORGET_PASSWORD_FORM_FALLBACK_EVENT.action;
        try {
            FormData formData = JumiaApplication.INSTANCE.getFormDataRegistry().get(mEventType.action);
            url = formData.getUrl();
        } catch (NullPointerException e) {
            Print.w(TAG, "FORM DATA IS NULL THEN GET LOGIN FORM FALLBACK", e);
        }
        return RestUrlUtils.completeUri(Uri.parse(url)).toString();
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getForgotPasswordForm);
    }
    
}
