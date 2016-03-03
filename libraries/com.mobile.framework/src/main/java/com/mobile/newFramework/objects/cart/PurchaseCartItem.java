package com.mobile.newFramework.objects.cart;

import android.os.Parcel;

import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.product.pojo.ProductRegular;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.ImageResolutionHelper;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;

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
    private long quantity;
    private int maxQuantity;
    private String variation;
    private String price;
    private String specialPrice;

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
        Print.d("ON INITIALIZE");

        try {
            super.initialize(jsonObject);
            mImageUrl = getImageUrl(jsonObject.getString(RestConstants.IMAGE));
            mSimpleSku = jsonObject.getString(RestConstants.SIMPLE_SKU);
            quantity = jsonObject.getLong(RestConstants.QUANTITY);
            maxQuantity = jsonObject.getInt(RestConstants.MAX_QUANTITY);
            variation = jsonObject.optString(RestConstants.VARIATION);
            // Fix NAFAMZ-7848
            // Throw JSONException if JSON_PRICE_TAG is not present
            String priceJSON = jsonObject.getString(RestConstants.PRICE);
            if (CurrencyFormatter.isNumber(priceJSON)) {
                mPrice = jsonObject.getDouble(RestConstants.PRICE);
                price = priceJSON;
            } else {
                // throw new JSONException("Price is not a number!");
                Print.d("WARNING: Price is not a number!");
                price = "";
            }

            mPriceConverted = jsonObject.optDouble(RestConstants.PRICE_CONVERTED, 0d);

            // Fix NAFAMZ-7848
            String specialPriceJSON = jsonObject.optString(RestConstants.SPECIAL_PRICE);
            if (CurrencyFormatter.isNumber(specialPriceJSON)) {
                mSpecialPrice = jsonObject.getDouble(RestConstants.SPECIAL_PRICE);
                specialPrice = specialPriceJSON;
            } else {
                mSpecialPrice = mPrice;
                specialPrice = price;
            }

            mSpecialPriceConverted = jsonObject.optDouble(RestConstants.SPECIAL_PRICE_CONVERTED, 0d);

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

    /**
     * @return The configuration/variant SKU of the product
     */
    public String getConfigSimpleSKU() {
        return mSimpleSku;
    }

    /**
     * @return the quantity
     */
    public long getQuantity() {
        return quantity;
    }

    /*
     * @param quantity of the product
     */
    public void setQuantity(long quantity) {
        this.quantity = quantity;
    }

    /**
     * @return the maxQuantity
     */
    public int getMaxQuantity() {
        return maxQuantity;
    }

    /**
     * @return the special price
     */
    public String getSpecialPriceString() {
        return specialPrice;
    }

    /**
     * @return the price
     */
    public String getPriceString() {
        return price;
    }

    public String getVariation() {
        return variation;
    }

    public void setVariation(String variation) {
        this.variation = variation;
    }

    private String getImageUrl(String url) {
        String modUrl = ImageResolutionHelper.replaceResolution(url);
        if (modUrl != null)
            return modUrl;
        return url;
    }

    /**
     * Return the price or special price used for tracking
     *
     * @author sergiopereira
     */
    public double getPriceForTracking() {
        return mSpecialPriceConverted > 0 ? mSpecialPriceConverted : mPriceConverted;
    }

    /**
     * Validate special price.
     *
     * @return true or false
     * @author sergiopereira
     */
    public boolean hasDiscount() {
        return mSpecialPriceConverted > 0;
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
        dest.writeLong(quantity);
        dest.writeInt(maxQuantity);
        dest.writeString(specialPrice);
        dest.writeString(price);
        dest.writeString(variation);
        dest.writeDouble(mSpecialPriceConverted);
    }


    /**
     * Parcel constructor
     */
    private PurchaseCartItem(Parcel in) {
        super(in);
        mSimpleSku = in.readString();
        quantity = in.readLong();
        maxQuantity = in.readInt();
        specialPrice = in.readString();
        price = in.readString();
        variation = in.readString();
        mSpecialPriceConverted = in.readDouble();
    }

    /**
     * Create parcelable
     */
    public static final Creator<PurchaseCartItem> CREATOR = new Creator<PurchaseCartItem>() {
        public PurchaseCartItem createFromParcel(Parcel in) {
            return new PurchaseCartItem(in);
        }

        public PurchaseCartItem[] newArray(int size) {
            return new PurchaseCartItem[size];
        }
    };

}