package com.bamilo.android.appmodule.bamiloapp.helpers.account;

import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.framework.service.requests.BaseRequest;
import com.bamilo.android.framework.service.requests.RequestBundle;
import com.bamilo.android.framework.service.rest.interfaces.AigApiInterface;
import com.bamilo.android.framework.service.utils.EventType;

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
