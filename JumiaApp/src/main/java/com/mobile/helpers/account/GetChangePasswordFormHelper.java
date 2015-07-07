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
import com.mobile.newFramework.utils.output.Print;

/**
 * Created by rsoares on 6/12/15.
 */
public class GetChangePasswordFormHelper extends SuperBaseHelper{

    public static final String PASSWORD_ID = "password";

    public static final String PASSWORD2_ID = "password2";

    private static String TAG = GetCustomerHelper.class.getSimpleName();

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle,this).execute(AigApiInterface.getChangePasswordForm);
    }

    @Override
    public EventType getEventType() {
        return EventType.GET_CHANGE_PASSWORD_FORM_FALLBACK_EVENT;
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST COMPLETE: " + baseResponse.hadSuccess());
        Bundle bundle = generateSuccessBundle(baseResponse);
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, (Form)baseResponse.getMetadata().getData());
        mRequester.onRequestComplete(bundle);
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST ERROR: " + baseResponse.getMessage());
        Bundle bundle = generateErrorBundle(baseResponse);
        mRequester.onRequestError(bundle);
    }
}
