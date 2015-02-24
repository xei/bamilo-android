package com.mobile.preferences;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.mobile.app.JumiaApplication;
import com.mobile.constants.ConstantsSharedPrefs;
import com.mobile.framework.Darwin;
import com.mobile.framework.rest.RestConstants;
import com.mobile.view.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class used to handle contry configs
 * 
 * @author ricardosoares
 */
public class CountryConfigs {

    private final String POSITION_LEFT = "1";

    private String currency_iso;
    private String currency_symbol;
    private String currency_position;
    private int no_decimals;
    private String thousands_sep;
    private String decimals_sep;
    private String lang_code;
    private String lang_name;
    private String ga_id;
    private String phone_number;
    private String cs_email;
    boolean rating_enable;
    boolean review_enable;
    boolean rating_login_required;
    boolean review_login_required;
    boolean facebook_is_available;

    public CountryConfigs() {
        currency_iso = null;
        currency_symbol = null;
        currency_position = null;
        no_decimals = 0;
        thousands_sep = null;
        decimals_sep = null;
        lang_code = null;
        lang_name = null;
        ga_id = null;
        phone_number = null;
        cs_email = null;
        rating_enable = true;
        review_enable = true;
        rating_login_required = false;
        review_login_required = false;
        facebook_is_available = false;
    }

    /**
     * Initialize object with an json object
     * 
     * @param jsonObject
     * @author ricardosoares
     */
    public CountryConfigs(JSONObject jsonObject) throws JSONException {
        this();
        currency_iso = jsonObject.getString(RestConstants.JSON_COUNTRY_CURRENCY_ISO);
        currency_symbol = jsonObject.getString(RestConstants.JSON_COUNTRY_CURRENCY_SYMBOL);

        // Fallback for currency
        if (currency_symbol.equalsIgnoreCase("")) {
            currency_symbol = currency_iso;
        }
        currency_position = jsonObject.optString(RestConstants.JSON_COUNTRY_CURRENCY_POSITION);
        no_decimals = jsonObject.getInt(RestConstants.JSON_COUNTRY_NO_DECIMALS);
        thousands_sep = jsonObject.getString(RestConstants.JSON_COUNTRY_THOUSANDS_SEP);
        decimals_sep = jsonObject.getString(RestConstants.JSON_COUNTRY_DECIMALS_SEP);
        facebook_is_available = jsonObject.getBoolean(RestConstants.JSON_FACEBOOK_IS_AVAILABLE);

        JSONArray languages = jsonObject.getJSONArray(RestConstants.JSON_COUNTRY_LANGUAGES);
        for (int i = 0; i < languages.length(); i++) {
            if (languages.getJSONObject(i).getBoolean(RestConstants.JSON_COUNTRY_LANG_DEFAULT)) {
                lang_code = languages.getJSONObject(i).getString(
                        RestConstants.JSON_COUNTRY_LANG_CODE);
                lang_name = languages.getJSONObject(i).getString(
                        RestConstants.JSON_COUNTRY_LANG_NAME);
                break;
            }

        }

        ga_id = jsonObject.getString(RestConstants.JSON_COUNTRY_GA_ID);

        phone_number = jsonObject.getString(RestConstants.JSON_CALL_PHONE_TAG);

        cs_email = jsonObject.getString(RestConstants.JSON_COUNTRY_CS_EMAIL);

        JSONObject ratingObject = jsonObject.optJSONObject(RestConstants.JSON_RATING_TAG);
        if (ratingObject != null) {
            rating_enable = ratingObject.optBoolean(RestConstants.JSON_IS_ENABLE_TAG, true);
            rating_login_required = ratingObject.optBoolean(RestConstants.JSON_REQUIRED_LOGIN_TAG,
                    false);
        }

        JSONObject reviewObject = jsonObject.optJSONObject(RestConstants.JSON_REVIEW_TAG);
        if (reviewObject != null) {
            review_enable = reviewObject.optBoolean(RestConstants.JSON_IS_ENABLE_TAG, true);
            review_login_required = reviewObject.optBoolean(RestConstants.JSON_REQUIRED_LOGIN_TAG,
                    false);
        }

    }

    /**
     * Write object variables to preferences
     * 
     * @param sharedPrefs
     * @author ricardosoares
     */
    public void writePreferences(SharedPreferences sharedPrefs) {

        Editor mEditor = sharedPrefs.edit();
        mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_ISO, currency_iso);
        if (currency_position != null && currency_position.equalsIgnoreCase(POSITION_LEFT)) {
            mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_SYMBOL, "%s " + currency_symbol);
            // #RTL
            if (JumiaApplication.INSTANCE.getResources().getBoolean(R.bool.is_bamilo_specific) &&
                    android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.HONEYCOMB) {
                mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_SYMBOL, currency_symbol
                        + " %s");
            }
        } else {
            mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_SYMBOL, currency_symbol + " %s");
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
        mEditor.putBoolean(Darwin.KEY_SELECTED_FACEBOOK_IS_AVAILABLE, facebook_is_available);

        mEditor.putBoolean(ConstantsSharedPrefs.KEY_COUNTRY_CONFIGS_AVAILABLE, true);
        mEditor.commit();
    }
}
