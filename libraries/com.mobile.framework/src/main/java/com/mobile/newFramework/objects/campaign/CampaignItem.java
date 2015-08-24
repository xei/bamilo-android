package com.mobile.newFramework.objects.campaign;


import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.product.pojo.ProductRegular;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class used to represent an item associated a campaign
 *
 * @author sergiopereira
 */
public class CampaignItem extends ProductRegular implements IJSONSerializable {

    private double mSavePrice;

    private int mStockPercentage;

    private boolean hasUniqueSize;

    private int mRemainingTime;

    private ArrayList<CampaignItemSize> mSizes;

    private CampaignItemSize mSelectedSize;

    private int mSelectedSizePosition;

    /**
     * Empty constructor
     */
    public CampaignItem() {
        super();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
     * )
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {

        super.initialize(jsonObject);

        mSavePrice = jsonObject.optDouble(RestConstants.JSON_SAVE_PRICE_TAG, 0d);
        mStockPercentage = jsonObject.optInt(RestConstants.JSON_STOCK_PERCENTAGE_TAG, 0);

        hasUniqueSize = jsonObject.optBoolean(RestConstants.JSON_HAS_UNIQUE_SIZE_TAG);
        mRemainingTime = jsonObject.optInt(RestConstants.JSON_REMAINING_TIME_TAG, -1);

        // Save sizes
        JSONArray sizesA = jsonObject.optJSONArray(RestConstants.JSON_SIZES_TAG);
        if (sizesA != null && sizesA.length() > 0) {
            mSizes = new ArrayList<>();
            for (int i = 0; i < sizesA.length(); i++) {
                JSONObject sizeO = sizesA.optJSONObject(i);
                if (sizeO != null)
                    mSizes.add(new CampaignItemSize(sizeO));
            }
        }

        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return mBrand + " " + mName + " " + mSku + " " + mImageUrl;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return null;
    }

	/*
	 * ########### Getters ###########
	 */
    /**
     * @return the mSavePrice
     */
    public Double getSavePrice() {
        return mSavePrice;
    }

    /**
     * @return the mStockPercentage
     */
    public int getStockPercentage() {
        return mStockPercentage;
    }

    /**
     * @return the mSizes
     */
    public ArrayList<CampaignItemSize> getSizes() {
        return mSizes;
    }

    /**
     * @return the mSelectedSize
     */
    public CampaignItemSize getSelectedSize() {
        return mSelectedSize;
    }

    /**
     * @return the mSelectedSizePosition
     */
    public int getSelectedSizePosition() {
        return mSelectedSizePosition;
    }

    /***
     * @return the mRemainingTime
     */
    public int getRemainingTime() {
        return mRemainingTime;
    }

	/*
	 * ########### Validators ###########
	 */
    /**
     * @return the hasUniqueSize
     */
    public boolean hasUniqueSize() {
        return hasUniqueSize;
    }

    /**
     * @return the hasSizes except itself
     */
    public boolean hasSizes() {
        return mSizes != null && mSizes.size() > 0;
    }

    /**
     * @return the hasSizes
     */
    public boolean hasSelectedSize() {
        return hasSizes() && mSelectedSizePosition >= 0 && mSelectedSizePosition < mSizes.size();
    }

    /**
     * @return the hasSizes
     */
    public boolean hasStock() {
        return mStockPercentage > 0;
    }

	/*
	 * ########### Setters ###########
	 */

    /**
     * @param mSelectedSize
     *            the mSelectedSize to set
     */
    public void setSelectedSize(CampaignItemSize mSelectedSize) {
        this.mSelectedSize = mSelectedSize;
    }

    /**
     * @param mSelectedSizePosition
     *            the mSelectedSizePosition to set
     */
    public void setSelectedSizePosition(int mSelectedSizePosition) {
        this.mSelectedSizePosition = mSelectedSizePosition;
    }

    /**
     * ########### Parcelable ###########
     */
	/*
	 * (non-Javadoc)
	 *
	 * @see android.os.Parcelable#describeContents()
	 */
    @Override
    public int describeContents() {
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeDouble(mSavePrice);
        dest.writeInt(mStockPercentage);
        dest.writeDouble(mMaxSavingPercentage);
        dest.writeBooleanArray(new boolean[] { hasUniqueSize });
        dest.writeList(mSizes);
        dest.writeParcelable(mSelectedSize, 0);
        dest.writeInt(mSelectedSizePosition);
        dest.writeInt(mRemainingTime);
    }

    /**
     * Parcel constructor
     */
    public CampaignItem(Parcel in) {
        mSavePrice = in.readDouble();
        mStockPercentage = in.readInt();
        boolean[] bolArray = new boolean[1];
        in.readBooleanArray(bolArray);
        hasUniqueSize = bolArray[0];
        mSizes = new ArrayList<>();
        in.readList(mSizes, CampaignItemSize.class.getClassLoader());
        mSelectedSize = in.readParcelable(CampaignItemSize.class.getClassLoader());
        mSelectedSizePosition = in.readInt();
        mRemainingTime = in.readInt();
    }

    /**
     * Create parcelable
     */
    public static final Parcelable.Creator<CampaignItem> CREATOR = new Parcelable.Creator<CampaignItem>() {
        public CampaignItem createFromParcel(Parcel in) {
            return new CampaignItem(in);
        }

        public CampaignItem[] newArray(int size) {
            return new CampaignItem[size];
        }
    };

}
