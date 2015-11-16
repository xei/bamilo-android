package com.mobile.newFramework.objects.product.pojo;

import android.os.Parcel;

import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.TextUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Defines a simple (variation) of a give product.
 *
 * @author sergio pereira
 */
public class ProductSimple extends ProductBase {

    private String mVariationValue;
    private int mQuantity;

    /**
     * Empty constructor.
     */
    public ProductSimple() {
        super();
    }

    public ProductSimple(JSONObject jsonObject) throws JSONException {
        initialize(jsonObject);
    }

    /* (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        // TODO NORMAL COMPLETE SIMPLE PRODUCT
        if (jsonObject.optJSONObject(RestConstants.JSON_META_TAG) != null) {
            jsonObject = jsonObject.getJSONObject(RestConstants.JSON_META_TAG);
        }
        // Base
        super.initialize(jsonObject);

        // TODO
        mVariationValue = jsonObject.optString(RestConstants.JSON_VARIATION_TAG);
        if (TextUtils.isEmpty(mVariationValue))
            mVariationValue = jsonObject.optString(RestConstants.JSON_SIZE_TAG);
        if (TextUtils.isEmpty(mVariationValue))
            mVariationValue = jsonObject.optString(RestConstants.VARIATION_VALUE);

        mQuantity = jsonObject.getInt(RestConstants.JSON_QUANTITY_TAG);

        return true;
    }

    public String getVariationValue() {
        return mVariationValue;
    }

    public int getQuantity() {
        return mQuantity;
    }

    public boolean isOutOfStock() {
        return mQuantity <= 0;
    }

    @Override
    public String toString() {
        return mVariationValue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mVariationValue);
        dest.writeInt(mQuantity);
    }

    private ProductSimple(Parcel in) {
        super(in);
        mVariationValue = in.readString();
        mQuantity = in.readInt();
    }

    public static final Creator<ProductSimple> CREATOR = new Creator<ProductSimple>() {
        public ProductSimple createFromParcel(Parcel in) {
            return new ProductSimple(in);
        }

        public ProductSimple[] newArray(int size) {
            return new ProductSimple[size];
        }
    };
}
