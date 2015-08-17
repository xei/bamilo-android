package com.mobile.newFramework.objects.product.pojo;

import android.os.Parcel;

import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.CollectionUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class used to represent a product with a list of simple variations.
 *
 * @author sergio pereira
 */
public class ProductMultiple extends ProductRegular {

    public final static int NO_DEFAULT_SIMPLE_POS = -1;

    private String mSizeGuideUrl;
    private String mVariationName;
    private ArrayList<ProductSimple> mSimples;
    private int mSelectedSimplePosition;

    /**
     * Empty constructor
     */
    public ProductMultiple() {
        super();
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        // Base product
        super.initialize(jsonObject);
        // Size guide
        mSizeGuideUrl = jsonObject.optString(RestConstants.JSON_SIZE_GUIDE_URL_TAG);
        // Get variation name
        mVariationName = jsonObject.optString(RestConstants.VARIATION_NAME);
        // Default selected simple position
        mSelectedSimplePosition = jsonObject.optInt(RestConstants.VARIATION_DEFAULT_POSITION, NO_DEFAULT_SIMPLE_POS);
        // Simples
        JSONArray simpleArray = jsonObject.getJSONArray(RestConstants.JSON_SIMPLES_TAG);
        int size = simpleArray.length();
        if (size > 0) {
            mSimples = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                JSONObject simpleObject = simpleArray.getJSONObject(i);
                ProductSimple simple = new ProductSimple();
                simple.initialize(simpleObject);
                mSimples.add(simple);
            }
        }
        return true;
    }

    @Override
    public JSONObject toJSON() {
        return super.toJSON();
    }

    public ArrayList<ProductSimple> getSimples() {
        return mSimples;
    }

    public boolean hasOwnSimpleVariation() {
        return CollectionUtils.isNotEmpty(mSimples) && mSimples.size() == 1;
    }

    public boolean hasMultiSimpleVariations() {
        return !hasOwnSimpleVariation();
    }

    public String getSizeGuideUrl() {
        return mSizeGuideUrl;
    }

    public String getVariationName() {
        return mVariationName;
    }

    public int getSelectedSimplePosition() {
        return mSelectedSimplePosition;
    }

    public boolean hasSelectedSimpleVariation() {
        return mSelectedSimplePosition != NO_DEFAULT_SIMPLE_POS;
    }

    public void setSelectedSimplePosition(int simplePosition) {
        mSelectedSimplePosition = simplePosition;
    }

    public ProductSimple getOwnSimpleVariation() {
        return mSimples.get(0);
    }

    public ProductSimple getSelectedSimpleVariation() {
        return getSimples().get(mSelectedSimplePosition);
    }


    /*
     * ############ PARCELABLE ############
	 */

    protected ProductMultiple(Parcel in) {
        super(in);
        mSizeGuideUrl = in.readString();
        mVariationName = in.readString();
        if (in.readByte() == 0x01) {
            mSimples = new ArrayList<>();
            in.readList(mSimples, ProductSimple.class.getClassLoader());
        } else {
            mSimples = null;
        }
        mSelectedSimplePosition = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(mSizeGuideUrl);
        dest.writeString(mVariationName);
        if (mSimples == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mSimples);
        }
        dest.writeInt(mSelectedSimplePosition);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @SuppressWarnings("unused")
    public static final Creator<ProductMultiple> CREATOR = new Creator<ProductMultiple>() {
        @Override
        public ProductMultiple createFromParcel(Parcel in) {
            return new ProductMultiple(in);
        }

        @Override
        public ProductMultiple[] newArray(int size) {
            return new ProductMultiple[size];
        }
    };
}
