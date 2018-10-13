package com.bamilo.android.framework.service.objects.cart;

import android.os.Parcel;
import android.os.Parcelable;

import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.objects.product.pojo.ProductRegular;
import com.bamilo.android.framework.service.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Shopping Cart Item used when an item is added to the shopping cart
 *
 * @author GuilhermeSilva
 * @modified Paulo Carvalho
 *
 */
public class PurchaseCartItem extends ProductRegular {

    private String mSimpleSku;
    private int mQuantity;
    private int mMaxQuantity;
    private String mVariationValue;
    private String mVariationName;

    /**
     * Empty constructor
     */
    public PurchaseCartItem() {
        // ...
    }

    public PurchaseCartItem(JSONObject jsonObject) {
        initialize(jsonObject);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
     * )
     */
    @Override
    public boolean initialize(JSONObject jsonObject) {
        try {
            super.initialize(jsonObject);
            mSimpleSku = jsonObject.getString(RestConstants.SIMPLE_SKU);
            mQuantity = jsonObject.getInt(RestConstants.QUANTITY);
            mMaxQuantity = jsonObject.getInt(RestConstants.MAX_QUANTITY);
            mVariationName = jsonObject.optString(RestConstants.VARIATION_NAME);
            mVariationValue = jsonObject.optString(RestConstants.VARIATION);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     *
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.METADATA;
    }

    public String getConfigSimpleSKU() {
        return mSimpleSku;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public void setQuantity(int mQuantity) {
        this.mQuantity = mQuantity;
    }

    public int getMaxQuantity() {
        return mMaxQuantity;
    }

    public String getVariationValue() {
        return mVariationValue;
    }

    public String getVariationName() {
        return mVariationName;
    }

    /*
     * ########### Parcelable ###########
     */

	/*
     * (non-Javadoc)
	 *
	 * @see android.os.Parcelable#describeContents()
	 */
    @Override
    public int describeContents() {
        return 0;
    }

    /*
     * (non-Javadoc)
     *
     * @see android.os.Parcelable#writeToParcel(android.os.Parcel, int)
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest,flags);
        dest.writeString(mSimpleSku);
        dest.writeInt(mQuantity);
        dest.writeInt(mMaxQuantity);
        dest.writeString(mVariationValue);
    }


    /**
     * Parcel constructor
     */
    private PurchaseCartItem(Parcel in) {
        super(in);
        mSimpleSku = in.readString();
        mQuantity = in.readInt();
        mMaxQuantity = in.readInt();
        mVariationValue = in.readString();
    }

    /**
     * Create parcelable
     */
    public static final Parcelable.Creator<PurchaseCartItem> CREATOR = new Parcelable.Creator<PurchaseCartItem>() {
        public PurchaseCartItem createFromParcel(Parcel in) {
            return new PurchaseCartItem(in);
        }

        public PurchaseCartItem[] newArray(int size) {
            return new PurchaseCartItem[size];
        }
    };

}