package com.mobile.helpers.account;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;

import java.util.Map;

/**
 * Example helper
 */
public class SetChangePasswordHelper extends SuperBaseHelper {

    private static String TAG = SetChangePasswordHelper.class.getSimpleName();

    private ContentValues mContentValues;

    @Override
    public EventType getEventType() {
        return EventType.CHANGE_PASSWORD_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.SMALL_TASK;
    }

    @Override
    protected RequestBundle createRequest(Bundle args) {
        mContentValues = args.getParcelable(Constants.BUNDLE_DATA_KEY);
        return super.createRequest(args);
    }

    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        return CollectionUtils.convertContentValuesToMap(mContentValues);
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
//        new ChangePassword(requestBundle, this).execute();
        new BaseRequest(requestBundle, this).execute(AigApiInterface.changePassword);
    }

    @Override
    public void postSuccess(BaseResponse baseResponse) {
        super.postSuccess(baseResponse);

        //TODO move to observable
        // Save credentials
        Print.i(TAG, "SAVE CUSTOMER CREDENTIALS");
        mContentValues.remove( "Alice_Module_Customer_Model_PasswordForm[password2]" );
        JumiaApplication.INSTANCE.getCustomerUtils().storeCredentials(mContentValues);
        Print.i(TAG, "GET CUSTOMER CREDENTIALS: " + JumiaApplication.INSTANCE.getCustomerUtils().getCredentials());
    }

    public static Bundle createBundle(ContentValues values) {
        Bundle bundle = new Bundle();
        bundle.putParcelable(Constants.BUNDLE_DATA_KEY, values);
        return bundle;
    }
}