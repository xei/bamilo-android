package com.mobile.helpers.account;

import com.mobile.app.JumiaApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.customer.Customer;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.EventType;

/**
 * Example helper
 */
public class GetCustomerHelper extends SuperBaseHelper {
    
    public static String TAG = GetCustomerHelper.class.getSimpleName();

    @Override
    public EventType getEventType() {
        return EventType.GET_CUSTOMER;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getCustomerDetails);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);

        JumiaApplication.CUSTOMER = (Customer) baseResponse.getMetadata().getData();
    }

}