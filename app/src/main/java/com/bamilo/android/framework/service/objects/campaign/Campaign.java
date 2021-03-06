package com.bamilo.android.framework.service.objects.campaign;


import android.os.Parcel;
import android.os.Parcelable;

import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.pojo.RestConstants;

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

    private int mCount;

    private int mTotalProducts;

    private ArrayList<CampaignItem> mItems = new ArrayList<>();



    /**
     * Empty constructor
     */
    public Campaign() {
        // ...
    }

    /**
     * Constructor
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
        // Get banner
        JSONObject cms = jsonObject.optJSONObject(RestConstants.CMS);
        // Get mobile and tablet banners
        JSONArray banner = (cms != null) ? cms.optJSONArray(RestConstants.MOBILE_BANNER) : null;
        mMobileBanner = (banner != null && banner.length() > 0) ? banner.get(0).toString() : null;
        JSONArray bannerDesktopA = (cms != null) ? cms.optJSONArray(RestConstants.DESKTOP_BANNER) : null;
        mTabletBanner = (bannerDesktopA != null && bannerDesktopA.length() > 0) ? bannerDesktopA.get(0).toString() : null;
        mTabletBanner = ((mTabletBanner == null) ? mMobileBanner : mTabletBanner);
        // Get campaign
        JSONObject campaign = jsonObject.getJSONObject(RestConstants.CAMPAIGN_ENTITY);
        // Get name
        mName = campaign.optString(RestConstants.NAME);
        // Get product count
        mCount = campaign.optInt(RestConstants.PRODUCT_COUNT);
        mTotalProducts = campaign.optInt(RestConstants.TOTAL_PRODUCTS);
        // Get data
        JSONArray itemsA = campaign.getJSONArray(RestConstants.PRODUCTS);
        for (int i = 0; i < itemsA.length(); i++) {
            CampaignItem item = new CampaignItem();
            if (item.initialize(itemsA.getJSONObject(i))) {
                mItems.add(item);
            }
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return mName + " " + mMobileBanner + " " + mCount;
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
    public int getRequiredJson() {
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
     * @param mName the mName to set
     */
    public void setName(String mName) {
        this.mName = mName;
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
        dest.writeInt(mCount);
        dest.writeList(mItems);
        dest.writeInt(mTotalProducts);
    }

    /**
     * Parcel constructor
     */
    public Campaign(Parcel in) {
        mMobileBanner = in.readString();
        mTabletBanner = in.readString();
        mName = in.readString();
        mCount = in.readInt();
        mItems = new ArrayList<>();
        in.readList(mItems, CampaignItem.class.getClassLoader());
        mTotalProducts = in.readInt();
    }

    /**
     * Create parcelable
     */
    public static final Creator<Campaign> CREATOR = new Creator<Campaign>() {
        public Campaign createFromParcel(Parcel in) {
            return new Campaign(in);
        }

        public Campaign[] newArray(int size) {
            return new Campaign[size];
        }
    };


}

