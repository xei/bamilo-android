package com.mobile.helpers.account;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.EventType;

/**
 * Created by rsoares on 6/12/15.
 */
public class GetChangePasswordFormHelper extends SuperBaseHelper{

    public static String TAG = GetCustomerHelper.class.getSimpleName();

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle,this).execute(AigApiInterface.getChangePasswordForm);
    }

    @Override
    public EventType getEventType() {
        return EventType.GET_CHANGE_PASSWORD_FORM_EVENT;
    }

}
