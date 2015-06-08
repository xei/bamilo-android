package com.mobile.helpers.session;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.customer.Customer;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.CustomerUtils;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;

import java.io.Serializable;
import java.util.Map;

/**
 * Example helper
 * 
 * @author Manuel Silva
 * 
 */
public class RegisterHelper extends SuperBaseHelper {
    
    private static String TAG = RegisterHelper.class.getSimpleName();
    
    public static final String REGISTER_CONTENT_VALUES = "contentValues";

    private ContentValues mContentValues;

    @Override
    public EventType getEventType() {
        return EventType.REGISTER_ACCOUNT_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.SMALL_TASK;
    }

    @Override
    protected RequestBundle createRequest(Bundle args) {
        mContentValues = args.getParcelable(Constants.BUNDLE_DATA_KEY );
        return super.createRequest(args);
    }

    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        return SuperBaseHelper.convertContentValuesToMap(mContentValues);
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
//        new RegisterCustomer(requestBundle, this).execute();
        new BaseRequest(requestBundle, this).execute(AigApiInterface.registerCustomer);
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST COMPLETE: " + baseResponse.hadSuccess());
        Print.i(TAG, "SAVE CUSTOMER CREDENTIALS");
        mContentValues.put(CustomerUtils.INTERNAL_AUTO_LOGIN_FLAG, true);
        JumiaApplication.INSTANCE.getCustomerUtils().storeCredentials(mContentValues);
        Print.i(TAG, "HAS CUSTOMER CREDENTIALS: " + JumiaApplication.INSTANCE.getCustomerUtils().hasCredentials());
        // Save customer
        JumiaApplication.CUSTOMER = ((Customer) baseResponse.getMetadata().getData());
        // Create bundle
        Bundle bundle = generateSuccessBundle(baseResponse);
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, JumiaApplication.CUSTOMER);
        mRequester.onRequestComplete(bundle);
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST ERROR: " + baseResponse.getMessage());
        Bundle bundle = generateErrorBundle(baseResponse);
        bundle.putSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY, (Serializable) baseResponse.getErrorMessages());
        mRequester.onRequestError(bundle);
    }


//
//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Bundle bundle = new Bundle();
//        contentValues = args.getParcelable(REGISTER_CONTENT_VALUES);
//        contentValues.put(CustomerUtils.INTERNAL_AUTOLOGIN_FLAG, true);
//        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.REGISTER_ACCOUNT_EVENT.action);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.REGISTER_ACCOUNT_EVENT);
//        bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, contentValues);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        if (saveCredentials) {
//            Log.i(TAG, "code1 saving credentials : ");
//            contentValues.remove("Alice_Module_Mobapi_Form_Ext1m3_Customer_RegistrationForm[newsletter_categories_subscribed][]");
//            JumiaApplication.INSTANCE.getCustomerUtils().storeCredentials(contentValues);
//            Log.i(TAG, "code1 hasCredentials : "+JumiaApplication.INSTANCE.getCustomerUtils().hasCredentials());
//        }
//        try {
//            if (jsonObject.has(RestConstants.JSON_USER_TAG)) {
//                jsonObject = jsonObject.getJSONObject(RestConstants.JSON_USER_TAG);
//            } else if (jsonObject.has(RestConstants.JSON_DATA_TAG)) {
//                jsonObject = jsonObject.getJSONObject(RestConstants.JSON_DATA_TAG);
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        JumiaApplication.CUSTOMER = new Customer(jsonObject);
//        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, JumiaApplication.CUSTOMER);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.REGISTER_ACCOUNT_EVENT);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "parseErrorBundle GetRegisterHelper");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.REGISTER_ACCOUNT_EVENT);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.REGISTER_ACCOUNT_EVENT);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
//        return parseResponseErrorBundle(bundle);
//    }
}
