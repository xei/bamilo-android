package com.mobile.newFramework.objects.product;


import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.product.pojo.ProductBundle;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.CollectionUtils;

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
    private int mLeaderPosition;
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
    public boolean initialize(JSONObject jsonObject) throws JSONException{
        JSONObject bundleEntityjson = jsonObject.getJSONObject(RestConstants.BUNDLE_ENTITY);

        mId = bundleEntityjson.getString(RestConstants.ID);
        mPrice = bundleEntityjson.getDouble(RestConstants.JSON_PRICE_TAG);
        mPriceConverted = bundleEntityjson.getDouble(RestConstants.JSON_BUNDLE_PRICE_CONVERTED);
        mLeaderPosition = bundleEntityjson.getInt(RestConstants.JSON_BUNDLE_LEADER_POS);
        JSONArray bundleProductsArray = bundleEntityjson.optJSONArray(RestConstants.PRODUCTS);
        if (bundleProductsArray != null && bundleProductsArray.length() > 0) {
            mProducts = new ArrayList<>();
            for (int i = 0; i < bundleProductsArray.length(); i++) {
                JSONObject productJson = bundleProductsArray.getJSONObject(i);
                ProductBundle bundleProduct = new ProductBundle(productJson);
                mProducts.add(bundleProduct);
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
        mLeaderPosition = in.readInt();
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
        dest.writeInt(mLeaderPosition);
        if (mProducts == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mProducts);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<BundleList> CREATOR = new Parcelable.Creator<BundleList>() {
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
