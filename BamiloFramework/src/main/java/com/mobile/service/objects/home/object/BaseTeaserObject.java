package com.mobile.service.objects.home.object;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.service.objects.IJSONSerializable;
import com.mobile.service.objects.RequiredJson;
import com.mobile.service.pojo.RestConstants;
import com.mobile.service.utils.DateTimeUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class used to represent the base of a teaser item.
 * @author spereira
 */
public class BaseTeaserObject implements IJSONSerializable, Parcelable {

    protected String mTitle;

    protected String mSubTitle;

    protected String mImagePhone;

    protected String mImageTablet;

    protected String mTargetLink;

    protected long mTimerInMillis;

    protected int mTeaserTypeId;

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

    public String getImage() {
        return mImagePhone;
    }

    public String getImage(Boolean isTablet) {
        return isTablet ? mImageTablet : mImagePhone;
    }

    public String getTargetLink() {
        return mTargetLink;
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

    /*
     * ########## JSON ##########
     */

    /**
     * Initialize object via JSONObject
     * @throws JSONException
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        // Get title
        mTitle = jsonObject.optString(RestConstants.TITLE);
        // Get sub title
        mSubTitle = jsonObject.optString(RestConstants.SUB_TITLE);
        // Get target link
        mTargetLink = jsonObject.optString(RestConstants.TARGET);
        // Get timer in seconds and convert to millis
        mTimerInMillis = jsonObject.optLong(RestConstants.UNIX_TIME) * DateTimeUtils.UNIT_SEC_TO_MILLIS;
        // Validate images
        if (jsonObject.has(RestConstants.IMAGE)) {
            // Get image
            mImagePhone = mImageTablet = jsonObject.optString(RestConstants.IMAGE);
        } else {
            // Get image phone
            mImagePhone = jsonObject.optString(RestConstants.IMAGE_PORTRAIT);
            // Get image tablet
            mImageTablet = jsonObject.optString(RestConstants.IMAGE_LANDSCAPE);
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
        dest.writeString(this.mTitle);
        dest.writeString(this.mSubTitle);
        dest.writeString(this.mImagePhone);
        dest.writeString(this.mImageTablet);
        dest.writeString(this.mTargetLink);
        dest.writeLong(this.mTimerInMillis);
        dest.writeInt(this.mTeaserTypeId);
    }

    protected BaseTeaserObject(Parcel in) {
        this.mTitle = in.readString();
        this.mSubTitle = in.readString();
        this.mImagePhone = in.readString();
        this.mImageTablet = in.readString();
        this.mTargetLink = in.readString();
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

}
