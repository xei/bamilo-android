package com.mobile.newFramework.objects.configs;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.statics.MobileAbout;
import com.mobile.newFramework.objects.statics.TargetHelper;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;


/**
 * Class used to handle country configs.
 *
 * @author ricardosoares
 * @modified sergiopereira
 */
public class CountryConfigs implements IJSONSerializable, Parcelable {

    //private static final String TAG = CountryConfigs.class.getSimpleName();

    public static final String CURRENCY_LEFT_POSITION = "1";

    // private static final String CURRENCY_RIGHT_POSITION = "0";

    public static final String STRING_START_PLACEHOLDER = "%s ";

    public static final String STRING_END_PLACEHOLDER = " %s";

    private String mCurrencyIso;
    private String mCurrencySymbol;
    private String mCurrencyPosition;
    private int mNoDecimals;
    private String mThousandsSep;
    private String mDecimalsSep;
    private String mGaId;
    private String mGTMId;
    private String mPhoneNumber;
    private String mCsEmail;
    private boolean isRatingEnable;
    private boolean isReviewEnable;
    private boolean isRatingLoginRequired;
    private boolean isReviewLoginRequired;
    private boolean isFacebookAvailable;
    private Languages languages;
    private List<TargetHelper> mobileAbout;
    private boolean hasCartPopup;
    private boolean mIsRichRelevanceEnabled;
    private String mSuggesterProvider;
    private String mApplicationId;
    private String mSuggesterApiKey;
    private String mNamespacePrefix;
    private boolean mUseAlgolia;

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
        mGaId = null;
        mGTMId = null;
        mPhoneNumber = null;
        mCsEmail = null;
        isRatingEnable = true;
        isReviewEnable = true;
        isRatingLoginRequired = false;
        isReviewLoginRequired = false;
        isFacebookAvailable = false;
        hasCartPopup = false;
        mIsRichRelevanceEnabled = false;
        mSuggesterProvider = null;
        mApplicationId = null;
        mSuggesterApiKey = null;
        mNamespacePrefix = null;
        mUseAlgolia = false;
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
                "\ngtm_android: " + mGTMId +
                "\nga_android_id: " + mGaId +
                "\nphone_number: " + mPhoneNumber +
                "\ncs_email: " + mCsEmail +
                "\nfacebook_is_available: " + isFacebookAvailable +
                "\nrating: " + isRatingEnable +
                "\nrating_login: " + isRatingLoginRequired +
                "\nreview: " + isReviewEnable +
                "\nhas_cart_popup: " + hasCartPopup +
                "\nrich_relevance_enabled: " + mIsRichRelevanceEnabled +
                "\nsuggester_provider: " + mSuggesterProvider +
                "\napplication_id: " + mApplicationId +
                "\nsuggester_api_key: " + mSuggesterApiKey +
                "\nnamespace_prefix: " + mNamespacePrefix
                ;
    }



    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        // Get currency info
        mCurrencyIso = jsonObject.getString(RestConstants.CURRENCY_ISO);
        mCurrencySymbol = jsonObject.getString(RestConstants.CURRENCY_SYMBOL);
        mCurrencyPosition = jsonObject.optString(RestConstants.CURRENCY_POSITION);
        // Fallback for currency
