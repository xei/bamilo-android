package com.mobile.service.objects.catalog;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.service.objects.IJSONSerializable;
import com.mobile.service.objects.RequiredJson;
import com.mobile.service.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class that represents a catalog banner
 * 
 * @author Paulo Carvalho
 * @modified spereira
 * 
 */
public class Banner implements IJSONSerializable, Parcelable {

    private String mPhoneImage;
    private String mTabletImage;
    private String mTarget;
    private String mTitle;

    public Banner() {
        // ...
    }

    public String getPhoneImage() {
        return mPhoneImage;
    }

    public String getTabletImage() {
        return mTabletImage;
    }

    public String getTarget() {
        return mTarget;
    }

    public String getTitle() {
        return mTitle;
    }

    /* (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        try {
            mPhoneImage = jsonObject.getString(RestConstants.PHONE_IMAGE);
            mTabletImage = jsonObject.getString(RestConstants.TABLET_IMAGE);
            mTarget = jsonObject.getString(RestConstants.TARGET);
            mTitle = jsonObject.getString(RestConstants.TITLE);
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
	    dest.writeString(mTarget);
        dest.writeString(mTitle);
	}

	/**
	 * Parcel constructor
	 */
    private Banner(Parcel in) {
        mPhoneImage = in.readString();
        mTabletImage = in.readString();
        mTarget = in.readString();
        mTitle = in.readString();
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
