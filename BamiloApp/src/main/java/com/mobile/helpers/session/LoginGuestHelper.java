package com.mobile.helpers.session;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.app.BamiloApplication;
import com.mobile.helpers.NextStepStruct;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.checkout.CheckoutStepLogin;
import com.mobile.newFramework.objects.customer.Customer;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.pojo.RestConstants;
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
 * Helper used to login a guest user
 * @author sergiopereira
 */
public class LoginGuestHelper extends SuperBaseHelper {
    
    boolean saveCredentials = true;

    @Override
    public EventType getEventType() {
        return EventType.GUEST_LOGIN_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.ACTION_TASK;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.signUpCustomer);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);
        // Save credentials
        if (saveCredentials) {
            mParameters.put(CustomerUtils.INTERNAL_AUTO_LOGIN_FLAG, true);
            mParameters.put(CustomerUtils.INTERNAL_PASSWORD_VALUE, "");
            mParameters.put(CustomerUtils.INTERNAL_EMAIL_VALUE, "");
            mParameters.put(CustomerUtils.INTERNAL_SIGN_UP_FLAG, true);
            mParameters.put(CustomerUtils.INTERNAL_FACEBOOK_FLAG, false);
            BamiloApplication.INSTANCE.getCustomerUtils().storeCredentials(mParameters);
            Print.i("GET CUSTOMER CREDENTIALS: " + BamiloApplication.INSTANCE.getCustomerUtils().getCredentials());
        }
        // Save customer
        CheckoutStepLogin loginCustomer = (CheckoutStepLogin) baseResponse.getContentData();
        Customer customer = loginCustomer.getCustomer();
        customer.setGuest(true);
        // Save customer
        BamiloApplication.CUSTOMER = customer;
        NextStepStruct nextStepStruct = new NextStepStruct(loginCustomer);
        baseResponse.getMetadata().setData(nextStepStruct);
        // Save new wish list
        WishListCache.set(BamiloApplication.CUSTOMER.getWishListCache());
    }

    public static Bundle createBundle(String email) {
        ContentValues values = new ContentValues();
        values.put(RestConstants.EMAIL, email);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        bundle.putBoolean(CustomerUtils.INTERNAL_AUTO_LOGIN_FLAG, true);
        return bundle;
    }

}
