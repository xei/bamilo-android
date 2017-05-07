package com.mobile.helpers.session;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.app.BamiloApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.customer.Customer;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.CustomerUtils;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.deeplink.TargetLink;

/**
 * Example helper
 * 
 * @author Manuel Silva
 * 
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
        mParameters.put(CustomerUtils.INTERNAL_AUTO_LOGIN_FLAG, true);
        BamiloApplication.INSTANCE.getCustomerUtils().storeCredentials(mParameters);
        // Save customer
        BamiloApplication.CUSTOMER = ((Customer) baseResponse.getContentData());
    }

    public static Bundle createBundle(String endpoint, ContentValues values) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_END_POINT_KEY, "/" + TargetLink.getIdFromTargetLink(endpoint));
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        return bundle;
    }

}
