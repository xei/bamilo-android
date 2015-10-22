package com.mobile.newFramework.objects.configs;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.statics.MobileAbout;
import com.mobile.newFramework.objects.statics.TargetHelper;
import com.mobile.newFramework.pojo.RestConstants;

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

    private static final String TAG = CountryConfigs.class.getSimpleName();

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
                "\nreview_login: " + isReviewLoginRequired
                ;
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
        languages = new Languages(jsonObject);

        try {
            mobileAbout = new MobileAbout(jsonObject);
        } catch (JSONException ex) {
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

    public void setCurrencyIso(String mCurrencyIso) {
        this.mCurrencyIso = mCurrencyIso;
    }

    public void setCurrencySymbol(String mCurrencySymbol) {
        this.mCurrencySymbol = mCurrencySymbol;
    }

    public void setNoDecimals(int mNoDecimals) {
        this.mNoDecimals = mNoDecimals;
    }

    public void setThousandsSep(String mThousandsSep) {
        this.mThousandsSep = mThousandsSep;
    }

    public void setDecimalsSep(String mDecimalsSep) {
        this.mDecimalsSep = mDecimalsSep;
    }

    public void setGaId(String mGaId) {
        this.mGaId = mGaId;
    }

    public void setGTMId(String mGTMId) {
        this.mGTMId = mGTMId;
    }

    public void setPhoneNumber(String mPhoneNumber) {
        this.mPhoneNumber = mPhoneNumber;
    }

    public void setCsEmail(String mCsEmail) {
        this.mCsEmail = mCsEmail;
    }

    public void setIsRatingEnable(boolean isRatingEnable) {
        this.isRatingEnable = isRatingEnable;
    }

    public void setIsReviewEnable(boolean isReviewEnable) {
        this.isReviewEnable = isReviewEnable;
    }

    public void setIsRatingLoginRequired(boolean isRatingLoginRequired) {
        this.isRatingLoginRequired = isRatingLoginRequired;
    }

    public void setIsFacebookAvailable(boolean isFacebookAvailable) {
        this.isFacebookAvailable = isFacebookAvailable;
    }

    public void setIsReviewLoginRequired(boolean isReviewLoginRequired) {
        this.isReviewLoginRequired = isReviewLoginRequired;
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