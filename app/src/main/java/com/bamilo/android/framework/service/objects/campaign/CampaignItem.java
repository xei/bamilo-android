package com.bamilo.android.framework.service.objects.campaign;


import android.os.Parcel;
import android.os.Parcelable;

import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.objects.product.pojo.ProductMultiple;
import com.bamilo.android.framework.service.pojo.IntConstants;
import com.bamilo.android.framework.service.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class used to represent an item associated a campaign
 *
 * @author sergiopereira
 */
public class CampaignItem extends ProductMultiple implements IJSONSerializable {

    private double mSavePrice;

    private int mStockPercentage;

    private boolean hasUniqueSize;

    private int mRemainingTime;

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
        mSavePrice = jsonObject.optDouble(RestConstants.SAVE_PRICE, 0d);
        mStockPercentage = jsonObject.optInt(RestConstants.STOCK_PERCENTAGE);
        hasUniqueSize = jsonObject.optBoolean(RestConstants.HAS_UNIQUE_SIZE);
        mRemainingTime = jsonObject.optInt(RestConstants.REMAINING_TIME, -1);
        mSelectedSimplePosition = IntConstants.DEFAULT_POSITION;
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return mBrand.getName() + " " + mName + " " + mSku + " " + mImageUrl;
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
    public int getRequiredJson() {
        return RequiredJson.NONE;
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
     * @return the hasSizes
     */
    public boolean hasStock() {
        return mStockPercentage > 0;
    }

    /**
     * ########### Parcelable ###########
     */


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeDouble(mSavePrice);
        dest.writeInt(mStockPercentage);
        dest.writeByte((byte) (hasUniqueSize ? 0x01 : 0x00));
        dest.writeInt(mRemainingTime);
    }

    protected CampaignItem(Parcel in) {
        super(in);
        mSavePrice = in.readDouble();
        mStockPercentage = in.readInt();
        hasUniqueSize = in.readByte() != 0x00;
        mRemainingTime = in.readInt();
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CampaignItem> CREATOR = new Parcelable.Creator<CampaignItem>() {
        @Override
        public CampaignItem createFromParcel(Parcel in) {
            return new CampaignItem(in);
        }

        @Override
        public CampaignItem[] newArray(int size) {
            return new CampaignItem[size];
        }
    };
}
