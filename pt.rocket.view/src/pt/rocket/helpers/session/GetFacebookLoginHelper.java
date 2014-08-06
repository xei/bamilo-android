/**
 * 
 */
package pt.rocket.helpers.session;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.app.JumiaApplication;
import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.objects.Customer;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.CustomerUtils;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import pt.rocket.helpers.BaseHelper;
import pt.rocket.helpers.HelperPriorityConfiguration;
import pt.rocket.utils.CheckoutStepManager;
import android.content.ContentValues;
import android.os.Bundle;

/**
 * Example helper
 * 
 * @author Guilherme Silva
 * 
 */
public class GetFacebookLoginHelper extends BaseHelper {
    private static String TAG = GetLoginFormHelper.class.getSimpleName();

    public static final String LOGIN_CONTENT_VALUES = "contentValues";
    boolean saveCredentials = true;
    ContentValues contentValues;

    @Override
    public Bundle generateRequestBundle(Bundle args) {
        saveCredentials = args.getBoolean(CustomerUtils.INTERNAL_AUTOLOGIN_FLAG);
        contentValues = args.getParcelable(LOGIN_CONTENT_VALUES);
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.FACEBOOK_LOGIN_EVENT.action);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
        bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, contentValues);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.FACEBOOK_LOGIN_EVENT);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        
        JSONObject jsonUser = null;
        try {
            if (jsonObject.has(RestConstants.JSON_USER_TAG)) {
                jsonUser = jsonObject.getJSONObject(RestConstants.JSON_USER_TAG);
                if (saveCredentials) {
                    contentValues.put(CustomerUtils.INTERNAL_PASSWORD_VALUE, jsonUser.getString(RestConstants.JSON_PASSWORD_TAG));
                    contentValues.put(CustomerUtils.INTERNAL_EMAIL_VALUE, jsonUser.getString(RestConstants.JSON_EMAIL_TAG));
                    contentValues.put(CustomerUtils.INTERNAL_FACEBOOK_FLAG, true);
                    JumiaApplication.INSTANCE.getCustomerUtils().storeCredentials(contentValues);
                }
            } else if (jsonObject.has(RestConstants.JSON_DATA_TAG)) {
                jsonUser = jsonObject.getJSONObject(RestConstants.JSON_DATA_TAG);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        JumiaApplication.CUSTOMER = new Customer(jsonUser);
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, JumiaApplication.CUSTOMER);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.FACEBOOK_LOGIN_EVENT);
        bundle.putSerializable(Constants.BUNDLE_NEXT_STEP_KEY, CheckoutStepManager.getNextCheckoutStep(jsonObject));
        return bundle;
    }
    
    
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        android.util.Log.d(TAG, "parseErrorBundle GetFacebookLoginHelper");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.FACEBOOK_LOGIN_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.FACEBOOK_LOGIN_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
}
