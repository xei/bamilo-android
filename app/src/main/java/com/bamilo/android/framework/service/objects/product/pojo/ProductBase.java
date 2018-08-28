package com.bamilo.android.framework.service.objects.product.pojo;

import android.os.Parcel;
import android.os.Parcelable;

import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.pojo.RestConstants;

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
    private boolean shopFirst;
    private String shopFirstOverlay;

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
        // Mandatory
        mSku = jsonObject.optString(RestConstants.SKU);
        mPrice = jsonObject.getDouble(RestConstants.PRICE);
        // Optional
        mPriceConverted = jsonObject.optDouble(RestConstants.PRICE_CONVERTED);
        mSpecialPrice = jsonObject.optDouble(RestConstants.SPECIAL_PRICE);
        mSpecialPriceConverted = jsonObject.optDouble(RestConstants.SPECIAL_PRICE_CONVERTED);
        mMaxSavingPercentage = jsonObject.optInt(RestConstants.MAX_SAVING_PERCENTAGE);
        mPriceRange = jsonObject.optString(RestConstants.PRICE_RANGE);
        shopFirst = jsonObject.optBoolean(RestConstants.SHOP_FIRST);
        shopFirstOverlay = jsonObject.optString(RestConstants.SHOP_FIRST_OVERLAY);
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
    public int getRequiredJson() {
        return RequiredJson.NONE;
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

    public String getShopFirstOverlay() {
        return shopFirstOverlay;
    }

    public boolean isShopFirst() {
        return shopFirst;
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
        dest.writeByte((byte) (shopFirst ? 0x01 : 0x00));
        dest.writeString(shopFirstOverlay);
    }

    protected ProductBase(Parcel in) {
        mSku = in.readString();
        mPrice = in.readDouble();
        mPriceConverted = in.readDouble();
        mSpecialPrice = in.readDouble();
        mSpecialPriceConverted = in.readDouble();
        mMaxSavingPercentage = in.readInt();
        mPriceRange = in.readString();
        shopFirst = in.readByte() != 0x00;
        shopFirstOverlay = in.readString();

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
