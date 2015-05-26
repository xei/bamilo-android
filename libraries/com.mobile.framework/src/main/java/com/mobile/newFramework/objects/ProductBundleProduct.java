package com.mobile.newFramework.objects;


import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.objects.BaseProduct;
import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.CurrencyFormatter;
import com.mobile.framework.utils.LogTagHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class that manages the full representation of a given product bundle.
 *
 * @author Paulo Carvalho
 *
 */
public class ProductBundleProduct extends BaseProduct implements IJSONSerializable, Parcelable {

    private static final String TAG = LogTagHelper.create(ProductBundleProduct.class);

    private String bundleProductMaxPrice;
    private double bundleProductMaxPriceDouble;
    private double bundleProductMaxPriceConverted;
    private String bundleProductImage;
    private int bundleProductLeaderPos;
    private String bundleProductMaxSpecialPrice;
    private double bundleProductMaxSpecialPriceDouble;
    private double bundleProductMaxSpecialPriceConverted;
    private String bundleProductSavingPercentage;
    private ArrayList<ProductBundleSimple> bundleSimples;
    private boolean isChecked = true;
    private int simpleSelectedPos;

    /**
     * Complete product bundle empty constructor.
     */
    public ProductBundleProduct() {
        super();
        bundleProductMaxPrice = "";
        bundleProductMaxPriceDouble = 0.0;
        bundleProductMaxPriceConverted = 0.0;
        bundleProductImage = "";
        bundleProductLeaderPos = 0;
        bundleProductMaxSpecialPrice = "";
        bundleProductMaxSpecialPriceDouble = 0.0;
        bundleProductMaxSpecialPriceConverted = 0.0;
        bundleProductSavingPercentage = "";
        isChecked = true;
        simpleSelectedPos = 0;
        bundleSimples = new ArrayList<ProductBundleSimple>();
    }

