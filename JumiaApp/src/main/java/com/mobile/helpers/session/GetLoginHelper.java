package com.mobile.helpers.session;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.CustomerUtils;
import com.mobile.framework.utils.EventTask;
import com.mobile.framework.utils.EventType;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.checkout.CheckoutStepLogin;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.session.LoginCustomer;
import com.mobile.utils.CheckoutStepManager;

import java.io.Serializable;
import java.util.Map;

import com.mobile.framework.output.Print;

/**
 * Example helper
 */
public class GetLoginHelper extends SuperBaseHelper {
    
    private static String TAG = GetLoginHelper.class.getSimpleName();

    public static final String LOGIN_CONTENT_VALUES = "contentValues";

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
        saveCredentials = args.getBoolean(CustomerUtils.INTERNAL_AUTOLOGIN_FLAG);
        mContentValues = args.getParcelable(Constants.BUNDLE_DATA_KEY);
        return super.createRequest(args);
    }

    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        return SuperBaseHelper.convertContentValuesToMap(mContentValues);
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new LoginCustomer(JumiaApplication.INSTANCE.getApplicationContext(), requestBundle, this).execute();
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST COMPLETE: " + baseResponse.hadSuccess());
        // Save credentials
        if (saveCredentials) {
            Print.i(TAG, "SAVE CUSTOMER CREDENTIALS");
            mContentValues.put(CustomerUtils.INTERNAL_FACEBOOK_FLAG, false);
            JumiaApplication.INSTANCE.getCustomerUtils().storeCredentials(mContentValues);
            Print.i(TAG, "GET CUSTOMER CREDENTIALS: " + JumiaApplication.INSTANCE.getCustomerUtils().getCredentials());
        }
        CheckoutStepLogin loginCustomer = ((CheckoutStepLogin) baseResponse.getMetadata().getData());
        // Save customer
        JumiaApplication.CUSTOMER = loginCustomer.getCustomer();
        // Create bundle
        Bundle bundle = generateSuccessBundle(baseResponse);
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, JumiaApplication.CUSTOMER);
        bundle.putSerializable(Constants.BUNDLE_NEXT_STEP_KEY, CheckoutStepManager.getNextFragment(loginCustomer.getNextStep()));
        mRequester.onRequestComplete(bundle);
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST ERROR: " + baseResponse.getMessage());
        Bundle bundle = generateErrorBundle(baseResponse);
        bundle.putSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY, (Serializable) baseResponse.getErrorMessages());
        mRequester.onRequestError(bundle);
    }


//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        saveCredentials = args.getBoolean(CustomerUtils.INTERNAL_AUTOLOGIN_FLAG);
//        contentValues = args.getParcelable(LOGIN_CONTENT_VALUES);
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.LOGIN_EVENT.action);
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
//        bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, contentValues);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.LOGIN_EVENT);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        if (saveCredentials) {
//            Log.i(TAG, "code1 saving credentials : ");
//            contentValues.put(CustomerUtils.INTERNAL_FACEBOOK_FLAG, false);
//            JumiaApplication.INSTANCE.getCustomerUtils().storeCredentials(contentValues);
//            Log.i(TAG, "code1 hasCredentials : "+JumiaApplication.INSTANCE.getCustomerUtils().hasCredentials());
//        }
//
//        JSONObject jsonUser = null;
//        try {
//            if (jsonObject.has(RestConstants.JSON_USER_TAG)) {
//                jsonUser = jsonObject.getJSONObject(RestConstants.JSON_USER_TAG);
//            } else if (jsonObject.has(RestConstants.JSON_DATA_TAG)) {
//                jsonUser = jsonObject.getJSONObject(RestConstants.JSON_DATA_TAG);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        JumiaApplication.CUSTOMER = new Customer(jsonUser);
//        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, JumiaApplication.CUSTOMER );
//        bundle.putSerializable(Constants.BUNDLE_NEXT_STEP_KEY, CheckoutStepManager.getNextCheckoutFragment(jsonObject));
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.LOGIN_EVENT);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "parseErrorBundle GetLoginHelper");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.LOGIN_EVENT);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.LOGIN_EVENT);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
//        return parseResponseErrorBundle(bundle);
//    }
}
