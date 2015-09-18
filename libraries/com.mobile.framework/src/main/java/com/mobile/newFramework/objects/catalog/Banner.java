/**
 * @author Guilherme Silva
 * 
 * @version 1.01
 * 
 * 2012/06/18
 * 
 * Copyright (c) Rocket Internet All Rights Reserved
 */
package com.mobile.newFramework.objects.catalog;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that represents a catalog banner
 * 
 * @author Paulo Carvalho
 * 
 */
public class Banner implements IJSONSerializable, Parcelable {

    private String mPhoneImage;
    private String mTabletImage;
    private String mTargetType;
    private String mTitle;
    private String mUrl;

    public Banner() {
        this.setPhoneImage("");
        this.setTabletImage("");
        this.setUrl("");
        this.setTargetType("");
        this.setTitle("");
    }

    public String getPhoneImage() {
        return mPhoneImage;
    }

    public void setPhoneImage(String mPhoneImage) {
        this.mPhoneImage = mPhoneImage;
    }

    public String getTabletImage() {
        return mTabletImage;
    }

    public void setTabletImage(String mTabletImage) {
        this.mTabletImage = mTabletImage;
    }

    public String getTargetType() {
        return mTargetType;
    }

    public void setTargetType(String mTargetType) {
        this.mTargetType = mTargetType;
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }


    /* (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        try {
            mPhoneImage = jsonObject.getString(RestConstants.JSON_BANNER_PHONE_IMG_TAG);
            mTabletImage = jsonObject.getString(RestConstants.JSON_BANNER_TABLET_IMG_TAG);
            mTargetType = jsonObject.getString(RestConstants.JSON_TARGET_TYPE_TAG);
            mTitle = jsonObject.getString(RestConstants.JSON_TITLE_TAG);
            mUrl = jsonObject.getString(RestConstants.URL);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /* (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.NONE;
    }

    /**
     * ########### Parcelable ###########
     */

    /*
     * (non-Javadoc)
     * @see android.os.Parcelable#describeContents()
     */
	@Override
	public int describeContents() {
		return 0;
	}

	/*
	 * (non-Javadoc)
	 * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
	 */
	@Override
	public void writeToParcel(Parcel dest, int flags) {
	    dest.writeString(mPhoneImage);
	    dest.writeString(mTabletImage);
	    dest.writeString(mTargetType);
        dest.writeString(mTitle);
        dest.writeString(mUrl);
	}

	/**
	 * Parcel constructor
	 * @param in
	 */
    private Banner(Parcel in) {
        mPhoneImage = in.readString();
        mTabletImage = in.readString();
        mTargetType = in.readString();
        mTitle = in.readString();
        mUrl = in.readString();
    }

	/**
	 * Create parcelable
	 */
    public static final Creator<Banner> CREATOR = new Creator<Banner>() {
        public Banner createFromParcel(Parcel in) {
            return new Banner(in);
        }

        public Banner[] newArray(int size) {
            return new Banner[size];
        }
    };


}
