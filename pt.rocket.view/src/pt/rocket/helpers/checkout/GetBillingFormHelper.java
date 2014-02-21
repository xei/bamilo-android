/**
 * 
 */
package pt.rocket.helpers.checkout;

import org.json.JSONObject;

import pt.rocket.forms.Form;
import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.objects.Addresses;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import pt.rocket.helpers.BaseHelper;
import pt.rocket.helpers.HelperPriorityConfiguration;
import android.os.Bundle;
import de.akquinet.android.androlog.Log;


/**
 * Helper used to get all customer addresses 
 * @author sergiopereira
 *
 */
public class GetBillingFormHelper extends BaseHelper {
    
    private static String TAG = GetBillingFormHelper.class.getSimpleName();
    
    private static final EventType type = EventType.GET_BILLING_FORM_EVENT;
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
     */
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Log.d(TAG, "GENERATE BUNDLE");
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, type.action);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, type);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
     */
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        try {
            // Get json object
            JSONObject jsonForm = jsonObject.getJSONObject("billingForm");
            JSONObject jsonList = jsonObject.getJSONObject("customer").getJSONObject("address_list");
            // Create form
            Form form = new Form();
            form.initialize(jsonForm);
            // Create addresses
            Addresses addresses = new Addresses(jsonList);
            // Add to bundle
            bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, addresses);
            bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, form);
            bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, type);
        } catch (Exception e) {
            Log.d(TAG, "PARSE EXCEPTION:", e);
            return parseErrorBundle(bundle);
        }
        Log.i(TAG, "PARSE JSON: SUCCESS");
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
        android.util.Log.d(TAG, "PARSE ERROR RESPONSE");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, type);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

}