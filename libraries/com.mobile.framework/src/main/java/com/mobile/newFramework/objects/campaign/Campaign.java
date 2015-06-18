package com.mobile.newFramework.objects.campaign;


import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class that represent a campaign page
 * @author sergiopereira
 *
 */
public class Campaign implements IJSONSerializable, Parcelable {

    private String mMobileBanner;

    private String mTabletBanner;

    private String mName;

    private String mStartTime;

    private String mEndTime;

    private int mCount;

    private ArrayList<CampaignItem> mItems = new ArrayList<>();

    /**
     * Empty constructor
     */
    public Campaign() {
        // ...
    }

    /**
     * Constructor
     * @param jsonObject
     * @throws JSONException
     */
    public Campaign(JSONObject jsonObject) throws JSONException {
        initialize(jsonObject);
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
//        Log.d(TAG, "ON INITIALIZE");
        // Get banner
        JSONObject cmsO = jsonObject.optJSONObject(RestConstants.JSON_CMS_TAG);
        // Get mobile and tablet banners
        JSONArray bannerMobileA = (cmsO != null) ? cmsO.optJSONArray(RestConstants.JSON_MOBILE_BANNER_TAG) : null;
        mMobileBanner = (bannerMobileA != null && bannerMobileA.length() > 0) ? bannerMobileA.get(0).toString() : null;
        JSONArray bannerDesktopA = (cmsO != null) ? cmsO.optJSONArray(RestConstants.JSON_DESKTOP_BANNER_TAG) : null;
        mTabletBanner = (bannerDesktopA != null && bannerDesktopA.length() > 0) ? bannerDesktopA.get(0).toString() : null;
        mTabletBanner = ((mTabletBanner == null) ? mMobileBanner : mTabletBanner);
        // Get campaign
        JSONObject campaignO = jsonObject.getJSONObject(RestConstants.JSON_CAMPAIGN_TAG);
        // Get name
        mName = campaignO.optString(RestConstants.JSON_NAME_TAG);
        // Get start time
        mStartTime = campaignO.optString(RestConstants.JSON_START_TIME_TAG);
        // Get end time
        mEndTime = campaignO.optString(RestConstants.JSON_END_TIME_TAG);
        // Get product count
        mCount = campaignO.optInt(RestConstants.JSON_PRODUCT_COUNT_TAG);
        // Get data
        JSONArray itemsA = campaignO.getJSONArray(RestConstants.JSON_DATA_TAG);
        for (int i = 0; i < itemsA.length(); i++) {
            mItems.add(new CampaignItem(itemsA.getJSONObject(i)));
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return mName + " " + mMobileBanner + " " + mStartTime + " " + mEndTime + " " + mCount;
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return RequiredJson.OBJECT_DATA;
    }

	/*
     * ########### Getters ###########
     */
    /**
     * @return the mBanner
     */
    public String getMobileBanner() {
        return mMobileBanner;
    }

    /**
     * @return the mBanner
     */
    public String getTabletBanner() {
        return mTabletBanner;
    }

    /**
     * @return the mName
     */
    public String getName() {
        return mName;
    }

    /**
     * @return the mStartTime
     */
    public String getStartTime() {
        return mStartTime;
    }

    /**
     * @return the mEndTime
     */
    public String getEndTime() {
        return mEndTime;
    }

    /**
     * @return the mCount
     */
    public int getCount() {
        return mCount;
    }

    /**
     * @return the mItems
     */
    public ArrayList<CampaignItem> getItems() {
        return mItems;
    }


	/*
     * ########### Setters ###########
     */
    /**
     * @param mBanner the mBanner to set
     */
    public void setMobileBanner(String mBanner) {
        this.mMobileBanner = mBanner;
    }

    /**
     * @param mBanner the mBanner to set
     */
    public void setTabletBanner(String mBanner) {
        this.mTabletBanner = mBanner;
    }

    /**
     * @param mName the mName to set
     */
    public void setName(String mName) {
        this.mName = mName;
    }

    /**
     * @param mStartTime the mStartTime to set
     */
    public void setStartTime(String mStartTime) {
        this.mStartTime = mStartTime;
    }

    /**
     * @param mEndTime the mEndTime to set
     */
    public void setEndTime(String mEndTime) {
        this.mEndTime = mEndTime;
    }

    /**
     * @param mCount the mCount to set
     */
    public void setCount(int mCount) {
        this.mCount = mCount;
    }

    /**
     * @param mItems the mItems to set
     */
    public void setItems(ArrayList<CampaignItem> mItems) {
        this.mItems = mItems;
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
        dest.writeString(mMobileBanner);
        dest.writeString(mTabletBanner);
        dest.writeString(mName);
        dest.writeString(mStartTime);
        dest.writeString(mEndTime);
        dest.writeInt(mCount);
        dest.writeList(mItems);
    }

    /**
     * Parcel constructor
     * @param in
     */
    public Campaign(Parcel in) {
        mMobileBanner = in.readString();
        mTabletBanner = in.readString();
        mName = in.readString();
        mStartTime = in.readString();
        mEndTime = in.readString();
        mCount = in.readInt();
        mItems = new ArrayList<CampaignItem>();
        in.readList(mItems, CampaignItem.class.getClassLoader());
    }

    /**
     * Create parcelable
     */
    public static final Parcelable.Creator<Campaign> CREATOR = new Parcelable.Creator<Campaign>() {
        public Campaign createFromParcel(Parcel in) {
            return new Campaign(in);
        }

        public Campaign[] newArray(int size) {
            return new Campaign[size];
        }
    };

}

