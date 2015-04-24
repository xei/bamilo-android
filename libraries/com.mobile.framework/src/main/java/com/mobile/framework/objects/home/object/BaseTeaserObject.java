package com.mobile.framework.objects.home.object;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.objects.IJSONSerializable;
import com.mobile.framework.rest.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by spereira on 4/15/15.
 */
public class BaseTeaserObject implements IJSONSerializable, Parcelable {

    protected String mTitle;

    protected String mSubTitle;

    protected String mUrl;

    protected String mImagePhone;

    protected String mImageTablet;

    protected String mTargetType;


    /**
     * Empty constructor
     */
    public BaseTeaserObject() {
        // ...
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

    public String getImagePhone() {
        return mImagePhone;
    }

    public String getImageTablet() {
        return mImageTablet;
    }

    public String getTargetType() {
        return mTargetType;
    }

    /*
     * ########## JSON ##########
     */

    /**
     * TODO
     *
     * @param jsonObject JSONObject containing the parameters of the object
     * @return
     * @throws JSONException
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        // Get title
        mTitle = jsonObject.optString(RestConstants.JSON_TITLE_TAG);
        // Get sub title
        mSubTitle = jsonObject.optString(RestConstants.JSON_SUB_TITLE_TAG);
        // Get url
        mUrl = jsonObject.getString(RestConstants.JSON_URL_TAG);
        // Get image phone
        mImagePhone = jsonObject.optString(RestConstants.JSON_IMAGE_PORTRAIT_TAG);
        // Get image tablet
        mImageTablet = jsonObject.optString(RestConstants.JSON_IMAGE_LANDSCAPE_TAG);
        // Get target type
        mTargetType = jsonObject.optString(RestConstants.JSON_TARGET_TYPE_TAG);
        return false;
    }

    /**
     * TODO
     *
     * @return
     */
    @Override
    public JSONObject toJSON() {
        return null;
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
        dest.writeString(this.mUrl);
        dest.writeString(this.mImagePhone);
        dest.writeString(this.mImageTablet);
        dest.writeString(this.mTargetType);
    }

    protected BaseTeaserObject(Parcel in) {
        this.mTitle = in.readString();
        this.mSubTitle = in.readString();
        this.mUrl = in.readString();
        this.mImagePhone = in.readString();
        this.mImageTablet = in.readString();
        this.mTargetType = in.readString();
    }

}
