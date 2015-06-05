package com.mobile.newFramework.objects.product;


import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.utils.CurrencyFormatter;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

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
public class ProductBundle implements IJSONSerializable, Parcelable {

    private static final String TAG = LogTagHelper.create(ProductBundle.class);

    private String bundleName;
    private String bundleId;
    private String bundlePrice;
    private double bundlePriceDouble;
    private double bundlePriceConverted;
    private int bundleLeaderPos;
    private ArrayList<ProductBundleProduct> bundleProducts;

    /**
     * Complete product bundle empty constructor.
     */
    public ProductBundle() {
        bundleName = "";
        bundleId = "";
        bundlePrice = "";
        bundlePriceDouble = 0.0;
        bundlePriceConverted = 0.0;
        bundleLeaderPos = 0;
        bundleProducts = new ArrayList<>();
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

            bundleId = jsonObject.getString(RestConstants.JSON_BUNDLE_ID);
            bundleName = jsonObject.getString(RestConstants.JSON_BUNDLE_NAME);

            String priceJSON = jsonObject.getString(RestConstants.JSON_BUNDLE_PRICE);

            if (!CurrencyFormatter.isNumber(priceJSON)) {
                throw new JSONException("Price is not a number!");
            }
            bundlePriceDouble = Double.parseDouble(priceJSON);
            bundlePrice = priceJSON;

            bundlePriceConverted = jsonObject.getDouble(RestConstants.JSON_BUNDLE_PRICE_CONVERTED);
            bundleLeaderPos = jsonObject.getInt(RestConstants.JSON_BUNDLE_LEADER_POS);
            JSONArray bundleProductsArray = jsonObject.optJSONArray(RestConstants.JSON_BUNDLE_PRODUCTS);

            if (bundleProductsArray != null && bundleProductsArray.length() > 0) {
                for (int i = 0; i < bundleProductsArray.length(); i++) {

                    JSONObject productJson = bundleProductsArray.getJSONObject(i);

                    ProductBundleProduct bundleProduct = new ProductBundleProduct(productJson);

                    bundleProducts.add(bundleProduct);
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
        return RequiredJson.OBJECT_DATA;
    }

    public String getBundleName() {
        return bundleName;
    }

    public void setBundleName(String bundleName) {
        this.bundleName = bundleName;
    }

    public String getBundleId() {
        return bundleId;
    }

    public void setBundleId(String bundleId) {
        this.bundleId = bundleId;
    }

    public String getBundlePrice() {
        return bundlePrice;
    }

    public void setBundlePrice(String bundlePrice) {
        this.bundlePrice = bundlePrice;
    }

    public double getBundlePriceDouble() {
        return bundlePriceDouble;
    }

    public void setBundlePriceDouble(double bundlePriceDouble) {
        this.bundlePriceDouble = bundlePriceDouble;
    }

    public double getBundlePriceConverted() {
        return bundlePriceConverted;
    }

    public void setBundlePriceConverted(double bundlePriceConverted) {
        this.bundlePriceConverted = bundlePriceConverted;
    }

    public int getBundleLeaderPos() {
        return bundleLeaderPos;
    }

    public void setBundleLeaderPos(int bundleLeaderPos) {
        this.bundleLeaderPos = bundleLeaderPos;
    }

    public ArrayList<ProductBundleProduct> getBundleProducts() {
        return bundleProducts;
    }

    public void setBundleProducts(ArrayList<ProductBundleProduct> bundleProducts) {
        this.bundleProducts = bundleProducts;
    }


    /*
     * ############ PARCELABLE ############
     */

    protected ProductBundle(Parcel in) {
        bundleName = in.readString();
        bundleId = in.readString();
        bundlePrice = in.readString();
        bundlePriceDouble = in.readDouble();
        bundlePriceConverted = in.readDouble();
        bundleLeaderPos = in.readInt();
        if (in.readByte() == 0x01) {
            bundleProducts = new ArrayList<>();
            in.readList(bundleProducts, ProductBundleProduct.class.getClassLoader());
        } else {
            bundleProducts = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(bundleName);
        dest.writeString(bundleId);
        dest.writeString(bundlePrice);
        dest.writeDouble(bundlePriceDouble);
        dest.writeDouble(bundlePriceConverted);
        dest.writeInt(bundleLeaderPos);
        if (bundleProducts == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(bundleProducts);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<ProductBundle> CREATOR = new Parcelable.Creator<ProductBundle>() {
        @Override
        public ProductBundle createFromParcel(Parcel in) {
            return new ProductBundle(in);
        }

        @Override
        public ProductBundle[] newArray(int size) {
            return new ProductBundle[size];
        }
    };

}
