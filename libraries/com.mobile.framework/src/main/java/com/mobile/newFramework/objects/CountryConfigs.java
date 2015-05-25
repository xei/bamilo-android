package com.mobile.newFramework.objects;

import android.content.Context;
import android.content.SharedPreferences;

import com.mobile.framework.Darwin;
import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import de.akquinet.android.androlog.Log;

/**
 * Class used to handle country configs.
 *
 * @author ricardosoares
 * @modified sergiopereira
 */
public class CountryConfigs implements com.mobile.newFramework.objects.IJSONSerializable {

    private static final String TAG = CountryConfigs.class.getSimpleName();

    private static final String CURRENCY_LEFT_POSITION = "1";

    // private static final String CURRENCY_RIGHT_POSITION = "0";

    private static final String STRING_START_PLACEHOLDER = "%s ";

    private static final String STRING_END_PLACEHOLDER = " %s";

    private String mCurrencyIso;
    private String mCurrencySymbol;
    private String mCurrencyPosition;
    private int mNoDecimals;
    private String mThousandsSep;
    private String mDecimalsSep;
    private String mLangCode;
    private String mLangName;
    private String mGaId;
    private String mGTMId;
    private String mPhoneNumber;
    private String mCsEmail;
    private boolean isRatingEnable;
    private boolean isReviewEnable;
    private boolean isRatingLoginRequired;
    private boolean isReviewLoginRequired;
    private boolean isFacebookAvailable;

    /**
     * Empty constructor
     */
    public CountryConfigs() {
        mCurrencyIso = null;
        mCurrencySymbol = null;
        mCurrencyPosition = null;
        mNoDecimals = 0;
        mThousandsSep = null;
        mDecimalsSep = null;
        mLangCode = null;
        mLangName = null;
        mGaId = null;
        mGTMId = null;
        mPhoneNumber = null;
        mCsEmail = null;
        isRatingEnable = true;
        isReviewEnable = true;
        isRatingLoginRequired = false;
        isReviewLoginRequired = false;
        isFacebookAvailable = false;
    }

    /**
     * Initialize object with an json object
     *
     * @param jsonObject The json response
     * @author ricardosoares
     */
    public CountryConfigs(JSONObject jsonObject) throws JSONException {
        this();
        initialize(jsonObject);
    }

    @Override
    public String toString() {
        return "####################" +
                "\ncurrency_iso: " + mCurrencyIso +
                "\ncurrency_symbol: " + mCurrencySymbol +
                "\ncurrency_position: " + mCurrencyPosition +
                "\nno_decimals: " + mNoDecimals +
                "\nthousands_sep: " + mThousandsSep +
                "\ndecimals_sep: " + mDecimalsSep +
                "\nlanguages_code: " + mLangCode +
                "\nlanguages_name: " + mLangName +
                "\ngtm_android: " + mGTMId +
                "\nga_android_id: " + mGaId +
                "\nphone_number: " + mPhoneNumber +
                "\ncs_email: " + mCsEmail +
                "\nfacebook_is_available: " + isFacebookAvailable +
                "\nrating: " + isRatingEnable +
                "\nrating_login: " + isRatingLoginRequired +
                "\nreview: " + isReviewEnable +
                "\nreview_login: " + isReviewLoginRequired
                ;
    }

    /**
     * Function used to get the shop country code.
     *
     * @param context The application context
     */
    public static String getCountryPhoneNumber(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String mPhone2Call = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_PHONE_NUMBER, null);
        Log.i(TAG, "SHOP COUNTRY PHONE NUMBER: " + mPhone2Call);
        return mPhone2Call;
    }

    /**
     * Get the value for Facebook.
     *
     * @param context The application context
     * @return true or false
     */
    public static boolean checkCountryRequirements(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPrefs.contains(Darwin.KEY_SELECTED_FACEBOOK_IS_AVAILABLE);
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        // Get currency info
        mCurrencyIso = jsonObject.getString(RestConstants.JSON_COUNTRY_CURRENCY_ISO);
        mCurrencySymbol = jsonObject.getString(RestConstants.JSON_COUNTRY_CURRENCY_SYMBOL);
        mCurrencyPosition = jsonObject.optString(RestConstants.JSON_COUNTRY_CURRENCY_POSITION);
        // Fallback for currency
//        if (TextUtils.isEmpty(mCurrencySymbol)) {
        if (mCurrencySymbol.equals("")) {
            mCurrencySymbol = mCurrencyIso;
        }
        // Get price info
        mNoDecimals = jsonObject.getInt(RestConstants.JSON_COUNTRY_NO_DECIMALS);
        mThousandsSep = jsonObject.getString(RestConstants.JSON_COUNTRY_THOUSANDS_SEP);
        mDecimalsSep = jsonObject.getString(RestConstants.JSON_COUNTRY_DECIMALS_SEP);
        // Get languages
        JSONArray languages = jsonObject.getJSONArray(RestConstants.JSON_COUNTRY_LANGUAGES);
        for (int i = 0; i < languages.length(); i++) {
            if (languages.getJSONObject(i).getBoolean(RestConstants.JSON_COUNTRY_LANG_DEFAULT)) {
                mLangCode = languages.getJSONObject(i).getString(RestConstants.JSON_CODE_TAG);
                mLangName = languages.getJSONObject(i).getString(RestConstants.JSON_NAME_TAG);
                break;
            }
        }
        // Get GA id
        mGaId = jsonObject.getString(RestConstants.JSON_COUNTRY_GA_ID);
        // Get GTM id
        mGTMId = jsonObject.optString(RestConstants.JSON_COUNTRY_GTM_ID);
        // Get phone number
        mPhoneNumber = jsonObject.getString(RestConstants.JSON_CALL_PHONE_TAG);
        // Get email
        mCsEmail = jsonObject.getString(RestConstants.JSON_COUNTRY_CS_EMAIL);
        // Get facebook flag
        isFacebookAvailable = jsonObject.getBoolean(RestConstants.JSON_FACEBOOK_IS_AVAILABLE);
        // Get rating info
        JSONObject ratingObject = jsonObject.optJSONObject(RestConstants.JSON_RATING_TAG);
        if (ratingObject != null) {
            isRatingEnable = ratingObject.optBoolean(RestConstants.JSON_IS_ENABLE_TAG, true);
            isRatingLoginRequired = ratingObject.optBoolean(RestConstants.JSON_REQUIRED_LOGIN_TAG);
        }
        // Get review info
        JSONObject reviewObject = jsonObject.optJSONObject(RestConstants.JSON_REVIEW_TAG);
        if (reviewObject != null) {
            isReviewEnable = reviewObject.optBoolean(RestConstants.JSON_IS_ENABLE_TAG, true);
            isReviewLoginRequired = reviewObject.optBoolean(RestConstants.JSON_REQUIRED_LOGIN_TAG);
        }
        return true;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return RequiredJson.METADATA;
    }
}
