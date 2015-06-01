/**
 * 
 */
package com.mobile.helpers.session;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.framework.enums.RequestType;
import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.CustomerUtils;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.Utils;
import com.mobile.helpers.BaseHelper;
import com.mobile.helpers.HelperPriorityConfiguration;
import com.mobile.newFramework.objects.user.Customer;
import com.mobile.utils.CheckoutStepManager;

import org.json.JSONException;
import org.json.JSONObject;

import de.akquinet.android.androlog.Log;

/**
 * 
 * @author sergiopereira
 *
 */
public class SetSignupHelper extends BaseHelper {
    
    private static String TAG = SetSignupHelper.class.getSimpleName();

    public static final String FORM_CONTENT_VALUES = "contentValues";
    
    private static final EventType EVENT_TYPE = EventType.SET_SIGNUP_EVENT;

    boolean saveCredentials = true;
    
    ContentValues contentValues;

    /*
     * (non-Javadoc)
     * @see com.mobile.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
     */
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Log.d(TAG, "REQUEST");
        saveCredentials = args.getBoolean(CustomerUtils.INTERNAL_AUTOLOGIN_FLAG);
        contentValues = args.getParcelable(FORM_CONTENT_VALUES);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, EVENT_TYPE.action);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
        bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, contentValues);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        return bundle;
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
     */
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.d(TAG, "PARSE BUNDLE: " + jsonObject);
        if (saveCredentials) {
             contentValues.put(CustomerUtils.INTERNAL_AUTOLOGIN_FLAG, true);
             contentValues.put(CustomerUtils.INTERNAL_PASSWORD_VALUE, "");
             contentValues.put(CustomerUtils.INTERNAL_EMAIL_VALUE, "");
             contentValues.put(CustomerUtils.INTERNAL_SIGNUP_FLAG, true);
            JumiaApplication.INSTANCE.getCustomerUtils().storeCredentials(contentValues);
            Log.d(TAG, "STORE CREDENTIALS: " + contentValues.toString());
        }
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        bundle.putSerializable(Constants.BUNDLE_NEXT_STEP_KEY, CheckoutStepManager.getNextCheckoutFragment(jsonObject));
        if (jsonObject.has(RestConstants.JSON_DATA_TAG)) {
            
            try {
                JSONObject dataObject = jsonObject.getJSONObject(RestConstants.JSON_DATA_TAG);
                if (dataObject.has(RestConstants.JSON_USER_TAG)) {
                    JSONObject customerObject = dataObject.getJSONObject(RestConstants.JSON_USER_TAG);
                    Customer customer = new Customer(customerObject);
                    bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, customer);
  
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        
        }

        return bundle;
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "PARSE ERROR BUNDLE");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        Log.d(TAG, "PARSE ERROR BUNDLE");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
    
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
        return parseResponseErrorBundle(bundle);
    }
    
}
