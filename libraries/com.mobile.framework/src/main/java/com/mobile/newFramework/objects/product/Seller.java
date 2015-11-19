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
    private String mWarranty;
    private int mRatingCount;
    private int mRatingValue;
    private boolean isGlobal;
    private String mDeliveryTime;
    private String mDeliveryCMSInfo;
    private String mDeliveryShippingInfo;
    private String mDeliveryMoreText;
    private String mDeliveryMoreLink;


    /**
     * Seller empty constructor.
     */
    public Seller() {
        // ...
    }

    /**
     * Seller Json constructor.
     */
    public Seller(JSONObject sellerObject) {
        super();
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

    public int getRatingCount() {
        return mRatingCount;
    }

    public int getRatingValue() {
        return mRatingValue;
    }

    public String getWarranty() {
        return mWarranty;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public String getDeliveryTime() {
        return mDeliveryTime;
    }

    public String getDeliveryCMSInfo() {
        return mDeliveryCMSInfo;
    }

    public String getDeliveryShippingInfo() {
        return mDeliveryShippingInfo;
    }

    public String getDeliveryMoreDetailsText() {
        return mDeliveryMoreText;
    }

    /*
         * (non-Javadoc)
         *
         * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
         */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        mName = jsonObject.optString(RestConstants.JSON_NAME_TAG);
        mUrl = jsonObject.optString(RestConstants.URL);
        mWarranty = jsonObject.optString(RestConstants.JSON_WARRANTY_TAG);
        isGlobal = jsonObject.optBoolean(RestConstants.IS_GLOBAL);
        JSONObject reviewObject = jsonObject.optJSONObject(RestConstants.JSON_REVIEWS_TAG);
        if(reviewObject != null){
            mRatingCount = reviewObject.optInt(RestConstants.JSON_TOTAL_TAG);
            mRatingValue = reviewObject.optInt(RestConstants.JSON_RATINGS_AVERAGE_TAG);
        }
        mDeliveryTime = jsonObject.optString(RestConstants.DELIVERY_TIME);
        JSONObject globalObject = jsonObject.optJSONObject(RestConstants.GLOBAL);
        if (globalObject != null) {
            mDeliveryCMSInfo = globalObject.optString(RestConstants.CMS_INFO);
            mDeliveryShippingInfo = globalObject.optString(RestConstants.SHIPPING);
            JSONObject globalLinkObject = globalObject.optJSONObject(RestConstants.LINK);
            if (globalLinkObject != null) {
                mDeliveryMoreText = globalLinkObject.optString(RestConstants.TEXT);
                mDeliveryMoreLink = globalLinkObject.optString(RestConstants.URL);
            }
        }
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
        dest.writeString(mWarranty);
        dest.writeInt(mRatingCount);
        dest.writeInt(mRatingValue);
        dest.writeByte((byte) (isGlobal ? 1 : 0));
        dest.writeString(mDeliveryTime);
        dest.writeString(mDeliveryCMSInfo);
        dest.writeString(mDeliveryShippingInfo);
        dest.writeString(mDeliveryMoreText);
        dest.writeString(mDeliveryMoreLink);
    }

    /**
     * Parcel constructor
     */
    protected Seller(Parcel in) {
        mName = in.readString();
        mUrl = in.readString();
        mWarranty = in.readString();
        mRatingCount = in.readInt();
        mRatingValue = in.readInt();
        isGlobal = in.readByte() == 1;
        mDeliveryTime = in.readString();
        mDeliveryCMSInfo = in.readString();
        mDeliveryShippingInfo = in.readString();
        mDeliveryMoreText = in.readString();
        mDeliveryMoreLink = in.readString();
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
