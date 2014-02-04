/**
 * 
 */
package pt.rocket.helpers;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import android.os.Bundle;
import android.util.Log;

/**
 * Helper used to get the shipping methods 
 * @author sergiopereira
 */
public class GetShippingMethodsHelper extends BaseHelper {
    
    private static String TAG = GetShippingMethodsHelper.class.getSimpleName();

    // TODO: Send the respective values
    // shippingMethodForm[shipping_method]
    // shippingMethodForm[pickup_station]
                
    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
     */
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Log.d(TAG, "REQUEST");
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.SHIPPING_METHODS_EVENT.action);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.SHIPPING_METHODS_EVENT);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        return bundle;
    }
   
    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
     */
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.i(TAG, "PARSE BUNDLE");

        try {
        
        // Get shipping methods
        JSONObject formJSON = jsonObject.getJSONObject("shippingMethodForm");
        Log.d(TAG, "FORM JSON: " + formJSON.toString());
        // Get cart
        JSONObject cartJSON = jsonObject.getJSONObject("cart");
        Log.d(TAG, "CAT JSON: " + cartJSON.toString());
        // Get messages
        JSONObject msgJSON = jsonObject.getJSONObject("messages");
        Log.d(TAG, "MSG JSON: " + msgJSON.toString());
        
//        
//        final ArrayList<Form> forms = new ArrayList<Form>();
//        JSONArray dataObject;
//        
//            dataObject = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);
//
//            for (int i = 0; i < dataObject.length(); ++i) {
//                Form form = new Form();
//                JSONObject formObject = dataObject.getJSONObject(i);
//                
//                if (!form.initialize(formObject)) Log.e(TAG, "Error initializing the form using the data");
//                
//                forms.add(form);
//            }
//            // formRegistry.put(action, forms);
//            if (forms.size() > 0) {
//                bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, forms.get(0));
//            }
        } catch (JSONException e) {
            Log.d(TAG, "PARSE EXCEPTION: " , e);
            return parseErrorBundle(bundle);
        }
        Log.i(TAG, "PARSE JSON: SUCCESS");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.SHIPPING_METHODS_EVENT);
        return bundle;
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        android.util.Log.d(TAG, "PARSE ERROR BUNDLE");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.SHIPPING_METHODS_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        android.util.Log.d(TAG, "PARSE RESPONSE ERROR BUNDLE");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.SHIPPING_METHODS_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
}
