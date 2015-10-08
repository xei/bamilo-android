package com.mobile.helpers.session;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.customer.Customer;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.CustomerUtils;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;

import java.util.Map;

/**
 * Example helper
 * 
 * @author Manuel Silva
 * 
 */
public class RegisterHelper extends SuperBaseHelper {
    
    private static String TAG = RegisterHelper.class.getSimpleName();

    private ContentValues mContentValues;

    @Override
    public EventType getEventType() {
        return EventType.REGISTER_ACCOUNT_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.SMALL_TASK;
    }

    @Override
    protected RequestBundle createRequest(Bundle args) {
        mContentValues = args.getParcelable(Constants.BUNDLE_DATA_KEY );
        return super.createRequest(args);
    }

    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        return CollectionUtils.convertContentValuesToMap(mContentValues);
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.registerCustomer);
    }

    @Override
    public void createSuccessBundleParams(BaseResponse baseResponse, Bundle bundle) {
        super.createSuccessBundleParams(baseResponse, bundle);
        Print.i(TAG, "SAVE CUSTOMER CREDENTIALS");
        mContentValues.put(CustomerUtils.INTERNAL_AUTO_LOGIN_FLAG, true);
        JumiaApplication.INSTANCE.getCustomerUtils().storeCredentials(mContentValues);
        Print.i(TAG, "HAS CUSTOMER CREDENTIALS: " + JumiaApplication.INSTANCE.getCustomerUtils().hasCredentials());
        // Save customer
        JumiaApplication.CUSTOMER = ((Customer) baseResponse.getMetadata().getData());
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, JumiaApplication.CUSTOMER);
    }

}
