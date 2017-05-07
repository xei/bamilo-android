package com.mobile.helpers.account;

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
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;
import com.mobile.utils.deeplink.TargetLink;

/**
 * Helper used to set User information
 */
public class SetUserDataHelper extends SuperBaseHelper {

    @Override
    public EventType getEventType() {
        return EventType.EDIT_USER_DATA_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.ACTION_TASK;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new BaseRequest(requestBundle, this).execute(AigApiInterface.setUserData);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);
        BamiloApplication.CUSTOMER = (Customer) baseResponse.getContentData();
    }

    public static Bundle createBundle(String endpoint, ContentValues values) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_END_POINT_KEY, "/" + TargetLink.getIdFromTargetLink(endpoint));
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        return bundle;
    }
}