//        if (TextUtils.isEmpty(mCurrencySymbol)) {
        if (mCurrencySymbol.equals("")) {
            mCurrencySymbol = mCurrencyIso;
        }
        // Get price info
        mNoDecimals = jsonObject.getInt(RestConstants.NO_DECIMALS);
        mThousandsSep = jsonObject.getString(RestConstants.THOUSANDS_SEP);
        mDecimalsSep = jsonObject.getString(RestConstants.DECIMALS_SEP);
        // Get GA id
        mGaId = jsonObject.getString(RestConstants.GA_ANDROID_ID);
        // Get GTM id
        mGTMId = jsonObject.optString(RestConstants.GTM_ANDROID);
        // Get phone number
        mPhoneNumber = jsonObject.getString(RestConstants.PHONE_NUMBER);
        // Get email
        mCsEmail = jsonObject.getString(RestConstants.CS_EMAIL);
        // Get facebook flag
        isFacebookAvailable = jsonObject.getBoolean(RestConstants.FACEBOOK_IS_AVAILABLE);
        // Get rating info
        JSONObject ratingObject = jsonObject.optJSONObject(RestConstants.RATING);
        if (ratingObject != null) {
            isRatingEnable = ratingObject.optBoolean(RestConstants.IS_ENABLE, true);
            isRatingLoginRequired = ratingObject.optBoolean(RestConstants.REQUIRED_LOGIN);
        }
        // Get review info
        JSONObject reviewObject = jsonObject.optJSONObject(RestConstants.REVIEW);
        if (reviewObject != null) {
            isReviewEnable = reviewObject.optBoolean(RestConstants.IS_ENABLE, true);
            isReviewLoginRequired = reviewObject.optBoolean(RestConstants.REQUIRED_LOGIN);
        }
        languages = new Languages(jsonObject);

        try {
            mobileAbout = new MobileAbout(jsonObject);
        } catch (JSONException ex) {
        }

        hasCartPopup = jsonObject.optBoolean(RestConstants.HAS_CART_POPUP);
        // Get if Rich Relevance is enabled
        mIsRichRelevanceEnabled = jsonObject.optBoolean(RestConstants.RICH_RELEVANCE_ENABLED);
        //Algolia/Api configurations
        mSuggesterProvider = jsonObject.optString(RestConstants.SUGGESTER_PROVIDER);
        if(TextUtils.equalsIgnoreCase(mSuggesterProvider, "algolia")){
            mUseAlgolia = true;
        }
        JSONObject jsonAlgolia = jsonObject.optJSONObject(RestConstants.ALGOLIA);
        if(jsonAlgolia != null){
            mApplicationId = jsonAlgolia.optString(RestConstants.APPLICATION_ID);
            mSuggesterApiKey = jsonAlgolia.optString(RestConstants.SUGGESTER_API_KEY);
            mNamespacePrefix = jsonAlgolia.optString(RestConstants.NAMESPACE_PREFIX);
        }
        return true;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.METADATA;
    }


    public String getCurrencyIso() {
        return mCurrencyIso;
    }

    public String getCurrencySymbol() {
        return mCurrencySymbol;
    }

    public String getCurrencyPosition() {
        return mCurrencyPosition;
    }

    public int getNoDecimals() {
        return mNoDecimals;
    }

    public String getThousandsSep() {
        return mThousandsSep;
    }

    public String getDecimalsSep() {
        return mDecimalsSep;
    }

    public boolean hasCartPopup() {
        return hasCartPopup;
    }

    public boolean isRichRelevanceEnabled() {
        return mIsRichRelevanceEnabled;
    }

    public String getGaId() {
        return mGaId;
    }

    public String getGTMId() {
        return mGTMId;
    }

    public String getPhoneNumber() {
        return mPhoneNumber;
    }

    public String getCsEmail() {
        return mCsEmail;
    }

    public boolean isRatingEnable() {
        return isRatingEnable;
    }

    public boolean isReviewEnable() {
        return isReviewEnable;
    }

    public boolean isRatingLoginRequired() {
        return isRatingLoginRequired;
    }

    public boolean isReviewLoginRequired() {
        return isReviewLoginRequired;
    }

    public boolean isFacebookAvailable() {
        return isFacebookAvailable;
    }

    public String getSuggesterProvider() { return mSuggesterProvider; }

    public String getApplicationId() { return mApplicationId; }

    public String getSuggesterApiKey() { return mSuggesterApiKey; }

    public String getNamespacePrefix() { return mNamespacePrefix; }

    public boolean isAlgoliaSearchEngine() { return mUseAlgolia; }

    protected CountryConfigs(Parcel in) {
        mCurrencyIso = in.readString();
        mCurrencySymbol = in.readString();
        mCurrencyPosition = in.readString();
        mNoDecimals = in.readInt();
        mThousandsSep = in.readString();
        mDecimalsSep = in.readString();
        mGaId = in.readString();
        mGTMId = in.readString();
        mPhoneNumber = in.readString();
        mCsEmail = in.readString();
        isRatingEnable = in.readByte() != 0x00;
        isReviewEnable = in.readByte() != 0x00;
        isRatingLoginRequired = in.readByte() != 0x00;
        isReviewLoginRequired = in.readByte() != 0x00;
        isFacebookAvailable = in.readByte() != 0x00;
        hasCartPopup = in.readByte() != 0x00;
        mIsRichRelevanceEnabled = in.readByte() != 0x00;
        mSuggesterProvider = in.readString();
        mApplicationId = in.readString();
        mSuggesterApiKey = in.readString();
        mNamespacePrefix = in.readString();
        mUseAlgolia = in.readByte() != 0x00;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mCurrencyIso);
        dest.writeString(mCurrencySymbol);
        dest.writeString(mCurrencyPosition);
        dest.writeInt(mNoDecimals);
        dest.writeString(mThousandsSep);
        dest.writeString(mDecimalsSep);
        dest.writeString(mGaId);
        dest.writeString(mGTMId);
        dest.writeString(mPhoneNumber);
        dest.writeString(mCsEmail);
        dest.writeByte((byte) (isRatingEnable ? 0x01 : 0x00));
        dest.writeByte((byte) (isReviewEnable ? 0x01 : 0x00));
        dest.writeByte((byte) (isRatingLoginRequired ? 0x01 : 0x00));
        dest.writeByte((byte) (isReviewLoginRequired ? 0x01 : 0x00));
        dest.writeByte((byte) (isFacebookAvailable ? 0x01 : 0x00));
        dest.writeByte((byte) (hasCartPopup ? 0x01 : 0x00));
        dest.writeByte((byte) (mIsRichRelevanceEnabled ? 0x01 : 0x00));
        dest.writeString(mSuggesterProvider);
        dest.writeString(mApplicationId);
        dest.writeString(mSuggesterApiKey);
        dest.writeString(mNamespacePrefix);
        dest.writeByte((byte) (mUseAlgolia ? 0x01 : 0x00));
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CountryConfigs> CREATOR = new Parcelable.Creator<CountryConfigs>() {
        @Override
        public CountryConfigs createFromParcel(Parcel in) {
            return new CountryConfigs(in);
        }

        @Override
        public CountryConfigs[] newArray(int size) {
            return new CountryConfigs[size];
        }
    };

    public void setLanguages(Languages languages) {
        this.languages = languages;
    }

    public Languages getLanguages() {
        return languages;
    }

    public List<TargetHelper> getMobileAbout() {
        return mobileAbout;
    }
}