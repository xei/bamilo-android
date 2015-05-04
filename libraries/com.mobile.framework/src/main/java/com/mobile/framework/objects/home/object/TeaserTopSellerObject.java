package com.mobile.framework.objects.home.object;

import android.os.Parcel;

import com.mobile.framework.rest.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class used to represent a top seller teaser item.
 * @author spereira
 */
public class TeaserTopSellerObject extends BaseTeaserObject {

    private String mSku;
    private String mBrand;
    private int mMaxSavingPercentage;
    private double mPrice;
    private double mPriceConverted;
    private double mSpecialPrice;
    private double mSpecialPriceConverted;

    /**
     * Constructor
     */
    public TeaserTopSellerObject() {
        //...
    }

    /*
     * ########## GETTERS ##########
     */

    public String getSku() {
        return mSku;
    }

    public String getBrand() {
        return mBrand;
    }

    public int getMaxSavingPercentage() {
        return mMaxSavingPercentage;
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
        // Get sku
        mSku = jsonObject.getString(RestConstants.JSON_SKU_TAG);
        // Get title
        mTitle = jsonObject.optString(RestConstants.JSON_NAME_TAG);
        // Get brand
        mBrand = jsonObject.getString(RestConstants.JSON_BRAND_TAG);
        // Get url
        mUrl = jsonObject.getString(RestConstants.JSON_URL_TAG);
        // Get image
        mImageTablet = mImagePhone = jsonObject.getString(RestConstants.JSON_IMAGE_TAG);
        // Get price
        mPrice = jsonObject.getDouble(RestConstants.JSON_PRICE_TAG);
        // Get price converted
        mPriceConverted = jsonObject.getDouble(RestConstants.JSON_PRICE_CONVERTED_TAG);
        // Get special price
        mSpecialPrice = jsonObject.optDouble(RestConstants.JSON_SPECIAL_PRICE_TAG);
        // Get special price converted
        mSpecialPriceConverted = jsonObject.optDouble(RestConstants.JSON_PRICE_CONVERTED_TAG);
        // Get discount percentage
        mMaxSavingPercentage = jsonObject.optInt(RestConstants.JSON_MAX_SAVING_PERCENTAGE_TAG);
        // Get target type
        mTargetType = jsonObject.optString(RestConstants.JSON_TARGET_TYPE_TAG);
        return true;
    }

    /**
     * TODO
     *
     * @return
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
        dest.writeString(this.mSku);
        dest.writeString(this.mBrand);
        dest.writeInt(this.mMaxSavingPercentage);
        dest.writeDouble(this.mPrice);
        dest.writeDouble(this.mPriceConverted);
        dest.writeDouble(this.mSpecialPrice);
        dest.writeDouble(this.mSpecialPriceConverted);
    }

    private TeaserTopSellerObject(Parcel in) {
        super(in);
        this.mSku = in.readString();
        this.mBrand = in.readString();
        this.mMaxSavingPercentage = in.readInt();
        this.mPrice = in.readDouble();
        this.mPriceConverted = in.readDouble();
        this.mSpecialPrice = in.readDouble();
        this.mSpecialPriceConverted = in.readDouble();
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
