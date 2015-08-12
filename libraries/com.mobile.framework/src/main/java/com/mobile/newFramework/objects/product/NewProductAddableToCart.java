package com.mobile.newFramework.objects.product;

import android.os.Parcel;

import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.CollectionUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by spereira on 8/4/15.
 */
public class NewProductAddableToCart extends NewProductPartial {

    private String mSizeGuideUrl;
    private String mVariationName;
    private ArrayList<NewProductSimple> mSimples;

    /**
     * Empty constructor
     */
    public NewProductAddableToCart() {
        super();
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        // Base product
        super.initialize(jsonObject);
        // Size guide
        mSizeGuideUrl = jsonObject.optString(RestConstants.JSON_SIZE_GUIDE_URL_TAG);
        // Get variation name
        mVariationName = jsonObject.optString(RestConstants.JSON_VARIATION_NAME_TAG);
        // Simples
        JSONArray simpleArray = jsonObject.getJSONArray(RestConstants.JSON_SIMPLES_TAG);
        int size = simpleArray.length();
        if (size > 0) {
            mSimples = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                JSONObject simpleObject = simpleArray.getJSONObject(i);
                NewProductSimple simple = new NewProductSimple();
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
    
    public ArrayList<NewProductSimple> getSimples() {
        return mSimples;
    }

    public boolean hasOwnSimpleVariation() {
        return CollectionUtils.isNotEmpty(mSimples) && mSimples.size() == 1;
    }

    public boolean hasSimpleVariations() {
        return CollectionUtils.isNotEmpty(mSimples);
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

     /*
	 * ############ PARCELABLE ############
	 */

    protected NewProductAddableToCart(Parcel in) {
        // TODO
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @SuppressWarnings("unused")
    public static final Creator<NewProductAddableToCart> CREATOR = new Creator<NewProductAddableToCart>() {
        @Override
        public NewProductAddableToCart createFromParcel(Parcel in) {
            return new NewProductAddableToCart(in);
        }

        @Override
        public NewProductAddableToCart[] newArray(int size) {
            return new NewProductAddableToCart[size];
        }
    };
}
