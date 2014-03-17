/**
 * 
 */
package pt.rocket.helpers.checkout;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.app.JumiaApplication;
import pt.rocket.forms.Form;
import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.interfaces.IMetaData;
import pt.rocket.framework.objects.OrderSummary;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import pt.rocket.helpers.BaseHelper;
import pt.rocket.helpers.HelperPriorityConfiguration;
import android.os.Bundle;
import android.util.Log;

/**
 * Helper used to get the payment methods
 * 
 * @author sergiopereira
 * @modified Manuel Silva
 */
public class GetPaymentMethodsHelper extends BaseHelper {

    private static String TAG = GetPaymentMethodsHelper.class.getSimpleName();

    private static final EventType type = EventType.GET_PAYMENT_METHODS_EVENT;
    
    public static final String NO_PAYMENT = "no_payment";
    /*
     * (non-Javadoc)
     * 
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
        bundle.putBoolean(IMetaData.MD_IGNORE_CACHE, true);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        return bundle;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
     */
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.i(TAG, "PARSE BUNDLE");

        try {

            // Get shipping methods
            JSONObject formJSON = jsonObject.getJSONObject("paymentMethodForm");
            Log.d(TAG, "FORM JSON: " + formJSON.toString());
            Form form = new Form();
            if (!form.initialize(formJSON))
                Log.e(TAG, "Error initializing the form using the data");

            // Get cart
            JSONObject cartJSON = jsonObject.optJSONObject("cart");
            if (cartJSON != null)
                Log.d(TAG, "CAT JSON: " + cartJSON.toString());
            // ShoppingCart cart = new
            // ShoppingCart(JumiaApplication.INSTANCE.getItemSimpleDataRegistry());
            // cart.initialize(cartJSON);

            // XXX
            OrderSummary orderSummary = new OrderSummary(jsonObject,
                    JumiaApplication.INSTANCE.getItemSimpleDataRegistry());
            bundle.putParcelable(Constants.BUNDLE_ORDER_SUMMARY_KEY, orderSummary);

            bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, form);

        } catch (JSONException e) {
            Log.d(TAG, "PARSE EXCEPTION: ", e);
            return parseErrorBundle(bundle);
        }
        Log.i(TAG, "PARSE JSON: SUCCESS");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, type);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * 
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
     * 
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
