/**
 * 
 */
package pt.rocket.helpers.account;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.app.JumiaApplication;
import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.objects.CustomerNewsletterSubscription;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import pt.rocket.helpers.BaseHelper;
import pt.rocket.helpers.HelperPriorityConfiguration;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;

/**
 * Helper used to create an address 
 * @author sergiopereira
 */
public class SubscribeNewslettersHelper extends BaseHelper {
    
    private static String TAG = SubscribeNewslettersHelper.class.getSimpleName();
    
    private static final EventType type = EventType.SUBSCRIBE_NEWSLETTERS_EVENT;
    
    public static final String FORM_CONTENT_VALUES = "form_content_values";

    private Parcelable contentValues;
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
     */
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Log.d(TAG, "REQUEST");
        Bundle bundle = new Bundle();
        contentValues = args.getParcelable(FORM_CONTENT_VALUES);
        bundle.putString(Constants.BUNDLE_URL_KEY, type.action);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, type);
        bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, contentValues);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        return bundle;
    }
   
    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
     */
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.d(TAG, "PARSE BUNDLE: " + jsonObject);
        try {
            ArrayList<CustomerNewsletterSubscription> subscriptions = new ArrayList<CustomerNewsletterSubscription>();
            // Get subscribed newsletters
            JSONArray jsonArray = jsonObject.optJSONArray(RestConstants.JSON_SUBSCRIBED_CATEGORIES_TAG);
            if(jsonArray != null && jsonArray.length() > 0) {
                for (int i = 0; i < jsonArray.length(); i++) {
                     JSONObject object = jsonArray.getJSONObject(i);
                    CustomerNewsletterSubscription newsletter = new CustomerNewsletterSubscription();
                    newsletter.initialize(object);
                    subscriptions.add(newsletter);
                }
            }
            // Save the newsletter subscriptions
            if(JumiaApplication.CUSTOMER != null)
                JumiaApplication.CUSTOMER.setNewsletterSubscriptions(subscriptions);
        } catch (JSONException e) {
            Log.w(TAG, "ON PARSING NEWSLETTER SUBSCRIPTIONS", e);
            return parseErrorBundle(bundle);
        }
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, type);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        android.util.Log.d(TAG, "PARSE ERROR BUNDLE");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, type);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        android.util.Log.d(TAG, "PARSE RESPONSE BUNDLE");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, type);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
}
