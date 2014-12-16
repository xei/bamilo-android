/**
 * 
 */
package pt.rocket.helpers.session;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.app.JumiaApplication;
import pt.rocket.forms.Form;
import pt.rocket.forms.FormData;
import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import pt.rocket.helpers.BaseHelper;
import pt.rocket.helpers.HelperPriorityConfiguration;
import android.os.Bundle;
import de.akquinet.android.androlog.Log;

/**
 * Example helper
 * 
 * @author Guilherme Silva
 * @modified sergiopereira
 */
public class GetForgotPasswordFormHelper extends BaseHelper {

    private static String TAG = GetForgotPasswordFormHelper.class.getSimpleName();
    
    private static final EventType EVENT_TYPE = EventType.GET_FORGET_PASSWORD_FORM_EVENT;
    
    private static final EventType FALL_BACK_EVENT_TYPE = EventType.GET_FORGET_PASSWORD_FORM_FALLBACK_EVENT;

    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
     */
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Log.d(TAG, "ON REQUEST");
        
        // Fall back
        String url = FALL_BACK_EVENT_TYPE.action;
        try {
            // Get form
            FormData formData = JumiaApplication.INSTANCE.getFormDataRegistry().get(EVENT_TYPE.action);
            url = formData.getUrl();
        } catch (NullPointerException e) {
            Log.w(TAG, "FORM DATA IS NULL THEN FORGET_PASSWORD_FORM FALLBACK", e);
        }
        
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, url);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
     */
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.d(TAG, "ON PARSE RESPONSE");

        final ArrayList<Form> forms = new ArrayList<Form>();
        JSONArray dataObject;
        try {
            dataObject = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);

            for (int i = 0; i < dataObject.length(); ++i) {
                Form form = new Form();
                JSONObject formObject = dataObject.getJSONObject(i);
                if (!form.initialize(formObject)) {
                    Log.e(TAG, "Error initializing the form using the data");
                }
                forms.add(form);
            }
            // formRegistry.put(action, forms);
            if (forms.size() > 0) {
                bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, forms.get(0));
            }
            bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_FORGET_PASSWORD_FORM_EVENT);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return bundle;
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "ON PARSE ERROR");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        Log.d(TAG, "ON PARSE RESPONSE ERROR");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

}
