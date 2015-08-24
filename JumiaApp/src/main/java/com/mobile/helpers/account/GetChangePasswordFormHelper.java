package com.mobile.helpers.account;

import android.os.Bundle;

import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.forms.Form;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventType;

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

    @Override
    public void createSuccessBundleParams(BaseResponse baseResponse, Bundle bundle) {
        super.createSuccessBundleParams(baseResponse, bundle);
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, (Form) baseResponse.getMetadata().getData());
    }
}
