package com.mobile.helpers;

import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;

import com.mobile.framework.ErrorCode;
import com.mobile.framework.objects.Errors;
import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.utils.JSONConstants;

import de.akquinet.android.androlog.Log;

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

        Log.d(TAG, "checkResponseForStatus : " + eventType);
        try {

            // full object in order
            JSONObject jsonObject = new JSONObject(response);
            Boolean success = jsonObject.optBoolean(JSONConstants.JSON_SUCCESS_TAG, false);

            if (eventType == EventType.GET_GLOBAL_CONFIGURATIONS) {
                success = true;
                return parseResponseBundle(bundle, jsonObject);
            }

            JSONObject metaData;

            if (jsonObject.has(JSONConstants.JSON_METADATA_TAG)) {
                // /**
                // * TODO: Validate if is necessary this step The methods
                // * GetRegions and GetCities receive a the metadata field as
                // * json array
                // */
                metaData = jsonObject.getJSONObject(JSONConstants.JSON_METADATA_TAG);
            } else {
                metaData = jsonObject;
            }

            // removing unnecessary information from bundle
            bundle.remove(Constants.BUNDLE_RESPONSE_KEY);
            JSONObject messagesObject = jsonObject.optJSONObject(JSONConstants.JSON_MESSAGES_TAG);
            if (success) {
                handleSuccess(messagesObject, bundle);
                return parseResponseBundle(bundle, metaData);
            } else {
                handleError(messagesObject, bundle);
                return parseResponseErrorBundle(bundle, metaData);
            }
            
        } catch (JSONException e) {
            e.printStackTrace();
            bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
            return parseResponseErrorBundle(bundle);
        } catch (NullPointerException e) {
            e.printStackTrace();
            bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
            return parseResponseErrorBundle(bundle);
        }
    }

    protected void handleSuccess(JSONObject messagesObject, Bundle bundle) throws JSONException {
        if (messagesObject != null) {
            JSONArray successArray = messagesObject.optJSONArray(RestConstants.JSON_SUCCESS_TAG);
            if (successArray != null && successArray.length() > 0) {
                bundle.putString(Constants.BUNDLE_RESPONSE_SUCCESS_MESSAGE_KEY, successArray.getString(0));
            }
        }
    }

    protected void handleError(JSONObject messagesObject, Bundle bundle) throws JSONException {
        HashMap<String, List<String>> errors = Errors.createErrorMessageMap(messagesObject);
        bundle.putSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY, errors);
        bundle.putSerializable(Constants.BUNDLE_ERROR_KEY, ErrorCode.REQUEST_ERROR);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
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
     * In case there as a valid json response, but that contains an error indication, be it due to
     * wrong parameters or something else this is the method used to parse that error
     * 
     * @return
     */
    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
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

    /**
     * Create a request with parameters.
     * 
     * @param request
     * @param params
     * @return string, the url with parameters
     * @throws NullPointerException
     * @throws UnsupportedOperationException
     * @author sergiopereira
     */
    protected String buildUriWithParameters(String request, Bundle params) {
        // Create builder with the base request
        Builder uriBuilder = Uri.parse(request).buildUpon();
        // Append each parameter
        for (String key : params.keySet())
            uriBuilder.appendQueryParameter(key, params.getString(key));
        // Return the new
        return uriBuilder.build().toString();
    }

    protected boolean getAllResponseObject() {
        return false;
    }
}
