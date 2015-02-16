/**
 * 
 */
package com.mobile.helpers.checkout;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;

import com.mobile.forms.Form;
import com.mobile.framework.enums.RequestType;
import com.mobile.framework.interfaces.IMetaData;
import com.mobile.framework.objects.OrderSummary;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.Utils;
import com.mobile.helpers.BaseHelper;
import com.mobile.helpers.HelperPriorityConfiguration;

import de.akquinet.android.androlog.Log;

/**
 * Helper used to get the payment methods
 * 
 * @author sergiopereira
 * @modified Manuel Silva
 */
public class GetPaymentMethodsHelper extends BaseHelper {

    private static String TAG = GetPaymentMethodsHelper.class.getSimpleName();

    private static final EventType EVENT_TYPE = EventType.GET_PAYMENT_METHODS_EVENT;
    
    public static final String NO_PAYMENT = "no_payment";
    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
     */
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Log.d(TAG, "REQUEST");
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, EVENT_TYPE.action);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        bundle.putBoolean(IMetaData.MD_IGNORE_CACHE, true);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
        return bundle;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
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

            OrderSummary orderSummary = new OrderSummary(jsonObject);
            bundle.putParcelable(Constants.BUNDLE_ORDER_SUMMARY_KEY, orderSummary);

            bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, form);

        } catch (JSONException e) {
            Log.d(TAG, "PARSE EXCEPTION: ", e);
            return parseErrorBundle(bundle);
        }
        Log.i(TAG, "PARSE JSON: SUCCESS");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * 
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
     * 
     * @see com.mobile.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        Log.d(TAG, "PARSE RESPONSE BUNDLE");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
    
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
        return parseResponseErrorBundle(bundle);
    }
}
