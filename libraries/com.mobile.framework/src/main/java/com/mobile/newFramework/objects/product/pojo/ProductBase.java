package com.mobile.newFramework.objects.product.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Defines a base of a give product.
 *
 * @author sergiopereira
 */
public class ProductBase implements Parcelable, IJSONSerializable {

    protected static final String TAG = ProductBase.class.getSimpleName();

    protected String mSku;
    protected double mPrice;
    protected double mPriceConverted;
    protected double mSpecialPrice;
    protected double mSpecialPriceConverted;
    protected int mMaxSavingPercentage;
    private String mPriceRange;

    /**
     * Empty constructor
     */
    public ProductBase() {
        super();
    }

    /* (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        return initializeProductBase(jsonObject);
    }

    protected final boolean initializeProductBase(JSONObject jsonObject) throws JSONException {
        // Mandatory
        mSku = jsonObject.getString(RestConstants.SKU);
        mPrice = jsonObject.getDouble(RestConstants.JSON_PRICE_TAG);
        // Optional
        mPriceConverted = jsonObject.optDouble(RestConstants.JSON_PRICE_CONVERTED_TAG);
        mSpecialPrice = jsonObject.optDouble(RestConstants.JSON_SPECIAL_PRICE_TAG);
        mSpecialPriceConverted = jsonObject.optDouble(RestConstants.JSON_SPECIAL_PRICE_CONVERTED_TAG);
        mMaxSavingPercentage = jsonObject.optInt(RestConstants.JSON_MAX_SAVING_PERCENTAGE_TAG);
        mPriceRange = jsonObject.optString(RestConstants.PRICE_RANGE);
        return true;
    }

    /* (non-Javadoc)
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
     * ############ GETTERS ############
	 */

    public String getSku() {
        return mSku;
    }

    public double getPrice() {
        return mPrice;
    }

    public double getSpecialPrice() {
        return mSpecialPrice;
    }

    public int getMaxSavingPercentage() {
        return mMaxSavingPercentage;
    }

    public boolean hasDiscount() {
        return mSpecialPrice > 0 && mSpecialPrice != Double.NaN;
    }

    public double getPriceForTracking() {
        return mSpecialPriceConverted > 0 ? mSpecialPriceConverted : mPriceConverted;
    }

    public String getPriceRange() {
        return mPriceRange;
    }

    /*
	 * ############ PARCELABLE ############
	 */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mSku);
        dest.writeDouble(mPrice);
        dest.writeDouble(mPriceConverted);
        dest.writeDouble(mSpecialPrice);
        dest.writeDouble(mSpecialPriceConverted);
        dest.writeInt(mMaxSavingPercentage);
        dest.writeString(mPriceRange);
    }

    protected ProductBase(Parcel in) {
        mSku = in.readString();
        mPrice = in.readDouble();
        mPriceConverted = in.readDouble();
        mSpecialPrice = in.readDouble();
        mSpecialPriceConverted = in.readDouble();
        mMaxSavingPercentage = in.readInt();
        mPriceRange = in.readString();
    }

    public static final Creator<ProductBase> CREATOR = new Creator<ProductBase>() {
        public ProductBase createFromParcel(Parcel in) {
            return new ProductBase(in);
        }

        public ProductBase[] newArray(int size) {
            return new ProductBase[size];
        }
    };

}
