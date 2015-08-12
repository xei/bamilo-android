package com.mobile.newFramework.objects.product;

import android.os.Parcel;

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
public class NewProductPartial extends NewProductBase {

    protected String mName;
    protected String mBrand;
    protected String mImageUrl;
    private String mCategories;
    protected boolean isNew;
    protected boolean isWishList;
    protected double mAvgRating;
    protected int mTotalReviews;
    protected int mTotalRatings;

    /**
     * Empty constructor
     */
    public NewProductPartial(){
        super();
    }

    /*
     * ############ IJSONSerializable ############
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        // Mandatory
        super.initialize(jsonObject);
        // Mandatory
        mName = jsonObject.getString(RestConstants.JSON_NAME_TAG);
        mBrand = jsonObject.getString(RestConstants.JSON_BRAND_TAG);
        // Optional
        mImageUrl = jsonObject.optString(RestConstants.JSON_IMAGE_TAG);
        isNew = jsonObject.optBoolean(RestConstants.JSON_IS_NEW_TAG);
        isWishList = jsonObject.optBoolean(RestConstants.JSON_IS_WISH_LIST_TAG);
        mCategories = jsonObject.optString(RestConstants.JSON_CATEGORIES_TAG);
        // Rating
        JSONObject ratings = jsonObject.optJSONObject(RestConstants.JSON_RATINGS_SUMMARY_TAG);
        if (ratings != null) {
            mAvgRating = ratings.optDouble(RestConstants.JSON_RATINGS_AVERAGE_TAG);
            mTotalRatings = ratings.optInt(RestConstants.JSON_RATINGS_TOTAL_TAG);
            mTotalReviews = ratings.optInt(RestConstants.JSON_REVIEWS_TOTAL_TAG);
        }
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

    public String getName() {
        return mName;
    }

    public String getBrand() {
        return mBrand;
    }

    public String getImageUrl() {
        return mImageUrl;
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

    public double getAvgRating() {
        return mAvgRating;
    }

    public int getTotalReviews() {
        return mTotalReviews;
    }

    public int getTotalRatings() {
        return mTotalRatings;
    }

    public String getCategories() {
        return mCategories;
    }



    /*
	 * ############ PARCELABLE ############
	 */

    protected NewProductPartial(Parcel in) {
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
    public static final Creator<NewProductPartial> CREATOR = new Creator<NewProductPartial>() {
        @Override
        public NewProductPartial createFromParcel(Parcel in) {
            return new NewProductPartial(in);
        }

        @Override
        public NewProductPartial[] newArray(int size) {
            return new NewProductPartial[size];
        }
    };

}
