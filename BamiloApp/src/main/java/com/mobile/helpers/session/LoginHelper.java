package com.mobile.helpers.session;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.app.BamiloApplication;
import com.mobile.helpers.NextStepStruct;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.service.objects.checkout.CheckoutStepLogin;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.requests.BaseRequest;
import com.mobile.service.requests.RequestBundle;
import com.mobile.service.rest.interfaces.AigApiInterface;
import com.mobile.service.utils.Constants;
import com.mobile.service.utils.CustomerUtils;
import com.mobile.service.utils.EventTask;
import com.mobile.service.utils.EventType;
import com.mobile.service.utils.cache.WishListCache;
import com.mobile.service.utils.output.Print;

/**
 * Login helper
 */
public class LoginHelper extends SuperBaseHelper {
    
    boolean saveCredentials = true;

    @Override
    public EventType getEventType() {
        return EventType.LOGIN_EVENT;
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
        CheckoutStepLogin loginCustomer = ((CheckoutStepLogin) baseResponse.getMetadata().getData());
        NextStepStruct nextStepStruct = new NextStepStruct(loginCustomer);
        baseResponse.getMetadata().setData(nextStepStruct);

        //TODO move to observable
        // Save customer
        BamiloApplication.CUSTOMER = loginCustomer.getCustomer();
        // Save credentials
        if (saveCredentials) {
            mParameters.put(CustomerUtils.INTERNAL_SIGN_UP_FLAG, false);
            BamiloApplication.INSTANCE.getCustomerUtils().storeCredentials(mParameters);
            Print.i("GET CUSTOMER CREDENTIALS: " + BamiloApplication.INSTANCE.getCustomerUtils().getCredentials());
        }
        // Save new wish list
        WishListCache.set(BamiloApplication.CUSTOMER.getWishListCache());
    }

    public static Bundle createLoginBundle(ContentValues values) {
        // TODO VALIDATE WHAT IS USED FOR INTERNAL_AUTO_LOGIN_FLAG
        values.put(CustomerUtils.INTERNAL_AUTO_LOGIN_FLAG, true);
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        bundle.putBoolean(CustomerUtils.INTERNAL_AUTO_LOGIN_FLAG, true);
        return bundle;
    }

}
