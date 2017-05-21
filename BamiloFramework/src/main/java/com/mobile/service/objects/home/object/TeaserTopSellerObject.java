package com.mobile.service.objects.home.object;

import android.os.Parcel;

import com.mobile.service.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class used to represent a top seller teaser item.
 * @author spereira
 */
public class TeaserTopSellerObject extends BaseTeaserObject {

    private String mBrand;
    private double mPrice;
    private double mPriceConverted;
    private double mSpecialPrice;
    private double mSpecialPriceConverted;
    private String mRichRelevanceClickHash;
    /**
     * Constructor
     */
    public TeaserTopSellerObject(int teaserTypeId) {
        super(teaserTypeId);
    }

    /*
     * ########## GETTERS ##########
     */

    public String getBrand() {
        return mBrand;
    }

    public String getRichRelevanceClickHash() {
        return mRichRelevanceClickHash;
    }
    
    public double getPrice() {
        return mPrice;
    }

    public double getSpecialPrice() {
        return mSpecialPrice;
    }

    public boolean hasSpecialPrice() {
        return !Double.isNaN(mSpecialPrice);
    }

    /*
     * ########## JSON ##########
     */

    /**
     * Initialize
     * @param jsonObject JSONObject containing the parameters of the object
     * @throws JSONException
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        super.initialize(jsonObject);
        // Get name
        mTitle = jsonObject.optString(RestConstants.NAME);
        // Get brand
        mBrand = jsonObject.getString(RestConstants.BRAND);
        // Get image
        mImageTablet = mImagePhone = jsonObject.getString(RestConstants.IMAGE);
        // Get Rich Relevance hash
        mRichRelevanceClickHash = jsonObject.optString(RestConstants.CLICK_REQUEST);
        // Get price
        mPrice = jsonObject.getDouble(RestConstants.PRICE);
        // Get price converted
        mPriceConverted = jsonObject.getDouble(RestConstants.PRICE_CONVERTED);
        // Get special price
        mSpecialPrice = jsonObject.optDouble(RestConstants.SPECIAL_PRICE);
        // Get special price converted
        mSpecialPriceConverted = jsonObject.optDouble(RestConstants.SPECIAL_PRICE_CONVERTED);
        return true;
    }

    /**
     *
     */
    @Override
    public JSONObject toJSON() {
        return null;
    }

    /*
     * ########## PARCELABLE ##########
     */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.mBrand);
        dest.writeDouble(this.mPrice);
        dest.writeDouble(this.mPriceConverted);
        dest.writeDouble(this.mSpecialPrice);
        dest.writeDouble(this.mSpecialPriceConverted);
        dest.writeString(this.mRichRelevanceClickHash);
    }

    private TeaserTopSellerObject(Parcel in) {
        super(in);
        this.mBrand = in.readString();
        this.mPrice = in.readDouble();
        this.mPriceConverted = in.readDouble();
        this.mSpecialPrice = in.readDouble();
        this.mSpecialPriceConverted = in.readDouble();
        this.mRichRelevanceClickHash = in.readString();
    }

    public static final Creator<TeaserTopSellerObject> CREATOR = new Creator<TeaserTopSellerObject>() {
        public TeaserTopSellerObject createFromParcel(Parcel source) {
            return new TeaserTopSellerObject(source);
        }

        public TeaserTopSellerObject[] newArray(int size) {
            return new TeaserTopSellerObject[size];
        }
    };
}
