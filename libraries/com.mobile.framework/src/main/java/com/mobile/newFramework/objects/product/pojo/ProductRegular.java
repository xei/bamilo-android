package com.mobile.newFramework.objects.product.pojo;

import android.os.Parcel;

import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.product.Brand;
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
    protected String mImageUrl;
    private String mCategories;
    protected boolean isNew;
    protected double mAvgRating;
    protected int mTotalReviews;
    protected int mTotalRatings;
    protected String mTarget;
    protected String mRichRelevanceClickHash;
    protected Brand mBrand;
    private String mCategoryUrlKey;
    private String mCategoryName;

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
        mName = jsonObject.optString(RestConstants.NAME);
        // TODO: Remove this line when all app parses brand_entity object. For now happens just in PDV
        String brand = jsonObject.optString(RestConstants.BRAND);
        int brandId = jsonObject.optInt(RestConstants.BRAND_ID);
        String brandKey = jsonObject.optString(RestConstants.BRAND_URL_KEY);
        mBrand = new Brand(brand, brandId, brandKey);
        JSONObject brandObject = jsonObject.optJSONObject(RestConstants.BRAND_ENTITY);
        if(brandObject != null) {
            mBrand.initialize(brandObject);
        }
        // Category
        mCategoryUrlKey = jsonObject.optString(RestConstants.CATEGORY_URL_KEY);
        mCategoryName = jsonObject.optString(RestConstants.CATEGORY_NAME);
        // Optional
        mImageUrl = jsonObject.optString(RestConstants.IMAGE);
        // Is new
        isNew = jsonObject.optBoolean(RestConstants.IS_NEW);
        // Wish List flag
        if (jsonObject.optBoolean(RestConstants.IS_WISH_LIST)) {
            WishListCache.add(mSku);
        }
        mCategories = jsonObject.optString(RestConstants.CATEGORIES);
        // Rating
        JSONObject ratings = jsonObject.optJSONObject(RestConstants.RATING_REVIEWS_SUMMARY);
        if (ratings != null) {
            mAvgRating = ratings.optDouble(RestConstants.AVERAGE);
            mTotalRatings = ratings.optInt(RestConstants.RATINGS_TOTAL);
            mTotalReviews = ratings.optInt(RestConstants.REVIEWS_TOTAL);
        }
        mTarget = jsonObject.optString(RestConstants.TARGET);
        // Click Request
        mRichRelevanceClickHash = jsonObject.optString(RestConstants.CLICK_REQUEST);
        return true;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.NONE;
    }

    /*
     * ############ GETTERS ############
	 */

    public String getName() {
        return mName;
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
            String[] categories = mCategories.split(",");
            return categories[0];
        }
        return "";
    }

    public String getTarget() {
        return mTarget;
    }

    public String getRichRelevanceClickHash() {
        return mRichRelevanceClickHash;
    }

    public String getBrandKey() {
        return mBrand.getUrlKey();
    }

    public Brand getBrand(){ return mBrand;}

    public String getBrandName(){ return mBrand.getName();}

    public String getCategoryKey() {
        return mCategoryUrlKey;
    }

    public String getCategoryName(){ return mCategoryName;}

    /*
	 * ############ PARCELABLE ############
	 */

    protected ProductRegular(Parcel in) {
        super(in);
        mName = in.readString();
        mImageUrl = in.readString();
        mCategories = in.readString();
        isNew = in.readByte() != 0x00;
        mAvgRating = in.readDouble();
        mTotalReviews = in.readInt();
        mTotalRatings = in.readInt();
        mTarget = in.readString();
        mRichRelevanceClickHash = in.readString();
        mBrand = in.readParcelable(Brand.class.getClassLoader());
        mCategoryName = in.readString();
        mCategoryUrlKey = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mName);
        dest.writeString(mImageUrl);
        dest.writeString(mCategories);
        dest.writeByte((byte) (isNew ? 0x01 : 0x00));
        dest.writeDouble(mAvgRating);
        dest.writeInt(mTotalReviews);
        dest.writeInt(mTotalRatings);
        dest.writeString(mTarget);
        dest.writeString(mRichRelevanceClickHash);
        dest.writeParcelable(mBrand,flags);
        dest.writeString(mCategoryName);
        dest.writeString(mCategoryUrlKey);
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
