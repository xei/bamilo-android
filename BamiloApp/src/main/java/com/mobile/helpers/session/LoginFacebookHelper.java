package com.mobile.helpers.session;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.app.BamiloApplication;
import com.mobile.helpers.NextStepStruct;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.checkout.CheckoutStepLogin;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.CustomerUtils;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.cache.WishListCache;
import com.mobile.newFramework.utils.output.Print;

/**
 * Facebook Login helper
 */
public class LoginFacebookHelper extends SuperBaseHelper {
    
    boolean saveCredentials = true;

    @Override
    public EventType getEventType() {
        return EventType.FACEBOOK_LOGIN_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.ACTION_TASK;
    }

    @Override
    protected RequestBundle createRequest(Bundle args) {
        saveCredentials = args.getBoolean(CustomerUtils.INTERNAL_AUTO_LOGIN_FLAG);
        return super.createRequest(args);
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.loginCustomer);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);
        // Save customer
        CheckoutStepLogin loginCustomer = (CheckoutStepLogin) baseResponse.getContentData();
        NextStepStruct nextStepStruct = new NextStepStruct(loginCustomer);
        baseResponse.getMetadata().setData(nextStepStruct);

        // Save customer
        BamiloApplication.CUSTOMER = loginCustomer.getCustomer();
        // Save credentials
        if (saveCredentials) {
            mParameters.put(CustomerUtils.INTERNAL_PASSWORD_VALUE, "");
            mParameters.put(CustomerUtils.INTERNAL_EMAIL_VALUE, BamiloApplication.CUSTOMER.getEmail());
            mParameters.put(CustomerUtils.INTERNAL_FACEBOOK_FLAG, true);
            mParameters.put(CustomerUtils.INTERNAL_SIGN_UP_FLAG, false);
            BamiloApplication.INSTANCE.getCustomerUtils().storeCredentials(mParameters);
            Print.i("GET CUSTOMER CREDENTIALS: " + BamiloApplication.INSTANCE.getCustomerUtils().getCredentials());
        }
        // Save new wish list
        WishListCache.set(BamiloApplication.CUSTOMER.getWishListCache());
    }

    public static Bundle createBundle(ContentValues values, boolean saveCredentials) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        bundle.putBoolean(CustomerUtils.INTERNAL_AUTO_LOGIN_FLAG, saveCredentials);
        return bundle;
    }
}
