package com.mobile.newFramework.objects.product;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * @author sergiopereira
 * @version 2.0
 * @date 2015/08/04
 */
public class NewProductBase implements Parcelable, IJSONSerializable {

    protected String mSku;
    protected String mName;
    protected String mBrand;
    protected String mUrl;
    protected String mImageUrl;
    protected double mPrice;
    protected double mSpecialPrice;
    protected double mPriceConverted;
    protected double mSpecialPriceConverted;
    protected int mMaxSavingPercentage;
    private boolean isNew;
    private boolean isWishList;
    private String mSizeGuideUrl;

    /**
     * Empty constructor
     */
    public NewProductBase(){
        // ...
    }

    /*
     * ############ IJSONSerializable ############
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        // Mandatory
        mSku = jsonObject.getString(RestConstants.JSON_SKU_TAG);
        mName = jsonObject.getString(RestConstants.JSON_NAME_TAG);
        mBrand = jsonObject.getString(RestConstants.JSON_BRAND_TAG);
        mUrl = jsonObject.getString(RestConstants.JSON_URL_TAG);
        mPrice = jsonObject.getDouble(RestConstants.JSON_PRICE_TAG);
        mPriceConverted = jsonObject.getDouble(RestConstants.JSON_PRICE_CONVERTED_TAG);
        // Optional
        mImageUrl = jsonObject.optString(RestConstants.JSON_IMAGE_TAG);
        mSpecialPrice = jsonObject.optDouble(RestConstants.JSON_SPECIAL_PRICE_TAG);
        mSpecialPriceConverted = jsonObject.optDouble(RestConstants.JSON_SPECIAL_PRICE_CONVERTED_TAG);
        mMaxSavingPercentage = jsonObject.optInt(RestConstants.JSON_MAX_SAVING_PERCENTAGE_TAG);
        isNew = jsonObject.optBoolean(RestConstants.JSON_IS_NEW_TAG);
        isWishList = jsonObject.optBoolean(RestConstants.JSON_IS_WISH_LIST_TAG);
        mSizeGuideUrl = jsonObject.optString(RestConstants.JSON_SIZE_GUIDE_URL_TAG);
        return true;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return null;
    }

    /*
	 * ############ GETTERS ############
	 */

    public String getSku() {
        return mSku;
    }

    public String getName() {
        return mName;
    }

    public String getBrand() {
        return mBrand;
    }

    public String getUrl() {
        return mUrl;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public double getPrice() {
        return mPrice;
    }

    public double getPriceConverted() {
        return mPriceConverted;
    }

    public double getSpecialPrice() {
        return mSpecialPrice;
    }

    public double getSpecialPriceConverted() {
        return mSpecialPriceConverted;
    }

    public int getMaxSavingPercentage() {
        return mMaxSavingPercentage;
    }

    public boolean isNew() {
        return isNew;
    }

    public boolean isWishList() {
        return isWishList;
    }

    public void setIsWishList(boolean isWishList) {
        this.isWishList = isWishList;
    }

    public String getSizeGuideUrl() {
        return mSizeGuideUrl;
    }

    public boolean hasDiscount() {
        return mSpecialPrice > 0;
    }

    public double getPriceForTracking() {
        return mSpecialPriceConverted > 0 ? mSpecialPriceConverted : mPriceConverted;
    }

    /*
	 * ############ PARCELABLE ############
	 */

    protected NewProductBase(Parcel in) {
        // TODO
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @SuppressWarnings("unused")
    public static final Creator<NewProductBase> CREATOR = new Creator<NewProductBase>() {
        @Override
        public NewProductBase createFromParcel(Parcel in) {
            return new NewProductBase(in);
        }

        @Override
        public NewProductBase[] newArray(int size) {
            return new NewProductBase[size];
        }
    };

}
