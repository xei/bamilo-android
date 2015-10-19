package com.mobile.newFramework.objects.product.pojo;

import android.os.Parcel;

import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.TextUtils;
import com.mobile.newFramework.utils.cache.WishListCache;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * Defines a base + partial of a give product.
 *
 * @author sergiopereira
 */
public class ProductRegular extends ProductBase {

    protected String mName;
    protected String mBrand;
    protected String mImageUrl;
    private String mCategories;
    protected boolean isNew;
    protected double mAvgRating;
    protected int mTotalReviews;
    protected int mTotalRatings;
    private int mBrandId;

    /**
     * Empty constructor
     */
    public ProductRegular() {
        super();
    }

    /*
     * ############ IJSONSerializable ############
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        // Mandatory
        super.initialize(jsonObject);

        return initializeProductRegular(jsonObject);
    }

    protected final boolean initializeProductRegular(JSONObject jsonObject) throws JSONException {
        // Mandatory
        mName = jsonObject.getString(RestConstants.JSON_NAME_TAG);
        mBrand = jsonObject.getString(RestConstants.JSON_BRAND_TAG);
        mBrandId = jsonObject.optInt(RestConstants.JSON_BRAND_ID_TAG, 0);
        // Optional TODO FIX THIS
        mImageUrl = jsonObject.optString(RestConstants.JSON_IMAGE_TAG);
        if(TextUtils.isEmpty(mImageUrl)) {
            mImageUrl = jsonObject.optString(RestConstants.JSON_IMAGE_URL_TAG);
        }
        // Is new
        isNew = jsonObject.optBoolean(RestConstants.JSON_IS_NEW_TAG);
        // Wish List flag
        if (jsonObject.optBoolean(RestConstants.JSON_IS_WISH_LIST_TAG)) {
            WishListCache.add(mSku);
        }
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

    public int getBrandId() {
        return mBrandId;
    }

    public String getImageUrl() {
        return mImageUrl;
    }

    public boolean isNew() {
        return isNew;
    }

    public boolean isWishList() {
        return WishListCache.has(mSku);
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

    public String getCategoryId() {
        if(TextUtils.isNotEmpty(mCategories)){
            if(mCategories.contains(",")){
                int startPos = mCategories.indexOf(",");
                if(startPos != -1){
                    return mCategories.substring(0,startPos);
                }
            } else {
                return mCategories;
            }

        }
        return "";
    }


    /*
	 * ############ PARCELABLE ############
	 */

    protected ProductRegular(Parcel in) {
        super(in);
        mName = in.readString();
        mBrand = in.readString();
        mBrandId = in.readInt();
        mImageUrl = in.readString();
        mCategories = in.readString();
        isNew = in.readByte() != 0x00;
        mAvgRating = in.readDouble();
        mTotalReviews = in.readInt();
        mTotalRatings = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mName);
        dest.writeString(mBrand);
        dest.writeInt(mBrandId);
        dest.writeString(mImageUrl);
        dest.writeString(mCategories);
        dest.writeByte((byte) (isNew ? 0x01 : 0x00));
        dest.writeDouble(mAvgRating);
        dest.writeInt(mTotalReviews);
        dest.writeInt(mTotalRatings);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @SuppressWarnings("unused")
    public static final Creator<ProductRegular> CREATOR = new Creator<ProductRegular>() {
        @Override
        public ProductRegular createFromParcel(Parcel in) {
            return new ProductRegular(in);
        }

        @Override
        public ProductRegular[] newArray(int size) {
            return new ProductRegular[size];
        }
    };

}
