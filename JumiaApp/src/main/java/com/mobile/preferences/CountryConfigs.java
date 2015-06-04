//package com.mobile.preferences;
//
//import android.content.Context;
//import android.content.SharedPreferences;
//import android.content.SharedPreferences.Editor;
//import android.os.Build;
//import android.text.TextUtils;
//
//import com.mobile.constants.ConstantsSharedPrefs;
//import com.mobile.framework.Darwin;
//import com.mobile.framework.rest.RestConstants;
//import com.mobile.framework.utils.Constants;
//import com.mobile.view.R;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import com.mobile.framework.output.Log;
//
///**
// * Class used to handle country configs.
// *
// * @author ricardosoares
// * @modified sergiopereira
// * TODO extends from countryConfigs on Framework
// */
//public class CountryConfigs {
//
//    private static final String TAG = CountryConfigs.class.getSimpleName();
//
//    private static final String CURRENCY_LEFT_POSITION = "1";
//
//    // private static final String CURRENCY_RIGHT_POSITION = "0";
//
//    private static final String STRING_START_PLACEHOLDER = "%s ";
//
//    private static final String STRING_END_PLACEHOLDER = " %s";
//
//    private String mCurrencyIso;
//    private String mCurrencySymbol;
//    private String mCurrencyPosition;
//    private int mNoDecimals;
//    private String mThousandsSep;
//    private String mDecimalsSep;
//    private String mLangCode;
//    private String mLangName;
//    private String mGaId;
//    private String mGTMId;
//    private String mPhoneNumber;
//    private String mCsEmail;
//    private boolean isRatingEnable;
//    private boolean isReviewEnable;
//    private boolean isRatingLoginRequired;
//    private boolean isReviewLoginRequired;
//    private boolean isFacebookAvailable;
//
//    /**
//     * Empty constructor
//     */
//    public CountryConfigs() {
//        mCurrencyIso = null;
//        mCurrencySymbol = null;
//        mCurrencyPosition = null;
//        mNoDecimals = 0;
//        mThousandsSep = null;
//        mDecimalsSep = null;
//        mLangCode = null;
//        mLangName = null;
//        mGaId = null;
//        mGTMId = null;
//        mPhoneNumber = null;
//        mCsEmail = null;
//        isRatingEnable = true;
//        isReviewEnable = true;
//        isRatingLoginRequired = false;
//        isReviewLoginRequired = false;
//        isFacebookAvailable = false;
//    }
//
//    /**
//     * Initialize object with an json object
//     *
//     * @param jsonObject The json response
//     * @author ricardosoares
//     */
//    public CountryConfigs(JSONObject jsonObject) throws JSONException {
//        this();
//        // Get currency info
//        mCurrencyIso = jsonObject.getString(RestConstants.JSON_COUNTRY_CURRENCY_ISO);
//        mCurrencySymbol = jsonObject.getString(RestConstants.JSON_COUNTRY_CURRENCY_SYMBOL);
//        mCurrencyPosition = jsonObject.optString(RestConstants.JSON_COUNTRY_CURRENCY_POSITION);
//        // Fallback for currency
//        if (TextUtils.isEmpty(mCurrencySymbol)) {
//            mCurrencySymbol = mCurrencyIso;
//        }
//        // Get price info
//        mNoDecimals = jsonObject.getInt(RestConstants.JSON_COUNTRY_NO_DECIMALS);
//        mThousandsSep = jsonObject.getString(RestConstants.JSON_COUNTRY_THOUSANDS_SEP);
//        mDecimalsSep = jsonObject.getString(RestConstants.JSON_COUNTRY_DECIMALS_SEP);
//        // Get languages
//        JSONArray languages = jsonObject.getJSONArray(RestConstants.JSON_COUNTRY_LANGUAGES);
//        for (int i = 0; i < languages.length(); i++) {
//            if (languages.getJSONObject(i).getBoolean(RestConstants.JSON_COUNTRY_LANG_DEFAULT)) {
//                mLangCode = languages.getJSONObject(i).getString(RestConstants.JSON_CODE_TAG);
//                mLangName = languages.getJSONObject(i).getString(RestConstants.JSON_NAME_TAG);
//                break;
//            }
//        }
//        // Get GA id
//        mGaId = jsonObject.getString(RestConstants.JSON_COUNTRY_GA_ID);
//        // Get GTM id
//        mGTMId = jsonObject.optString(RestConstants.JSON_COUNTRY_GTM_ID);
//        // Get phone number
//        mPhoneNumber = jsonObject.getString(RestConstants.JSON_CALL_PHONE_TAG);
//        // Get email
//        mCsEmail = jsonObject.getString(RestConstants.JSON_COUNTRY_CS_EMAIL);
//        // Get facebook flag
//        isFacebookAvailable = jsonObject.getBoolean(RestConstants.JSON_FACEBOOK_IS_AVAILABLE);
//        // Get rating info
//        JSONObject ratingObject = jsonObject.optJSONObject(RestConstants.JSON_RATING_TAG);
//        if (ratingObject != null) {
//            isRatingEnable = ratingObject.optBoolean(RestConstants.JSON_IS_ENABLE_TAG, true);
//            isRatingLoginRequired = ratingObject.optBoolean(RestConstants.JSON_REQUIRED_LOGIN_TAG);
//        }
//        // Get review info
//        JSONObject reviewObject = jsonObject.optJSONObject(RestConstants.JSON_REVIEW_TAG);
//        if (reviewObject != null) {
//            isReviewEnable = reviewObject.optBoolean(RestConstants.JSON_IS_ENABLE_TAG, true);
//            isReviewLoginRequired = reviewObject.optBoolean(RestConstants.JSON_REQUIRED_LOGIN_TAG);
//        }
//    }
//
//    /**
//     * Write object variables to preferences
//     *
//     * @param context The application context
//     * @author ricardosoares
//     */
//    public void writePreferences(Context context) {
//        // Get shared prefs
//        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
//        Editor mEditor = sharedPrefs.edit();
//        // Currency info
//        mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_ISO, mCurrencyIso);
//        if (!TextUtils.isEmpty(mCurrencyPosition) && mCurrencyPosition.equals(CURRENCY_LEFT_POSITION)) {
//            mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_SYMBOL, STRING_START_PLACEHOLDER + mCurrencySymbol);
//            // #RTL
//            if (context.getResources().getBoolean(R.bool.is_bamilo_specific) && Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
//                mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_SYMBOL, mCurrencySymbol + STRING_END_PLACEHOLDER);
//            }
//        } else {
//            mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_CURRENCY_SYMBOL, mCurrencySymbol + STRING_END_PLACEHOLDER);
//        }
//        // Price info
//        mEditor.putInt(Darwin.KEY_SELECTED_COUNTRY_NO_DECIMALS, mNoDecimals);
//        mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_THOUSANDS_SEP, mThousandsSep);
//        mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_DECIMALS_SEP, mDecimalsSep);
//        // Languages
//        mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_LANG_CODE, mLangCode);
//        mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_LANG_NAME, mLangName);
//        // GA
//        mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_GA_ID, mGaId);
//        // GTM
//        mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_GTM_ID, mGTMId);
//        // Phone number
//        mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_PHONE_NUMBER, mPhoneNumber);
//        // Email
//        mEditor.putString(Darwin.KEY_SELECTED_COUNTRY_CS_EMAIL, mCsEmail);
//        // Facebook
//        mEditor.putBoolean(Darwin.KEY_SELECTED_FACEBOOK_IS_AVAILABLE, isFacebookAvailable);
//        // Rating
//        mEditor.putBoolean(Darwin.KEY_SELECTED_RATING_ENABLE, isRatingEnable);
//        mEditor.putBoolean(Darwin.KEY_SELECTED_RATING_REQUIRED_LOGIN, isRatingLoginRequired);
//        // Review
//        mEditor.putBoolean(Darwin.KEY_SELECTED_REVIEW_ENABLE, isReviewEnable);
//        mEditor.putBoolean(Darwin.KEY_SELECTED_REVIEW_REQUIRED_LOGIN, isReviewLoginRequired);
//        // Flag
//        mEditor.putBoolean(Darwin.KEY_COUNTRY_CONFIGS_AVAILABLE, true);
//        mEditor.apply();
//    }
//
//    /**
//     * Function used to get the shop country code.
//     * @param context The application context
//     */
//    public static String getCountryPhoneNumber(Context context) {
//        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
//        String mPhone2Call = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_PHONE_NUMBER, null);
//        Log.i(TAG, "SHOP COUNTRY PHONE NUMBER: " + mPhone2Call);
//        return mPhone2Call;
//    }
//
//    /**
//     * Get the value for Facebook.
//     * @param context The application context
//     * @return true or false
//     */
//    public static boolean checkCountryRequirements(Context context) {
//        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
//        return sharedPrefs.contains(Darwin.KEY_SELECTED_FACEBOOK_IS_AVAILABLE);
//    }
//
//}
