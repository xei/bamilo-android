package pt.rocket.helpers;

import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.objects.Errors;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.utils.JSONConstants;
import pt.rocket.utils.JumiaApplication;

import android.os.Bundle;
import android.util.Log;

/**
 * Base helper for the test app. The helper is responsible for generating the bundle for the api
 * call and parse the http response
 * 
 * @author Guilherme Silva
 * 
 */
public abstract class BaseHelper {
    private static String TAG = BaseHelper.class.getSimpleName();

    /**
     * Creates the bundle for the request
     * 
     * @return
     */
    public abstract Bundle generateRequestBundle(Bundle bundle);

    /**
     * Checks the response status of the response that came in a bundle in order to evaluate if its
     * a valid response or not
     * 
     * @param bundle
     * @return
     */
    public Bundle checkResponseForStatus(Bundle bundle) {

        String response = bundle.getString(Constants.BUNDLE_RESPONSE_KEY);
        EventType eventType = (EventType) bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY);
        
        long elapsed = System.currentTimeMillis() - JumiaApplication.INSTANCE.timeTrackerMap.get(eventType);
        Log.i("REQUEST", "event type response : "+bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY)+" time spent : "+elapsed);
        String trackValue = bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY) + " : "+elapsed;
        JumiaApplication.INSTANCE.writeToTrackerFile(trackValue);
        Log.d(TAG, "checkResponseForStatus : " + eventType);
        try {// TODO maintain generic information here, no need to pass on the
             // full object in order
            JSONObject jsonObject = new JSONObject(response);

            Boolean success = jsonObject.optBoolean(JSONConstants.JSON_SUCCESS_TAG, false);
            JSONObject metaData;
            if (eventType == EventType.GET_CALL_TO_ORDER_PHONE
                    || eventType == EventType.REVIEW_PRODUCT_EVENT) {
                metaData = jsonObject;
            } else {
                if (jsonObject.has(JSONConstants.JSON_METADATA_TAG)) {
                    metaData = jsonObject.getJSONObject(JSONConstants.JSON_METADATA_TAG);
                } else {
                    metaData = jsonObject;
                }

            }
            // removing unnecessary information from bundle
            bundle.remove(Constants.BUNDLE_RESPONSE_KEY);
            if (success) {
                Log.i(TAG, "code1 success response checkResponseForStatus");
                return parseResponseBundle(bundle, metaData);
            } else {
                JSONObject messagesObject = jsonObject
                        .optJSONObject(JSONConstants.JSON_MESSAGES_TAG);
                HashMap<String, List<String>> errors = Errors
                        .createErrorMessageMap(messagesObject);
                bundle.putSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY, errors);
                bundle.putSerializable(Constants.BUNDLE_ERROR_KEY, ErrorCode.REQUEST_ERROR);
                bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
                Log.i(TAG, "code1 error response checkResponseForStatus : " + errors.toString());
                return parseResponseErrorBundle(bundle);
            }
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            Log.i(TAG, "code1 error response checkResponseForStatus : json error " + eventType);
            bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
            return parseResponseErrorBundle(bundle);
        }
    }

    /**
     * In case there as a valid json response, but that contains an error indication, be it due to
     * wrong parameters or something else this is the method used to parse that error
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
     * Parses the bundle from the error response - This parses errors of generic factors: TIMEOUT,
     * PROTOCOL ERROR, SOCKET ERROR among others
     * 
     * @param bundle
     * @return
     */
    public abstract Bundle parseErrorBundle(Bundle bundle);
}
