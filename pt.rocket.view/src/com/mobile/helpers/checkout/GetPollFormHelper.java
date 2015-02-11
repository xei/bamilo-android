/**
 * 
 */
package com.mobile.helpers.checkout;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobile.app.JumiaApplication;
import com.mobile.forms.Form;
import com.mobile.framework.enums.RequestType;
import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.Utils;
import com.mobile.helpers.BaseHelper;
import com.mobile.helpers.HelperPriorityConfiguration;
import com.mobile.utils.CheckoutStepManager;
import android.os.Bundle;
import de.akquinet.android.androlog.Log;

/**
 * Helper used to get the poll form 
 * @author sergiopereira
 */
public class GetPollFormHelper extends BaseHelper {
    
    private static String TAG = GetPollFormHelper.class.getSimpleName();

    private static final EventType EVENT_TYPE = EventType.GET_POLL_FORM_EVENT;
    private static final EventType fallback_EVENT_TYPE = EventType.GET_POLL_FORM_FALLBACK_EVENT;
    /*
     * (non-Javadoc)
     * @see com.mobile.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
     */
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Log.d(TAG, "REQUEST");
        Bundle bundle = new Bundle();
        String url;
        if(JumiaApplication.INSTANCE.getFormDataRegistry() != null && JumiaApplication.INSTANCE.getFormDataRegistry().get(EVENT_TYPE.action) != null ){
            url = JumiaApplication.INSTANCE.getFormDataRegistry().get(EVENT_TYPE.action).getUrl();    
        } else {
            url = fallback_EVENT_TYPE.action;
        }
        
        bundle.putString(Constants.BUNDLE_URL_KEY, url);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
     */
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.d(TAG, "PARSE BUNDLE");
        final ArrayList<Form> forms = new ArrayList<Form>();
        JSONArray dataObject;
        try {
            
            dataObject = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);

            for (int i = 0; i < dataObject.length(); ++i) {
                Form form = new Form();
                JSONObject formObject = dataObject.getJSONObject(i);
                
                if (!form.initialize(formObject)) Log.e(TAG, "Error initializing the form using the data");
                
                forms.add(form);
            }

            if (forms.size() > 0)  bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, forms.get(0));
            
        } catch (JSONException e) {
            Log.d(TAG, "PARSE JSON", e);
            return parseErrorBundle(bundle);
        }
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        bundle.putSerializable(Constants.BUNDLE_NEXT_STEP_KEY, CheckoutStepManager.getNextCheckoutStep(jsonObject));
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