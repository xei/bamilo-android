package com.mobile.newFramework.objects.product;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONObject;

/**
 * Class that represents the Seller of a specific product
 *
 * @author Paulo Carvalho
 *
 */
public class Seller implements IJSONSerializable, Parcelable {

    private String mName;

    private String mUrl;

    private String mSellerId;

    private int mMinDeliveryTime;

    private int mMaxDeliveryTime;

    private int mRatingCount;

    private int mRatingValue;


    /**
     * Seller empty constructor.
     */
    public Seller() {
//        Log.i(TAG, "EMPTY constructor");
        mName = "";
        mUrl = "";
        mMinDeliveryTime = 1;
        mMaxDeliveryTime = 1;
        mRatingCount = 0;
        mRatingValue = 0;
        setSellerId("");
    }

    /**
     * Seller Json constructor.
     */
    public Seller(JSONObject sellerObject) {
        super();
//        Log.i(TAG, "JSON constructor");
        initialize(sellerObject);
    }

    public String getName() {
        return mName;
    }

    public void setName(String mName) {
        this.mName = mName;
    }

    public String getUrl() {
        return mUrl;
    }

    public void setUrl(String mUrl) {
        this.mUrl = mUrl;
    }

    public int getMinDeliveryTime() {
        return mMinDeliveryTime;
    }

    public void setMinDeliveryTime(int mMinDeliveryTime) {
        this.mMinDeliveryTime = mMinDeliveryTime;
    }

    public int getMaxDeliveryTime() {
        return mMaxDeliveryTime;
    }

    public void setMaxDeliveryTime(int mMaxDeliveryTime) {
        this.mMaxDeliveryTime = mMaxDeliveryTime;
    }

    public int getRatingCount() {
        return mRatingCount;
    }

    public void setRatingCount(int mRatingCount) {
        this.mRatingCount = mRatingCount;
    }

    public int getRatingValue() {
        return mRatingValue;
    }

    public void setRatingValue(int mRatingValue) {
        this.mRatingValue = mRatingValue;
    }

    public String getSellerId() {
        return mSellerId;
    }

    public void setSellerId(String mSellerId) {
        this.mSellerId = mSellerId;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        mName = jsonObject.optString(RestConstants.JSON_NAME_TAG);
        mUrl = jsonObject.optString(RestConstants.JSON_URL_TAG);
        mSellerId = jsonObject.optString(RestConstants.JSON_ID_TAG);
        JSONObject reviewObject = jsonObject.optJSONObject(RestConstants.JSON_REVIEWS_TAG);
        if(reviewObject != null){
            mRatingCount = reviewObject.optInt(RestConstants.JSON_TOTAL_TAG);
            mRatingValue = reviewObject.optInt(RestConstants.JSON_RATINGS_AVERAGE_TAG);
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


    /**
     * ########### Parcelable ###########
     * @author sergiopereira
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
        dest.writeString(mName);
        dest.writeString(mUrl);
        dest.writeString(mSellerId);
        dest.writeInt(mMinDeliveryTime);
        dest.writeInt(mMaxDeliveryTime);
        dest.writeInt(mRatingCount);
        dest.writeInt(mRatingValue);
    }

    /**
     * Parcel constructor
     * @param in
     */
    protected Seller(Parcel in) {
        mName = in.readString();
        mUrl = in.readString();
        mSellerId = in.readString();
        mMinDeliveryTime = in.readInt();
        mMaxDeliveryTime = in.readInt();
        mRatingCount = in.readInt();
        mRatingValue = in.readInt();

    }

    /**
     * Create parcelable
     */
    public static final Parcelable.Creator<Seller> CREATOR = new Parcelable.Creator<Seller>() {
        public Seller createFromParcel(Parcel in) {
            return new Seller(in);
        }

        public Seller[] newArray(int size) {
            return new Seller[size];
        }
    };

}