    public ProductBundleProduct(JSONObject jsonObject) {
        this();
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

            sku = jsonObject.getString(RestConstants.JSON_SKU_TAG);
            name = jsonObject.getString(RestConstants.JSON_NAME_TAG);
            brand = jsonObject.getString(RestConstants.JSON_BRAND_TAG);
            String maxPriceJSON = jsonObject.optString(RestConstants.JSON_MAX_PRICE_TAG,"");

            if (!"".equals(maxPriceJSON)) {
                if (!CurrencyFormatter.isNumber(maxPriceJSON)) {
                    throw new JSONException("Price is not a number!");
                }
                bundleProductMaxPriceDouble = Double.parseDouble(maxPriceJSON);
//                bundleProductMaxPrice = CurrencyFormatter.formatCurrency(maxPriceJSON); TODO
            }

            bundleProductMaxPriceConverted = jsonObject.getDouble(RestConstants.JSON_MAX_PRICE_CONVERTED_TAG);
            bundleProductImage = jsonObject.getString(RestConstants.JSON_IMAGE_TAG);
            String priceJSON = jsonObject.optString(RestConstants.JSON_PRICE_TAG, "");
            if (!"".equals(priceJSON)) {
                if (!CurrencyFormatter.isNumber(priceJSON)) {
                    throw new JSONException("Price is not a number!");
                }
                priceDouble = Double.parseDouble(priceJSON);
//                price = CurrencyFormatter.formatCurrency(priceJSON); TODO
            }

            priceConverted = jsonObject.getDouble(RestConstants.JSON_PRICE_CONVERTED_TAG);
            bundleProductLeaderPos = jsonObject.getInt(RestConstants.JSON_BUNDLE_PRODUCT_LEADER_POS);

            String maxSpecialPriceJSON = jsonObject.optString(RestConstants.JSON_MAX_SPECIAL_PRICE_TAG, "");

            if (!"".equals(maxSpecialPriceJSON)) { //TODO TextUtils.isEmpty

                if (!CurrencyFormatter.isNumber(maxSpecialPriceJSON)) {
                    throw new JSONException("Price is not a number!");
                }
                bundleProductMaxSpecialPriceDouble = Double.parseDouble(maxSpecialPriceJSON);
//                bundleProductMaxSpecialPrice = CurrencyFormatter.formatCurrency(maxSpecialPriceJSON); TODO
            }

            bundleProductMaxSpecialPriceConverted = jsonObject.optDouble(RestConstants.JSON_MAX_SPECIAL_PRICE_CONVERTED_TAG);

            String specialPriceJSON = jsonObject.optString(RestConstants.JSON_SPECIAL_PRICE_TAG, "");

            if (!"".equals(specialPriceJSON)) { //TODO TextUtils.isEmpty
                if (!CurrencyFormatter.isNumber(specialPriceJSON)) {
                    throw new JSONException("Price is not a number!");
                }
                specialPriceDouble = Double.parseDouble(specialPriceJSON);
//                specialPrice = CurrencyFormatter.formatCurrency(specialPriceJSON); TODO

            }

            specialPriceConverted = jsonObject.optDouble(RestConstants.JSON_SPECIAL_PRICE_CONVERTED_TAG);
            bundleProductSavingPercentage = jsonObject.optString(RestConstants.JSON_MAX_SAVING_PERCENTAGE_TAG);

            JSONArray productsSimpleArray = jsonObject.optJSONArray(RestConstants.JSON_SIMPLES_TAG);

            if (productsSimpleArray != null && productsSimpleArray.length() > 0) {
                for (int i = 0; i < productsSimpleArray.length(); i++) {

                    JSONObject simpleJson = productsSimpleArray.getJSONObject(i);
                    ProductBundleSimple bundleSimpleProduct = new ProductBundleSimple(bundleProductLeaderPos, simpleJson);

                    bundleSimples.add(bundleSimpleProduct);
                }
            }


        } catch (JSONException e) {

//            Log.e(TAG, "Error initializing the complete product", e);
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
    public RequiredJson getRequiredJson() {
        return null;
    }

    public String getBundleProductMaxPrice() {
        return bundleProductMaxPrice;
    }

    public void setBundleProductMaxPrice(String bundleProductMaxPrice) {
        this.bundleProductMaxPrice = bundleProductMaxPrice;
    }

    public double getBundleProductMaxPriceDouble() {
        return bundleProductMaxPriceDouble;
    }

    public void setBundleProductMaxPriceDouble(double bundleProductMaxPriceDouble) {
        this.bundleProductMaxPriceDouble = bundleProductMaxPriceDouble;
    }

    public double getBundleProductMaxPriceConverted() {
        return bundleProductMaxPriceConverted;
    }

    public void setBundleProductMaxPriceConverted(double bundleProductMaxPriceConverted) {
        this.bundleProductMaxPriceConverted = bundleProductMaxPriceConverted;
    }

    public String getBundleProductImage() {
        return bundleProductImage;
    }

    public void setBundleProductImage(String bundleProductImage) {
        this.bundleProductImage = bundleProductImage;
    }

    public int getBundleProductLeaderPos() {
        return bundleProductLeaderPos;
    }

    public void setBundleProductLeaderPos(int bundleProductLeaderPos) {
        this.bundleProductLeaderPos = bundleProductLeaderPos;
    }

    public String getBundleProductMaxSpecialPrice() {
        return bundleProductMaxSpecialPrice;
    }

    public void setBundleProductMaxSpecialPrice(String bundleProductMaxSpecialPrice) {
        this.bundleProductMaxSpecialPrice = bundleProductMaxSpecialPrice;
    }

    public double getBundleProductMaxSpecialPriceDouble() {
        return bundleProductMaxSpecialPriceDouble;
    }

    public void setBundleProductMaxSpecialPriceDouble(double bundleProductMaxSpecialPriceDouble) {
        this.bundleProductMaxSpecialPriceDouble = bundleProductMaxSpecialPriceDouble;
    }

    public double getBundleProductMaxSpecialPriceConverted() {
        return bundleProductMaxSpecialPriceConverted;
    }

    public void setBundleProductMaxSpecialPriceConverted(double bundleProductMaxSpecialPriceConverted) {
        this.bundleProductMaxSpecialPriceConverted = bundleProductMaxSpecialPriceConverted;
    }

    public String getBundleProductSavingPercentage() {
        return bundleProductSavingPercentage;
    }

    public void setBundleProductSavingPercentage(String bundleProductSavingPercentage) {
        this.bundleProductSavingPercentage = bundleProductSavingPercentage;
    }

    public boolean hasDiscount() {
        return specialPriceDouble != 0.0 && specialPriceDouble != (priceDouble);
    }

    /**
     * Return the paid price for tracking.
     *
     * @return double
     * @author sergiopereira
     */
    public double getPriceForTracking() {

        return specialPriceConverted > 0.0 ? specialPriceConverted : priceConverted;
    }

    public ArrayList<ProductBundleSimple> getBundleSimples() {
        return bundleSimples;
    }

    public void setBundleSimples(ArrayList<ProductBundleSimple> bundleSimples) {
        this.bundleSimples = bundleSimples;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean isChecked) {
        this.isChecked = isChecked;
    }

    public int getSimpleSelectedPos() {
        return simpleSelectedPos;
    }

    public void setSimpleSelectedPos(int simpleSelectedPos) {
        this.simpleSelectedPos = simpleSelectedPos;
    }


    /*
     * ############ PARCELABLE ############
     */

    protected ProductBundleProduct(Parcel in) {
        sku = in.readString();
        name = in.readString();
        brand = in.readString();
        bundleProductMaxPrice = in.readString();
        bundleProductMaxPriceDouble = in.readDouble();
        bundleProductMaxPriceConverted = in.readDouble();
        bundleProductImage = in.readString();
        price = in.readString();
        priceDouble = in.readDouble();
        priceConverted = in.readDouble();
        bundleProductLeaderPos = in.readInt();
        bundleProductMaxSpecialPrice = in.readString();
        bundleProductMaxSpecialPriceDouble = in.readDouble();
        bundleProductMaxSpecialPriceConverted = in.readDouble();
        specialPrice = in.readString();
        specialPriceDouble = in.readDouble();
        specialPriceConverted = in.readDouble();
        bundleProductSavingPercentage = in.readString();
        if (in.readByte() == 0x01) {
            bundleSimples = new ArrayList<ProductBundleSimple>();
            in.readList(bundleSimples, ProductBundleSimple.class.getClassLoader());
        } else {
            bundleSimples = null;
        }
        isChecked = in.readByte() == 1;
        simpleSelectedPos = in.readInt();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(sku);
        dest.writeString(name);
        dest.writeString(brand);
        dest.writeString(bundleProductMaxPrice);
        dest.writeDouble(bundleProductMaxPriceDouble);
        dest.writeDouble(bundleProductMaxPriceConverted);
        dest.writeString(bundleProductImage);
        dest.writeString(price);
        dest.writeDouble(priceDouble);
        dest.writeDouble(priceConverted);
        dest.writeInt(bundleProductLeaderPos);
        dest.writeString(bundleProductMaxSpecialPrice);
        dest.writeDouble(bundleProductMaxSpecialPriceDouble);
        dest.writeDouble(bundleProductMaxSpecialPriceConverted);
        dest.writeString(specialPrice);
        dest.writeDouble(specialPriceDouble);
        dest.writeDouble(specialPriceConverted);
        dest.writeString(bundleProductSavingPercentage);
        if (bundleSimples == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(bundleSimples);
        }
        dest.writeByte((byte) (isChecked ? 1 : 0));
        dest.writeInt(simpleSelectedPos);

    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ProductBundleProduct> CREATOR = new Parcelable.Creator<ProductBundleProduct>() {
        @Override
        public ProductBundleProduct createFromParcel(Parcel in) {
            return new ProductBundleProduct(in);
        }

        @Override
        public ProductBundleProduct[] newArray(int size) {
            return new ProductBundleProduct[size];
        }
    };

}

