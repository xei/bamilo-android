package com.bamilo.android.framework.service.objects.product;


import android.os.Parcel;
import android.os.Parcelable;

import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.objects.product.pojo.ProductBundle;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.utils.CollectionUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * Class that manages the full representation of a given product bundle.
 *
 * @author Paulo Carvalho
 */
public class BundleList implements IJSONSerializable, Parcelable {

    protected static final String TAG = BundleList.class.getSimpleName();

    private String mId;
    private double mPrice;
    private double mPriceConverted;
    private ArrayList<ProductBundle> mProducts;


    /**
     * Complete product bundle empty constructor.
     */
    @SuppressWarnings("unused")
    public BundleList() {
        // ...
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
     * )
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        JSONObject bundleEntityJson = jsonObject.getJSONObject(RestConstants.BUNDLE_ENTITY);
        mId = bundleEntityJson.getString(RestConstants.ID);
        mPrice = bundleEntityJson.getDouble(RestConstants.PRICE);
        mPriceConverted = bundleEntityJson.getDouble(RestConstants.PRICE_CONVERTED);
        JSONArray bundleProductsArray = bundleEntityJson.optJSONArray(RestConstants.PRODUCTS);
        if (bundleProductsArray != null && bundleProductsArray.length() > 0) {
            mProducts = new ArrayList<>();
            for (int i = 0; i < bundleProductsArray.length(); i++) {
                JSONObject productJson = bundleProductsArray.getJSONObject(i);
                ProductBundle bundleProduct = new ProductBundle();
                if (bundleProduct.initialize(productJson)) {
                    mProducts.add(bundleProduct);
                }
            }
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

    public String getBundleId() {
        return mId;
    }

    public ArrayList<ProductBundle> getProducts() {
        return mProducts;
    }


    public double getPrice() {
        return mPrice;
    }

    /**
     * Change a bundle product state and update total combo's price when checking/unchecking a bundle product
     */
    public void updateTotalPriceWhenChecking(int bundlePosition) {
        if (CollectionUtils.isNotEmpty(mProducts)) {
            //get selected bundle
            ProductBundle productBundle = mProducts.get(bundlePosition);
            //change for the oposite state
            productBundle.setChecked(!productBundle.isChecked());
            //update total price
            if (productBundle.isChecked()) {
                mPrice += productBundle.hasDiscount() ? productBundle.getSpecialPrice() : productBundle.getPrice();
            } else {
                mPrice -= productBundle.hasDiscount() ? productBundle.getSpecialPrice() : productBundle.getPrice();
            }
            //update item in bundle array
            mProducts.set(bundlePosition, productBundle);
        }
    }

    /*
     * ############ PARCELABLE ############
     */

    protected BundleList(Parcel in) {
        mId = in.readString();
        mPrice = in.readDouble();
        mPriceConverted = in.readDouble();
        if (in.readByte() == 0x01) {
            mProducts = new ArrayList<>();
            in.readList(mProducts, ProductBundle.class.getClassLoader());
        } else {
            mProducts = null;
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
        dest.writeDouble(mPrice);
        dest.writeDouble(mPriceConverted);
        if (mProducts == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mProducts);
        }
    }

    @SuppressWarnings("unused")
    public static final Creator<BundleList> CREATOR = new Creator<BundleList>() {
        @Override
        public BundleList createFromParcel(Parcel in) {
            return new BundleList(in);
        }

        @Override
        public BundleList[] newArray(int size) {
            return new BundleList[size];
        }
    };

}
