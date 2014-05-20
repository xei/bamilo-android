package pt.rocket.framework.testproject.helper;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.testproject.objects.XMLObject;
import pt.rocket.framework.testproject.utils.XMLUtils;
import pt.rocket.framework.utils.Constants;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;

import com.rocket.framework.testshell.test.R;

/**
 * Base helper for the test app. The helper is responsible for generating the
 * bundle for the api call and parse the http response
 * 
 * @author Guilherme Silva
 * 
 */
public abstract class BaseHelper {
    private static String TAG = BaseHelper.class.getSimpleName();
    protected Context mContext;
    protected String failedParameterMessage;
    public static String KEY_COUNTRY = "key_country";
    public static String KEY_COUNTRY_TAG = "key_country_tag";
    
    /**
     * Countries base url
     */
    
    public static String BASE_URL_STAGING_NG = "https://alice-staging.jumia.com.ng/mobapi/v1.3";
    public static String BASE_URL_STAGING_KE = "https://alice-staging.jumia.co.ke/mobapi/v1.3";
    public static String BASE_URL_STAGING_MA = "https://alice-staging.jumia.ma/mobapi/v1.3";
    public static String BASE_URL_STAGING_EG = "https://alice-staging.jumia.com.eg/mobapi/v1.3";
    public static String BASE_URL_STAGING_CI = "https://alice-staging.jumia.ci/mobapi/v1.3";
    public static String BASE_URL_STAGING_UG = "https://alice-staging.jumia.ug/mobapi/v1.3";
    
    public static String BASE_URL_NG = "https://www.jumia.com.ng/mobapi/v1.1";
    public static String BASE_URL_KE = "https://www.jumia.co.ke/mobapi/v1.1";
    public static String BASE_URL_MA = "https://www.jumia.ma/mobapi/v1.1";
    public static String BASE_URL_EG = "https://www.jumia.com.eg/mobapi/v1.1";
    public static String BASE_URL_CI = "https://www.jumia.ci/mobapi/v1.1";//BASE_URL_STAGING_CI;//
    public static String BASE_URL_UG = "https://www.jumia.ug/mobapi/v1.1";

    
    /**
     * Creates the bundle for the request
     * 
     * @return
     */
    public abstract Bundle generateRequestBundle(Bundle args);

    /**
     * Checks the response status of the response that came in a bundle in order
     * to evaluate if its a valid response or not
     * 
     * @param bundle
     * @return
     */
    @SuppressWarnings("rawtypes")
    public Bundle checkResponseForStatus(Bundle bundle, Context mContext) {
        String response = bundle.getString(Constants.BUNDLE_RESPONSE_KEY);
        this.mContext = mContext;
        try {
            JSONObject jsonObject = new JSONObject(response);
            boolean validation = true;
//            Log.i(TAG, "code1: "+bundle.getBoolean(Constants.BUNDLE_METADATA_REQUIRED_KEY)+" _ "+bundle.getBoolean(Constants.BUNDLE_GENERAL_RULES_FALSE_KEY));
            try {
            	XMLObject generalRules;
            	if(!bundle.getBoolean(Constants.BUNDLE_METADATA_REQUIRED_KEY)){
            		
            		Log.i(TAG, "code1nometadata");
            		generalRules = XMLUtils.xmlParser(mContext, R.xml.general_rules_metadata_not_required);	
            	} else if(!bundle.getBoolean(Constants.BUNDLE_GENERAL_RULES_FALSE_KEY)){
            		
            		Log.i(TAG, "code1 with false");
            		generalRules = XMLUtils.xmlParser(mContext, R.xml.general_rules_false);	
            	} else if(!bundle.getBoolean(Constants.BUNDLE_GENERAL_RULES_GET_COUNTRIES_KEY)){
            		
            		Log.i(TAG, "code1 countries");
            		generalRules = XMLUtils.xmlParser(mContext, R.xml.general_rules_get_countries);	
            	} else {
            		
            		generalRules = XMLUtils.xmlParser(mContext, R.xml.general_rules);
            		Log.i(TAG, "code1 with metadata");	
            	}
                
                validation = XMLUtils.jsonObjectAssertion(jsonObject , generalRules);
                bundle.putBoolean(Constants.BUNDLE_JSON_VALIDATION_KEY, validation);
                Log.i(TAG," received validation "+validation);
            } catch (Exception e) {
                e.printStackTrace();
            }
            if(bundle.getBoolean(Constants.BUNDLE_METADATA_REQUIRED_KEY) && bundle.getBoolean(Constants.BUNDLE_GENERAL_RULES_GET_COUNTRIES_KEY)){
	            if (validation) {
	                jsonObject = jsonObject.getJSONObject(XMLReadingConfiguration.XML_METADATA_CONTAINER_TAG);
	                bundle.putSerializable(Constants.BUNDLE_ERROR_KEY, ErrorCode.NO_ERROR);
	                return parseResponseBundle(bundle, jsonObject);
	
	            } else {
	                Log.i(TAG," Failed validation ");
	                Log.i(TAG,  " failedParameterMessage "+XMLUtils.getMessage());
	                bundle.putString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY, XMLUtils.getMessage());
	                bundle.putSerializable(Constants.BUNDLE_ERROR_KEY, ErrorCode.ERROR_PARSING_SERVER_DATA);
	                return parseResponseErrorBundle(bundle);
	            }
            }else{
            	 Log.i(TAG," RETURNING JSON VALIDATION "+validation);
            	if (validation) {
            		return parseResponseBundle(bundle, jsonObject);
            	}else{
            		bundle.putString(Constants.BUNDLE_WRONG_PARAMETER_MESSAGE_KEY, XMLUtils.getMessage());
	                bundle.putSerializable(Constants.BUNDLE_ERROR_KEY, ErrorCode.ERROR_PARSING_SERVER_DATA);
	                return parseResponseErrorBundle(bundle);
            	}
            	
            	 
            }
        } catch (JSONException e) {
        	XMLUtils.setMessage("Response empty or server error[not a JSONObject]");
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
    public abstract Bundle parseResponseErrorBundle(Bundle bundle);

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
