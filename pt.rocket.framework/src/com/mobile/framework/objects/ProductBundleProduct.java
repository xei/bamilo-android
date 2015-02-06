package com.mobile.framework.objects;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.CurrencyFormatter;
import com.mobile.framework.utils.LogTagHelper;
import android.os.Parcel;
import android.os.Parcelable;
import de.akquinet.android.androlog.Log;

/**
 * Class that manages the full representation of a given product bundle.
 * 
 * @author Paulo Carvalho
 * 
 */
public class ProductBundleProduct implements IJSONSerializable, Parcelable {

    private static final String TAG = LogTagHelper.create(ProductBundleProduct.class);

    private String bundleProductSku;
    private String bundleProductName;
    private String bundleProductBrand;
    private String bundleProductMaxPrice;
    private double bundleProductMaxPriceDouble;
    private double bundleProductMaxPriceConverted;
    private String bundleProductImage;
    private String bundleProductPrice;
    private double bundleProductPriceDouble;
    private double bundleProductPriceConverted;
    private int bundleProductLeaderPos;
    private String bundleProductMaxSpecialPrice;
    private double bundleProductMaxSpecialPriceDouble;
    private double bundleProductMaxSpecialPriceConverted;
    private String bundleProductSpecialPrice;
    private double bundleProductSpecialPriceDouble;
    private double bundleProductSpecialPriceConverted;
    private String bundleProductSavingPercentage;
    private ArrayList<ProductBundleSimple> bundleSimples;
    private boolean isChecked = true;
    private int simpleSelectedPos;

    /**
     * Complete product bundle empty constructor.
     */
    public ProductBundleProduct() {

        bundleProductSku = "";
        bundleProductName = "";
        bundleProductBrand = "";
        bundleProductMaxPrice = "";
        bundleProductMaxPriceDouble = 0.0;
        bundleProductMaxPriceConverted = 0.0;
        bundleProductImage = "";
        bundleProductPrice = "";
        bundleProductPriceDouble = 0.0;
        bundleProductPriceConverted = 0.0;
        bundleProductLeaderPos = 0;
        bundleProductMaxSpecialPrice = "";
        bundleProductMaxSpecialPriceDouble = 0.0;
        bundleProductMaxSpecialPriceConverted = 0.0;
        bundleProductSpecialPrice = "";
        bundleProductSpecialPriceDouble = 0.0;
        bundleProductSpecialPriceConverted = 0.0;
        bundleProductSavingPercentage = "";
        isChecked = true;
        simpleSelectedPos = 0;
        bundleSimples = new ArrayList<ProductBundleSimple>();
    }

