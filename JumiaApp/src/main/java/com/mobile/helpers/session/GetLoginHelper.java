package com.mobile.helpers.session;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.checkout.CheckoutStepLogin;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.CustomerUtils;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.cache.WishListCache;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.CheckoutStepManager;

import java.util.Map;

/**
 * Example helper
 */
public class GetLoginHelper extends SuperBaseHelper {
    
    private static String TAG = GetLoginHelper.class.getSimpleName();

    boolean saveCredentials = true;

    private ContentValues mContentValues;

    @Override
    public EventType getEventType() {
        return EventType.LOGIN_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.SMALL_TASK;
    }

    @Override
    protected RequestBundle createRequest(Bundle args) {
        saveCredentials = args.getBoolean(CustomerUtils.INTERNAL_AUTO_LOGIN_FLAG);
        mContentValues = args.getParcelable(Constants.BUNDLE_DATA_KEY);
        return super.createRequest(args);
    }

    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        return CollectionUtils.convertContentValuesToMap(mContentValues);
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.loginCustomer);
    }

    @Override
    public void createSuccessBundleParams(BaseResponse baseResponse, Bundle bundle) {
        super.createSuccessBundleParams(baseResponse, bundle);
        CheckoutStepLogin loginCustomer = ((CheckoutStepLogin) baseResponse.getMetadata().getData());
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY,  loginCustomer.getCustomer());
        bundle.putSerializable(Constants.BUNDLE_NEXT_STEP_KEY, CheckoutStepManager.getNextFragment(loginCustomer.getNextStep()));
        // Save customer
        JumiaApplication.CUSTOMER = loginCustomer.getCustomer();
        // Save credentials
        if (saveCredentials) {
            Print.i(TAG, "SAVE CUSTOMER CREDENTIALS");
            mContentValues.put(CustomerUtils.INTERNAL_SIGN_UP_FLAG, false);
            mContentValues.put(CustomerUtils.INTERNAL_FACEBOOK_FLAG, false);
            JumiaApplication.INSTANCE.getCustomerUtils().storeCredentials(mContentValues);
            Print.i(TAG, "GET CUSTOMER CREDENTIALS: " + JumiaApplication.INSTANCE.getCustomerUtils().getCredentials());
        }
        // Save new wish list
        WishListCache.set(JumiaApplication.CUSTOMER.getWishListCache());
    }
}
