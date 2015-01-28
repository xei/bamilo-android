/**
 * @author Manuel Silva
 * 
 */
package pt.rocket.helpers.configs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.framework.Darwin;
import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.interfaces.IMetaData;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import pt.rocket.helpers.BaseHelper;
import pt.rocket.helpers.HelperPriorityConfiguration;
import pt.rocket.view.R;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import de.akquinet.android.androlog.Log;

/**
 * Get Countries Configurations helper 
 * metadata: { 
 *      currency_iso: "XOF", 
 *      currency_symbol: "FCFA",
 *      no_decimals: 0, 
 *      thousands_sep: ",", 
 *      decimals_sep: ".", 
 *      languages: [ 
 *          { 
 *              lang_code: "fr_CI",
 *              lang_name: "Français", 
 *              lang_default: true 
 *             } 
 *       ], 
 *       ga_id: "UA-41452964-1", 
 *       phone_number: "20006161",
 *       cs_email: "" 
 * }
 * 
 * @author Manuel Silva
 * 
 */
public class GetCountryConfigsHelper extends BaseHelper {

    private static String TAG = GetCountryConfigsHelper.class.getSimpleName();
    
    private static final EventType EVENT_TYPE = EventType.GET_COUNTRY_CONFIGURATIONS;
    
    private final String POSITION_LEFT = "1";
    
    //private final String POSITION_RIGHT = "2";

    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.GET_COUNTRY_CONFIGURATIONS.action);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
        bundle.putBoolean(IMetaData.MD_IGNORE_CACHE, true);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_COUNTRY_CONFIGURATIONS);
        return bundle;
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        
        String currency_iso = null;
        String currency_symbol = null;
        String currency_position = null;
        int no_decimals = 0;
        String thousands_sep = null;
        String decimals_sep = null;
        String lang_code = null;
        String lang_name = null;
        String ga_id = null;
        String phone_number = null;
        String cs_email = null;
        boolean rating_enable = true;
        boolean review_enable = true;
        boolean rating_login_required = false;
        boolean review_login_required = false;
        try {
            
            currency_iso = jsonObject.getString(RestConstants.JSON_COUNTRY_CURRENCY_ISO);
            currency_symbol = jsonObject.getString(RestConstants.JSON_COUNTRY_CURRENCY_SYMBOL);
			
			// Fallback for currency
            if(currency_symbol.equalsIgnoreCase("")){
                currency_symbol = currency_iso;
            }
            currency_position = jsonObject.optString(RestConstants.JSON_COUNTRY_CURRENCY_POSITION);
            no_decimals = jsonObject.getInt(RestConstants.JSON_COUNTRY_NO_DECIMALS);
            thousands_sep = jsonObject.getString(RestConstants.JSON_COUNTRY_THOUSANDS_SEP);
            decimals_sep = jsonObject.getString(RestConstants.JSON_COUNTRY_DECIMALS_SEP);
            
            
            JSONArray languages = jsonObject.getJSONArray(RestConstants.JSON_COUNTRY_LANGUAGES);
            for (int i = 0; i < languages.length(); i++) {
                if(languages.getJSONObject(i).getBoolean(RestConstants.JSON_COUNTRY_LANG_DEFAULT)){
                    lang_code = languages.getJSONObject(i).getString(RestConstants.JSON_COUNTRY_LANG_CODE);
                    lang_name = languages.getJSONObject(i).getString(RestConstants.JSON_COUNTRY_LANG_NAME);
                    break;
                }
                
            }
            
            ga_id = jsonObject.getString(RestConstants.JSON_COUNTRY_GA_ID);
            Log.i(TAG, "COUNTRY GA ID:" + ga_id);
            phone_number = jsonObject.getString(RestConstants.JSON_CALL_PHONE_TAG);
            Log.i(TAG, "COUNTRY PHONE:" + phone_number);
            cs_email = jsonObject.getString(RestConstants.JSON_COUNTRY_CS_EMAIL);
            
            JSONObject ratingObject = jsonObject.optJSONObject(RestConstants.JSON_RATING_TAG);
            if(ratingObject != null){
                rating_enable = ratingObject.optBoolean(RestConstants.JSON_IS_ENABLE_TAG, true);
                rating_login_required = ratingObject.optBoolean(RestConstants.JSON_REQUIRED_LOGIN_TAG, false);
            }
            
            JSONObject reviewObject = jsonObject.optJSONObject(RestConstants.JSON_REVIEW_TAG);
            if(reviewObject != null){
                review_enable = reviewObject.optBoolean(RestConstants.JSON_IS_ENABLE_TAG, true);
                review_login_required = reviewObject.optBoolean(RestConstants.JSON_REQUIRED_LOGIN_TAG, false);
            }
                
        } catch (JSONException e) {
            e.printStackTrace();
            bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        }
        
        SharedPreferences sharedPrefs = JumiaApplication.INSTANCE.getApplicationContext().getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        Editor mEditor = sharedPrefs.edit();
        mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_ISO, currency_iso);
        if(currency_position != null && currency_position.equalsIgnoreCase(POSITION_LEFT)){
            mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_SYMBOL, "%s "+currency_symbol);
            //#RTL
            if(JumiaApplication.INSTANCE.getResources().getBoolean(R.bool.is_bamilo_specific) &&
                    android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB){
                mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_SYMBOL, currency_symbol+" %s");
            }
        } else {
            mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_SYMBOL, currency_symbol+" %s");
        }
        mEditor.putInt(Darwin.KEY_SELECTED_COUNTRY_NO_DECIMALS, no_decimals);
        mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_THOUSANDS_SEP, thousands_sep);
        mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_DECIMALS_SEP, decimals_sep);
        mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_LANG_CODE, lang_code);
        mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_LANG_NAME, lang_name);
        mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_GA_ID, ga_id);
        mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_PHONE_NUMBER, phone_number);
        mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_CS_EMAIL, cs_email);
        
        mEditor.putBoolean(Darwin.KEY_SELECTED_RATING_ENABLE, true);
        mEditor.putBoolean(Darwin.KEY_SELECTED_RATING_REQUIRED_LOGIN, rating_login_required);
        mEditor.putBoolean(Darwin.KEY_SELECTED_REVIEW_ENABLE, true);
        mEditor.putBoolean(Darwin.KEY_SELECTED_REVIEW_REQUIRED_LOGIN, review_login_required);
        
        mEditor.putBoolean(ConstantsSharedPrefs.KEY_COUNTRY_CONFIGS_AVAILABLE, true);
        mEditor.commit();
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_COUNTRY_CONFIGURATIONS);

        return bundle;
    }

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "parseErrorBundle GetCountriesConfigsHelper");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_COUNTRY_CONFIGURATIONS);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_COUNTRY_CONFIGURATIONS);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
    
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
        return parseResponseErrorBundle(bundle);
    }
}
