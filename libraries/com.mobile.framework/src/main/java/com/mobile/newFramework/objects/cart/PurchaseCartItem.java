package com.mobile.newFramework.objects.cart;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.ImageResolutionHelper;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Shopping Cart Item used when an item is added to the shopping cart
 *
 * @author GuilhermeSilva
 * @modified Paulo Carvalho
 *
 * TODO: USE Product Object
 */
public class PurchaseCartItem implements IJSONSerializable, Parcelable {

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
    private String imageUrl;
    private String productUrl;
    private String mSKU;
    private String mSimpleSku;
    private long quantity;
    private int maxQuantity;
    private String configId;
    private String name;
    private String brand;
    private Map<String, String> simpleData;
    private String variation;
    private String price;
    private String specialPrice;
    private double savingPercentage;
    private double priceVal = 0;
    private double specialPriceVal = 0;
    private double mPriceValueConverted = 0;
    private double mSpecialPriceConverted = 0;
    private String mCategoriesIds;
    private String mAttributeSetId;
    private boolean mShopFirst;

    public PurchaseCartItem() {

    }

    public PurchaseCartItem(JSONObject jsonObject) {
        initialize(jsonObject);
    }

    /**
     * Parcel constructor
     */
    private PurchaseCartItem(Parcel in) {
        imageUrl = in.readString();
        productUrl = in.readString();
        mSKU = in.readString();
        mSimpleSku = in.readString();
        quantity = in.readLong();
        maxQuantity = in.readInt();
        configId = in.readString();
        name = in.readString();
        specialPrice = in.readString();
        savingPercentage = in.readDouble();
        price = in.readString();
        simpleData = new HashMap<>();
        in.readMap(simpleData, String.class.getClassLoader());
        variation = in.readString();
        priceVal = in.readDouble();
        specialPriceVal = in.readDouble();
        mPriceValueConverted = in.readDouble();
        mSpecialPriceConverted = in.readDouble();
        mCategoriesIds = in.readString();
        mAttributeSetId = in.readString();
        brand = in.readString();
        mShopFirst = in.readByte() != 0x00;
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
            imageUrl = getImageUrl(jsonObject.getString(RestConstants.IMAGE));
            productUrl = jsonObject.optString(RestConstants.URL);
            mSKU = jsonObject.getString(RestConstants.SKU);
            mSimpleSku = jsonObject.getString(RestConstants.SIMPLE_SKU);
            quantity = jsonObject.getLong(RestConstants.QUANTITY);
            maxQuantity = jsonObject.getInt(RestConstants.MAX_QUANTITY);
            name = jsonObject.getString(RestConstants.NAME);
            brand = jsonObject.getString(RestConstants.BRAND);
            mAttributeSetId = jsonObject.optString(RestConstants.ATTRIBUTE_SET_ID);
            variation = jsonObject.optString(RestConstants.VARIATION);
            mCategoriesIds = jsonObject.optString(RestConstants.ID_CATALOG_CATEGORY);
            // Fix NAFAMZ-7848
            // Throw JSONException if JSON_PRICE_TAG is not present
            String priceJSON = jsonObject.getString(RestConstants.PRICE);
            if (CurrencyFormatter.isNumber(priceJSON)) {
                priceVal = jsonObject.getDouble(RestConstants.PRICE);
                price = priceJSON;
                //price = CurrencyFormatter.formatCurrency(priceJSON);
            } else {
                // throw new JSONException("Price is not a number!");
                Print.d("WARNING: Price is not a number!");
                price = "";
            }

            mPriceValueConverted = jsonObject.optDouble(RestConstants.PRICE_CONVERTED, 0d);

            // Fix NAFAMZ-7848
            String specialPriceJSON = jsonObject.optString(RestConstants.SPECIAL_PRICE);
            if (CurrencyFormatter.isNumber(specialPriceJSON)) {
                specialPriceVal = jsonObject.getDouble(RestConstants.SPECIAL_PRICE);
                specialPrice = specialPriceJSON;
                // specialPrice = CurrencyFormatter.formatCurrency();
            } else {
                specialPriceVal = priceVal;
                specialPrice = price;
            }

            mSpecialPriceConverted = jsonObject.optDouble(RestConstants.SPECIAL_PRICE_CONVERTED, 0d);

            savingPercentage = 100 - specialPriceVal / priceVal * 100;

            mShopFirst = jsonObject.optBoolean(RestConstants.SHOP_FIRST,false);


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
     * @return the imageUrl
     */
    public String getImageUrl() {
        return imageUrl;
    }

    /**
     * @return the productUrl
     */
    public String getProductUrl() {
        return productUrl;
    }

    /**
     * @return The product SKU (short form)
     */
    public String getConfigSKU() {
        return mSKU;
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
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @return the brand
     */
    public String getBrand() {
        return brand;
    }

    /**
     * @return the special price
     */
    public String getSpecialPrice() {
        return specialPrice;
    }

    /**
     * @return the special price
     */
    public Double getSpecialPriceVal() {
        return specialPriceVal;
    }

    /**
     * @return the price
     */
    public String getPrice() {
        return price;
    }

    /**
     * @return the price
     */
    public Double getPriceVal() {
        return priceVal;
    }

    /**
     * @return the savingPercentage
     */
    public double getSavingPercentage() {
        return savingPercentage;
    }

    /**
     * @return the mShopFirst
     */
    public boolean isShopFirst() {
        return mShopFirst;
    }

    /**
     * @return the simpleData
     */
    public Map<String, String> getSimpleData() {
        return simpleData;
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
     * @return category id from the item
     */
    public String getCategoriesIds() {
        return mCategoriesIds;
    }

    /**
     * Return the price or special price used for tracking
     *
     * @author sergiopereira
     */
    public double getPriceForTracking() {
        return mSpecialPriceConverted > 0 ? mSpecialPriceConverted : mPriceValueConverted;
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

    public String getAttributeSetId (){
        return mAttributeSetId;
    }

    /**
     * ########### Parcelable ###########
     *
     * @author sergiopereira
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
        dest.writeString(imageUrl);
        dest.writeString(productUrl);
        dest.writeString(mSKU);
        dest.writeString(mSimpleSku);
        dest.writeLong(quantity);
        dest.writeInt(maxQuantity);
        dest.writeString(configId);
        dest.writeString(name);
        dest.writeString(specialPrice);
        dest.writeDouble(savingPercentage);
        dest.writeString(price);
        dest.writeMap(simpleData);
        dest.writeString(variation);
        dest.writeDouble(priceVal);
        dest.writeDouble(specialPriceVal);
        dest.writeDouble(mPriceValueConverted);
        dest.writeDouble(mSpecialPriceConverted);
        dest.writeString(mCategoriesIds);
        dest.writeString(mAttributeSetId);
        dest.writeString(brand);
        dest.writeByte((byte) (mShopFirst ? 0x01 : 0x00));

    }

}