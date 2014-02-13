/**
 * 
 */
package pt.rocket.helpers.checkout;

import org.json.JSONObject;

import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import pt.rocket.helpers.BaseHelper;
import pt.rocket.helpers.HelperPriorityConfiguration;
import android.os.Bundle;
import android.util.Log;

/**
 * Helper used to set the shipping address 
 * @author sergiopereira
 */
public class CheckoutFinishHelper extends BaseHelper {
    
    private static String TAG = CheckoutFinishHelper.class.getSimpleName();
    
    private static final EventType type = EventType.CHECKOUT_FINISH_EVENT;
            
    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
     */
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Log.d(TAG, "REQUEST");
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, type.action);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, type);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        return bundle;
    }
   
    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
     */
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.d(TAG, "PARSE BUNDLE: " + jsonObject.toString());
        // TODO: Parse the response
        
        // Get order number
        String mOrderNumber = jsonObject.optString("order_nr");
        // Get first name
        String mFirstName = jsonObject.optString("customer_first_name");
        // Get last name
        String mLastName = jsonObject.optString("customer_last_name");
        
        // This step only occurs if the response returned on step 6 has a key "payment".
        // Depending on the selected payment method, client will be asked to provide payment details or redirected to the payment provider's external page to provide payment details.
        // TODO: Filter response by method
        // Cash On Delivery -   If the payment method selected was "Cash On Delivery" this step not applicable.
        // Paga             -   The follow response tell us that is need made a auto submit form for action
        // WebPAY           -   In this case the form has a property named "target" that indicates the result of the form's submit has to be displayed in an iframe, whose NAME is "target" property's value
        // GlobalPay        -   Auto-submit-external
        // Wallety          -   Submit-external
        // AdyenPayment     -   Page
        
        bundle.putString(Constants.BUNDLE_RESPONSE_KEY, mOrderNumber);
        
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
