package com.bamilo.android.appmodule.bamiloapp.helpers.session;

import android.content.ContentValues;
import android.os.Bundle;

import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.helpers.NextStepStruct;
import com.bamilo.android.appmodule.bamiloapp.helpers.SuperBaseHelper;
import com.bamilo.android.framework.service.objects.checkout.CheckoutStepLogin;
import com.bamilo.android.framework.service.objects.customer.Customer;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.requests.BaseRequest;
import com.bamilo.android.framework.service.requests.RequestBundle;
import com.bamilo.android.framework.service.rest.interfaces.AigApiInterface;
import com.bamilo.android.framework.service.utils.Constants;
import com.bamilo.android.framework.service.utils.CustomerUtils;
import com.bamilo.android.framework.service.utils.EventTask;
import com.bamilo.android.framework.service.utils.EventType;
import com.bamilo.android.framework.service.utils.cache.WishListCache;

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
            BamiloApplication.INSTANCE.getCustomerUtils().storeCredentials(mParameters);
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
