/**
 * 
 */
package pt.rocket.helpers.session;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.forms.Form;
import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import pt.rocket.helpers.BaseHelper;
import pt.rocket.helpers.HelperPriorityConfiguration;
import pt.rocket.app.JumiaApplication;
import android.os.Bundle;
import android.util.Log;

/**
 * 
 * @author sergiopereira
 *
 */
public class GetSignupFormHelper extends BaseHelper {
    
    private static String TAG = GetSignupFormHelper.class.getSimpleName();
    
    private static final EventType type = EventType.GET_SIGNUP_FORM_EVENT;
    
    private static final EventType fallback = EventType.GET_SIGNUP_FORM_FALLBACK_EVENT;

    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
     */
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Log.d(TAG, "REQUEST");
        String url = JumiaApplication.INSTANCE.getFormDataRegistry().get(fallback.action).getUrl();
        try {
            url = JumiaApplication.INSTANCE.getFormDataRegistry().get(type.action).getUrl();
        } catch (NullPointerException e) {
            Log.w(TAG, "GET SIGN UP FORM FROM FALLBACK");
        }
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, url);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
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
