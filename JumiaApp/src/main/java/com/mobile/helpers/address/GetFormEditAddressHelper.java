package com.mobile.helpers.address;

import android.net.Uri;
import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.forms.FormData;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.RestUrlUtils;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;

/**
 * Helper used to get the form to edit an address 
 * @author sergiopereira
 */
public class GetFormEditAddressHelper extends SuperBaseHelper {
    
    private static String TAG = GetFormEditAddressHelper.class.getSimpleName();

    public static final String SELECTED_ADDRESS_ID = RestConstants.ID;

    @Override
    public EventType getEventType() {
        return EventType.GET_EDIT_ADDRESS_FORM_EVENT;
    }

    @Override
    protected String getRequestUrl(Bundle args) {
        String url = EventType.GET_EDIT_ADDRESS_FORM_FALLBACK_EVENT.action;
        try {
            FormData formData = JumiaApplication.INSTANCE.getFormDataRegistry().get(mEventType.action);
            url = formData.getUrl();
        } catch (NullPointerException e) {
            Print.w(TAG, "FORM DATA IS NULL THEN GET FORM FALLBACK", e);
        }
        return RestUrlUtils.completeUri(Uri.parse(url)).toString();
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getEditAddressForm);
    }

}

