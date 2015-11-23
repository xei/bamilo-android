package com.mobile.newFramework.objects.home.object;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.DateTimeUtils;
import com.mobile.newFramework.utils.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class used to represent the base of a teaser item.
 * @author spereira
 */
public class BaseTeaserObject implements IJSONSerializable, Parcelable {

    protected String mTitle;

    protected String mSubTitle;

    protected String mUrl;

    protected String mSku;

    protected String mImagePhone;

    protected String mImageTablet;

    protected String mTarget;

    protected String mTargetType;

    protected String mTargetKey;

    protected long mTimerInMillis;

    protected int mTeaserTypeId;

    protected static final String TEASER_SEPARATOR = "::";

    /**
     * Empty constructor
     */
    public BaseTeaserObject(int teaserTypeId) {
        mTeaserTypeId = teaserTypeId;
    }

    /*
     * ########## GETTERS ##########
     */

    public String getTitle() {
        return mTitle;
    }

    public String getSubTitle() {
        return mSubTitle;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getImage() {
        return mImagePhone;
    }

    public String getImage(Boolean isTablet) {
        return isTablet ? mImageTablet : mImagePhone;
    }

    public String getTargetType() {
        return mTargetType;
    }

    public String getTargetKey() {
        return mTargetKey;
    }

    public String getTarget() {
        return mTarget;
    }

    public boolean hasTimer() {
        return mTimerInMillis > 0;
    }

    public boolean hasValidRealTimer() {
        return mTimerInMillis - System.currentTimeMillis() > 0;
    }

    public long getRealTimer() {
        return mTimerInMillis - System.currentTimeMillis();
    }

    public int getTeaserTypeId() {
        return mTeaserTypeId;
    }

    public String getSku() {
        return mSku;
    }

    /*
     * ########## JSON ##########
     */

    /**
     * Initialize object via JSONObject
     * @throws JSONException
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        // Get sku
        mSku = jsonObject.optString(RestConstants.SKU);
        // Get title
        mTitle = jsonObject.optString(RestConstants.JSON_TITLE_TAG);
        // Get sub title
        mSubTitle = jsonObject.optString(RestConstants.JSON_SUB_TITLE_TAG);
        // Get url
        //FIXME to be removed after API fixes
        mUrl = jsonObject.optString(RestConstants.URL);
        // Get target
        mTarget = jsonObject.optString(RestConstants.JSON_TARGET_TAG);
        setTargetInfo(mTarget);
        // Get timer in seconds and convert to millis
        mTimerInMillis = jsonObject.optLong(RestConstants.JSON_UNIX_TIME_TAG) * DateTimeUtils.UNIT_SEC_TO_MILLIS;
        // Validate images
        if (jsonObject.has(RestConstants.JSON_IMAGE_TAG)) {
            // Get image
            mImagePhone = mImageTablet = jsonObject.optString(RestConstants.JSON_IMAGE_TAG);
        } else {
            // Get image phone
            mImagePhone = jsonObject.optString(RestConstants.JSON_IMAGE_PORTRAIT_TAG);
            // Get image tablet
            mImageTablet = jsonObject.optString(RestConstants.JSON_IMAGE_LANDSCAPE_TAG);
        }
        return false;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.NONE;
    }

    /*
     * ########## PARCELABLE ##########
     */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.mSku);
        dest.writeString(this.mTitle);
        dest.writeString(this.mSubTitle);
        dest.writeString(this.mUrl);
        dest.writeString(this.mImagePhone);
        dest.writeString(this.mImageTablet);
        dest.writeString(this.mTarget);
        dest.writeString(this.mTargetType);
        dest.writeString(this.mTargetKey);
        dest.writeLong(this.mTimerInMillis);
        dest.writeInt(this.mTeaserTypeId);
    }

    protected BaseTeaserObject(Parcel in) {
        this.mSku = in.readString();
        this.mTitle = in.readString();
        this.mSubTitle = in.readString();
        this.mUrl = in.readString();
        this.mImagePhone = in.readString();
        this.mImageTablet = in.readString();
        this.mTarget = in.readString();
        this.mTargetType = in.readString();
        this.mTargetKey = in.readString();
        this.mTimerInMillis = in.readLong();
        this.mTeaserTypeId = in.readInt();
    }

    public static final Creator<BaseTeaserObject> CREATOR = new Creator<BaseTeaserObject>() {
        public BaseTeaserObject createFromParcel(Parcel source) {
            return new BaseTeaserObject(source);
        }

        public BaseTeaserObject[] newArray(int size) {
            return new BaseTeaserObject[size];
        }
    };

    /**
     * Function responsible for separating the target type and target key information
     * @param target
     */
    protected void setTargetInfo(String target) {
        if (TextUtils.isNotEmpty(target)) {
            String[] targetInfo = target.split(TEASER_SEPARATOR);
            mTargetType = targetInfo[0];
            if(targetInfo.length == 2){
                mTargetKey = targetInfo[1];
            }

        }
    }
}
