package com.mobile.helpers.session;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.app.BamiloApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.service.objects.customer.Customer;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.Constants;
import com.mobile.service.utils.CustomerUtils;
import com.mobile.service.utils.EventTask;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.output.Print;
import com.mobile.utils.deeplink.TargetLink;

/**
 * Example helper
 *
 * @author Manuel Silva
 */
public class RegisterHelper extends SuperBaseHelper {

    @Override
    public EventType getEventType() {
        return EventType.REGISTER_ACCOUNT_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.ACTION_TASK;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.registerCustomer);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);
        Print.i("SAVE CUSTOMER CREDENTIALS");
        if (((Customer) baseResponse.getContentData()).getEmail() != null) {
            mParameters.put(CustomerUtils.INTERNAL_AUTO_LOGIN_FLAG, true);
            BamiloApplication.INSTANCE.getCustomerUtils().storeCredentials(mParameters);
            // Save customer
            BamiloApplication.CUSTOMER = ((Customer) baseResponse.getContentData());
        }
    }

    public static Bundle createBundle(String endpoint, ContentValues values) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_END_POINT_KEY, "/" + TargetLink.getIdFromTargetLink(endpoint));
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        return bundle;
    }

}
