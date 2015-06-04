package com.mobile.helpers.session;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.CustomerUtils;
import com.mobile.framework.utils.EventTask;
import com.mobile.framework.utils.EventType;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.user.Customer;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.session.LoginCustomer;

import java.util.Map;

import com.mobile.framework.output.Print;

/**
 * Example helper
 * 
 * @author Guilherme Silva
 * 
 */
public class GetFacebookLoginHelper extends SuperBaseHelper {
    
    private static String TAG = GetLoginFormHelper.class.getSimpleName();

    public static final String LOGIN_CONTENT_VALUES = "contentValues";

    boolean saveCredentials = true;

    private ContentValues mContentValues;

    @Override
    public EventType getEventType() {
        return EventType.FACEBOOK_LOGIN_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.NORMAL_TASK;
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
        // Save customer
        Customer customer = (Customer) baseResponse.getMetadata().getData();
        // Save credentials
        if (saveCredentials) {
            Print.i(TAG, "SAVE CUSTOMER CREDENTIALS");
            mContentValues.put(CustomerUtils.INTERNAL_PASSWORD_VALUE, customer.getPassword());
            mContentValues.put(CustomerUtils.INTERNAL_EMAIL_VALUE, customer.getEmail());
            mContentValues.put(CustomerUtils.INTERNAL_FACEBOOK_FLAG, true);
            JumiaApplication.INSTANCE.getCustomerUtils().storeCredentials(mContentValues);
            Print.i(TAG, "GET CUSTOMER CREDENTIALS: " + JumiaApplication.INSTANCE.getCustomerUtils().getCredentials());
        }
        // Save customer
        JumiaApplication.CUSTOMER = customer;
        // Create bundle
        Bundle bundle = generateSuccessBundle(baseResponse);
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, JumiaApplication.CUSTOMER);
        // TODO: NEXT STEP
        // bundle.putSerializable(Constants.BUNDLE_NEXT_STEP_KEY, CheckoutStepManager.getNextCheckoutStep(jsonObject));
        //
        mRequester.onRequestComplete(bundle);
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST ERROR: " + baseResponse.getMessage());
        Bundle bundle = generateErrorBundle(baseResponse);
        mRequester.onRequestError(bundle);
    }







//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        saveCredentials = args.getBoolean(CustomerUtils.INTERNAL_AUTOLOGIN_FLAG);
//        contentValues = args.getParcelable(LOGIN_CONTENT_VALUES);
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.FACEBOOK_LOGIN_EVENT.action);
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
//        bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, contentValues);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.FACEBOOK_LOGIN_EVENT);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//
//        JSONObject jsonUser = null;
//        try {
//            if (jsonObject.has(RestConstants.JSON_USER_TAG)) {
//                jsonUser = jsonObject.getJSONObject(RestConstants.JSON_USER_TAG);
//                if (saveCredentials) {
//                    contentValues.put(CustomerUtils.INTERNAL_PASSWORD_VALUE, jsonUser.getString(RestConstants.JSON_PASSWORD_TAG));
//                    contentValues.put(CustomerUtils.INTERNAL_EMAIL_VALUE, jsonUser.getString(RestConstants.JSON_EMAIL_TAG));
//                    contentValues.put(CustomerUtils.INTERNAL_FACEBOOK_FLAG, true);
//                    JumiaApplication.INSTANCE.getCustomerUtils().storeCredentials(contentValues);
//                }
//            } else if (jsonObject.has(RestConstants.JSON_DATA_TAG)) {
//                jsonUser = jsonObject.getJSONObject(RestConstants.JSON_DATA_TAG);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        JumiaApplication.CUSTOMER = new Customer(jsonUser);
//        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, JumiaApplication.CUSTOMER);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.FACEBOOK_LOGIN_EVENT);
//        bundle.putSerializable(Constants.BUNDLE_NEXT_STEP_KEY, CheckoutStepManager.getNextCheckoutStep(jsonObject));
//        return bundle;
//    }


//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "parseErrorBundle GetFacebookLoginHelper");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.FACEBOOK_LOGIN_EVENT);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.FACEBOOK_LOGIN_EVENT);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
//        return parseResponseErrorBundle(bundle);
//    }
}