    public ProductBundleProduct(JSONObject jsonObject) {

        bundleProductSku = "";
        bundleProductName = "";
        bundleProductBrand = "";
        bundleProductMaxPrice = "";
        bundleProductMaxPriceDouble = 0.0;
        bundleProductMaxPriceConverted = 0.0;
        bundleProductImage = "";
        bundleProductPrice = "";
        bundleProductPriceDouble = 0.0;
        bundleProductPriceConverted = 0.0;
        bundleProductLeaderPos = 0;
        bundleProductMaxSpecialPrice = "";
        bundleProductMaxSpecialPriceDouble = 0.0;
        bundleProductMaxSpecialPriceConverted = 0.0;
        bundleProductSpecialPrice = "";
        bundleProductSpecialPriceDouble = 0.0;
        bundleProductSpecialPriceConverted = 0.0;
        bundleProductSavingPercentage = "";
        isChecked = true;
        simpleSelectedPos = 0;
        bundleSimples = new ArrayList<ProductBundleSimple>();
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

            bundleProductSku = jsonObject.getString(RestConstants.JSON_SKU_TAG);
            bundleProductName = jsonObject.getString(RestConstants.JSON_NAME_TAG);
            bundleProductBrand = jsonObject.getString(RestConstants.JSON_BRAND_TAG);
            String maxPriceJSON = jsonObject.optString(RestConstants.JSON_MAX_PRICE_TAG,"");

            if (!"".equals(maxPriceJSON)) {
                if (!CurrencyFormatter.isNumber(maxPriceJSON)) {
                    throw new JSONException("Price is not a number!");
                }
                bundleProductMaxPriceDouble = Double.parseDouble(maxPriceJSON);
                bundleProductMaxPrice = CurrencyFormatter.formatCurrency(maxPriceJSON);
            }

            bundleProductMaxPriceConverted = jsonObject.getDouble(RestConstants.JSON_MAX_PRICE_CONVERTED_TAG);
            bundleProductImage = jsonObject.getString(RestConstants.JSON_IMAGE_TAG);
            String priceJSON = jsonObject.optString(RestConstants.JSON_PRICE_TAG, "");
            if (!"".equals(priceJSON)) {
                if (!CurrencyFormatter.isNumber(priceJSON)) {
                    throw new JSONException("Price is not a number!");
                }
                bundleProductPriceDouble = Double.parseDouble(priceJSON);
                bundleProductPrice = CurrencyFormatter.formatCurrency(priceJSON);
            }

            bundleProductPriceConverted = jsonObject.getDouble(RestConstants.JSON_PRICE_CONVERTED_TAG);
            bundleProductLeaderPos = jsonObject.getInt(RestConstants.JSON_BUNDLE_PRODUCT_LEADER_POS);

            String maxSpecialPriceJSON = jsonObject.optString(RestConstants.JSON_MAX_SPECIAL_PRICE_TAG, "");

            if (!"".equals(maxSpecialPriceJSON)) {

                if (!CurrencyFormatter.isNumber(maxSpecialPriceJSON)) {
                    throw new JSONException("Price is not a number!");
                }
                bundleProductMaxSpecialPriceDouble = Double.parseDouble(maxSpecialPriceJSON);
                bundleProductMaxSpecialPrice = CurrencyFormatter.formatCurrency(maxSpecialPriceJSON);
            }

            bundleProductMaxSpecialPriceConverted = jsonObject.optDouble(RestConstants.JSON_MAX_SPECIAL_PRICE_CONVERTED_TAG);

            String specialPriceJSON = jsonObject.optString(RestConstants.JSON_SPECIAL_PRICE_TAG, "");

            if (!"".equals(specialPriceJSON)) {
                if (!CurrencyFormatter.isNumber(specialPriceJSON)) {
                    throw new JSONException("Price is not a number!");
                }
                bundleProductSpecialPriceDouble = Double.parseDouble(specialPriceJSON);
                bundleProductSpecialPrice = CurrencyFormatter.formatCurrency(specialPriceJSON);

            }

            bundleProductSpecialPriceConverted = jsonObject.optDouble(RestConstants.JSON_SPECIAL_PRICE_CONVERTED_TAG);
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

            Log.e(TAG, "Error initializing the complete product", e);
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

    public String getBundleProductSku() {
        return bundleProductSku;
    }

    public void setBundleProductSku(String bundleProductSku) {
        this.bundleProductSku = bundleProductSku;
    }

    public String getBundleProductName() {
        return bundleProductName;
    }

    public void setBundleProductName(String bundleProductName) {
        this.bundleProductName = bundleProductName;
    }

    public String getBundleProductBrand() {
        return bundleProductBrand;
    }

    public void setBundleProductBrand(String bundleProductBrand) {
        this.bundleProductBrand = bundleProductBrand;
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

    public String getBundleProductPrice() {
        return bundleProductPrice;
    }

    public void setBundleProductPrice(String bundleProductPrice) {
        this.bundleProductPrice = bundleProductPrice;
    }

    public double getBundleProductPriceDouble() {
        return bundleProductPriceDouble;
    }

    public void setBundleProductPriceDouble(double bundleProductPriceDouble) {
        this.bundleProductPriceDouble = bundleProductPriceDouble;
    }

    public double getBundleProductPriceConverted() {
        return bundleProductPriceConverted;
    }

    public void setBundleProductPriceConverted(double bundleProductPriceConverted) {
        this.bundleProductPriceConverted = bundleProductPriceConverted;
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

    public String getBundleProductSpecialPrice() {
        return bundleProductSpecialPrice;
    }

    public void setBundleProductSpecialPrice(String bundleProductSpecialPrice) {
        this.bundleProductSpecialPrice = bundleProductSpecialPrice;
    }

    public double getBundleProductSpecialPriceDouble() {
        return bundleProductSpecialPriceDouble;
    }

    public void setBundleProductSpecialPriceDouble(double bundleProductSpecialPriceDouble) {
        this.bundleProductSpecialPriceDouble = bundleProductSpecialPriceDouble;
    }

    public double getBundleProductSpecialPriceConverted() {
        return bundleProductSpecialPriceConverted;
    }

    public void setBundleProductSpecialPriceConverted(double bundleProductSpecialPriceConverted) {
        this.bundleProductSpecialPriceConverted = bundleProductSpecialPriceConverted;
    }

    public String getBundleProductSavingPercentage() {
        return bundleProductSavingPercentage;
    }

    public void setBundleProductSavingPercentage(String bundleProductSavingPercentage) {
        this.bundleProductSavingPercentage = bundleProductSavingPercentage;
    }

    public boolean hasDiscount() {
        return bundleProductSpecialPriceDouble != 0.0 && bundleProductSpecialPriceDouble != (bundleProductPriceDouble);
    }

    /**
     * Return the paid price for tracking.
     * 
     * @return double
     * @author sergiopereira
     */
    public double getPriceForTracking() {

        return bundleProductSpecialPriceConverted > 0.0 ? bundleProductSpecialPriceConverted : bundleProductPriceConverted;
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
        bundleProductSku = in.readString();
        bundleProductName = in.readString();
        bundleProductBrand = in.readString();
        bundleProductMaxPrice = in.readString();
        bundleProductMaxPriceDouble = in.readDouble();
        bundleProductMaxPriceConverted = in.readDouble();
        bundleProductImage = in.readString();
        bundleProductPrice = in.readString();
        bundleProductPriceDouble = in.readDouble();
        bundleProductPriceConverted = in.readDouble();
        bundleProductLeaderPos = in.readInt();
        bundleProductMaxSpecialPrice = in.readString();
        bundleProductMaxSpecialPriceDouble = in.readDouble();
        bundleProductMaxSpecialPriceConverted = in.readDouble();
        bundleProductSpecialPrice = in.readString();
        bundleProductSpecialPriceDouble = in.readDouble();
        bundleProductSpecialPriceConverted = in.readDouble();
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
        dest.writeString(bundleProductSku);
        dest.writeString(bundleProductName);
        dest.writeString(bundleProductBrand);
        dest.writeString(bundleProductMaxPrice);
        dest.writeDouble(bundleProductMaxPriceDouble);
        dest.writeDouble(bundleProductMaxPriceConverted);
        dest.writeString(bundleProductImage);
        dest.writeString(bundleProductPrice);
        dest.writeDouble(bundleProductPriceDouble);
        dest.writeDouble(bundleProductPriceConverted);
        dest.writeInt(bundleProductLeaderPos);
        dest.writeString(bundleProductMaxSpecialPrice);
        dest.writeDouble(bundleProductMaxSpecialPriceDouble);
        dest.writeDouble(bundleProductMaxSpecialPriceConverted);
        dest.writeString(bundleProductSpecialPrice);
        dest.writeDouble(bundleProductSpecialPriceDouble);
        dest.writeDouble(bundleProductSpecialPriceConverted);
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
