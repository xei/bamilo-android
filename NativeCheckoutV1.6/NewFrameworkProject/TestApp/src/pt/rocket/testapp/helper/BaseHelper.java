package pt.rocket.testapp.helper;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.utils.Constants;
import pt.rocket.testapp.objects.JSONConstants;

import android.os.Bundle;
import android.util.Log;

/**
 * Base helper for the test app. The helper is responsible for generating the
 * bundle for the api call and parse the http response
 * 
 * @author Guilherme Silva
 * 
 */
public abstract class BaseHelper {

    /**
     * Creates the bundle for the request
     * 
     * @return
     */
    public abstract Bundle generateRequestBundle();

    /**
     * Checks the response status of the response that came in a bundle in order
     * to evaluate if its a valid response or not
     * 
     * @param bundle
     * @return
     */
    public Bundle checkResponseForStatus(Bundle bundle) {
    	Log.d("TRACK", "checkResponseForStatus");
        String response = bundle.getString(Constants.BUNDLE_RESPONSE_KEY);
        try {// TODO maintain generic information here, no need to pass on the
             // full object in order
            JSONObject jsonObject = new JSONObject(response);

            Boolean success = jsonObject.optBoolean(JSONConstants.JSON_SUCCESS_TAG, false);

            JSONObject metaData = jsonObject.getJSONObject(JSONConstants.JSON_METADATA_TAG);

            // removing unnecessary information from bundle
            bundle.remove(Constants.BUNDLE_RESPONSE_KEY);
            if (success) {
                return parseResponseBundle(bundle, metaData);
            } else {
                return parseResponseErrorBundle(bundle);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return parseResponseErrorBundle(bundle);
        }
    }

    /**
     * In case there as a valid json response, but that contains an error
     * indication, be it due to wrong parameters or something else this is the
     * method used to parse that error
     * 
     * @return
     */
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        return bundle;
    }

    /**
     * Parses a correct json response
     * 
     * @param bundle
     * @return
     */
    public abstract Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject);

    /**
     * Parses the bundle from the error response - This parses errors of generic
     * factors: TIMEOUT, PROTOCOL ERROR, SOCKET ERROR among others
     * 
     * @param bundle
     * @return
     */
    public abstract Bundle parseErrorBundle(Bundle bundle);
}
