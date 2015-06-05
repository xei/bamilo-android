package com.mobile.newFramework.objects.campaign;


import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.objects.BaseProduct;
import com.mobile.framework.objects.CampaignItemSize;
import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class used to represent an item associated a campaign
 *
 * @author sergiopereira
 */
public class CampaignItem extends BaseProduct implements IJSONSerializable {

    private double mSavePrice;

    private String mImage;

    private ArrayList<String> mImages;

    private int mStockPercentage;

    private double mMaxSavingPercentage;

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

    /**
     * Constructor
     *
     * @param jsonObject
     */
    public CampaignItem(JSONObject jsonObject) {
        initialize(jsonObject);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
     * )
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
//        Log.i(TAG, "ON INITIALIZE");
        mSavePrice = jsonObject.optDouble(RestConstants.JSON_SAVE_PRICE_TAG, 0d);
        specialPriceDouble = jsonObject.optDouble(RestConstants.JSON_SPECIAL_PRICE_TAG, 0d);
        specialPriceConverted = jsonObject.optDouble(RestConstants.JSON_SPECIAL_PRICE_CONVERTED_TAG, 0d);
        priceDouble = jsonObject.optDouble(RestConstants.JSON_PRICE_TAG, 0d);
        priceConverted = jsonObject.optDouble(RestConstants.JSON_PRICE_CONVERTED_TAG, 0d);
        sku = jsonObject.optString(RestConstants.JSON_SKU_TAG);
        brand = jsonObject.optString(RestConstants.JSON_BRAND_TAG);
        name = jsonObject.optString(RestConstants.JSON_NAME_TAG);
        mStockPercentage = jsonObject.optInt(RestConstants.JSON_STOCK_PERCENTAGE_TAG, 0);
        mMaxSavingPercentage = jsonObject.optDouble(RestConstants.JSON_MAX_SAVING_PERCENTAGE_TAG, 0d);
        hasUniqueSize = jsonObject.optBoolean(RestConstants.JSON_HAS_UNIQUE_SIZE_TAG);
        mRemainingTime = jsonObject.optInt(RestConstants.JSON_REMAINING_TIME_TAG, -1);

        // Save images
        JSONArray imagesA = jsonObject.optJSONArray(RestConstants.JSON_IMAGES_TAG);
        if (imagesA != null && imagesA.length() > 0) {
            mImage = imagesA.optString(0);
            mImages = new ArrayList<>();
            for (int i = 0; i < imagesA.length(); i++) {
                mImages.add(imagesA.optString(i));
            }
        }

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
        return brand + " " + name + " " + sku + " " + mImage;
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
     * @return the mImage
     */
    public String getImage() {
        return mImage;
    }

    /**
     * @return the mImages
     */
    public ArrayList<String> getImages() {
        return mImages;
    }

    /**
     * @return the mStockPercentage
     */
    public int getStockPercentage() {
        return mStockPercentage;
    }

    /**
     * @return the mMaxSavingPercentage
     */
    public double getMaxSavingPercentage() {
        return mMaxSavingPercentage;
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

    /**
     * Return the paid price for tracking.
     *
     * @return double
     * @author sergiopereira
     */
    public double getPriceForTracking() {
//        Log.i(TAG, "ORIGIN PRICE VALUES: " + priceDouble + " " + specialPriceDouble);
//        Log.i(TAG, "PRICE VALUE FOR TRACKING: " + priceConverted + " " + specialPriceConverted);
        return specialPriceConverted > 0 ? specialPriceConverted : priceConverted;
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
     * @param mSavePrice
     *            the mSavePrice to set
     */
    public void setSavePrice(Double mSavePrice) {
        this.mSavePrice = mSavePrice;
    }

    /**
     * @param mImage
     *            the mImage to set
     */
    public void setImage(String mImage) {
        this.mImage = mImage;
    }

    /**
     * @param mImages
     *            the mImages to set
     */
    public void setImages(ArrayList<String> mImages) {
        this.mImages = mImages;
    }

    /**
     * @param mStockPercentage
     *            the mStockPercentage to set
     */
    public void setStockPercentage(int mStockPercentage) {
        this.mStockPercentage = mStockPercentage;
    }

    /**
     * @param mMaxSavingPercentage
     *            the mMaxSavingPercentage to set
     */
    public void setMaxSavingPercentage(double mMaxSavingPercentage) {
        this.mMaxSavingPercentage = mMaxSavingPercentage;
    }

    /**
     * @param hasUniqueSize
     *            the hasUniqueSize to set
     */
    public void setHasUniqueSize(boolean hasUniqueSize) {
        this.hasUniqueSize = hasUniqueSize;
    }

    /**
     * @param mSizes
     *            the mSizes to set
     */
    public void setSizes(ArrayList<CampaignItemSize> mSizes) {
        this.mSizes = mSizes;
    }

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
     * @param mRemainingTime
     *            the mRemainingTime to set
     */
    public void setRemainingTime(int mRemainingTime) {
        this.mRemainingTime = mRemainingTime;
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
        dest.writeDouble(priceDouble);
        dest.writeDouble(specialPriceDouble);
        dest.writeString(sku);
        dest.writeString(brand);
        dest.writeString(name);
        dest.writeString(mImage);
        dest.writeList(mImages);
        dest.writeInt(mStockPercentage);
        dest.writeDouble(mMaxSavingPercentage);
        dest.writeBooleanArray(new boolean[] { hasUniqueSize });
        dest.writeList(mSizes);
        dest.writeParcelable(mSelectedSize, 0);
        dest.writeInt(mSelectedSizePosition);
        dest.writeInt(mRemainingTime);
        dest.writeDouble(priceConverted);
        dest.writeDouble(specialPriceConverted);
    }

    /**
     * Parcel constructor
     *
     * @param in
     */
    public CampaignItem(Parcel in) {
        mSavePrice = in.readDouble();
        specialPriceDouble = in.readDouble();
        priceDouble = in.readDouble();
        sku = in.readString();
        brand = in.readString();
        name = in.readString();
        mImage = in.readString();
        mImages = new ArrayList<>();
        in.readList(mImages, String.class.getClassLoader());
        mStockPercentage = in.readInt();
        mMaxSavingPercentage = in.readDouble();
        boolean[] bolArray = new boolean[1];
        in.readBooleanArray(bolArray);
        hasUniqueSize = bolArray[0];
        mSizes = new ArrayList<>();
        in.readList(mSizes, CampaignItemSize.class.getClassLoader());
        mSelectedSize = in.readParcelable(CampaignItemSize.class.getClassLoader());
        mSelectedSizePosition = in.readInt();
        mRemainingTime = in.readInt();
        priceConverted = in.readDouble();
        specialPriceConverted = in.readDouble();
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
