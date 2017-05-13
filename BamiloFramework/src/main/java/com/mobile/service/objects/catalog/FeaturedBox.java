package com.mobile.service.objects.catalog;


import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.service.objects.IJSONSerializable;
import com.mobile.service.objects.RequiredJson;
import com.mobile.service.pojo.IntConstants;
import com.mobile.service.pojo.RestConstants;
import com.mobile.service.utils.CollectionUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class used to fill the suggestions screen when no results are found after a
 * search
 *
 * @author Andre Lopes
 * @modified sergiopereira
 */
public class FeaturedBox implements IJSONSerializable, Parcelable {

    private String mProductsTitle;
    private String mBrandsTitle;
    private String mSearchTips;
    private String mErrorMessage;
    private String mNoticeMessage;
    private ArrayList<FeaturedItem> mBrands;
    private ArrayList<FeaturedItem> mProducts;

    /**
     * Empty constructor
     */
    public FeaturedBox() {
        // ...
    }

    public FeaturedBox(JSONObject metadataObject) throws JSONException {
        this();
        initialize(metadataObject);
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject
     * )
     */
    @Override
    public boolean initialize(JSONObject metadataObject) throws JSONException {
        // Products
        JSONArray featuredBoxObject = metadataObject.optJSONArray(RestConstants.FEATURED_BOX);
        if (CollectionUtils.isNotEmpty(featuredBoxObject)) {
            // First item
            JSONObject productsCategoryObject = featuredBoxObject.getJSONObject(IntConstants.DEFAULT_POSITION);
            // Title
            mProductsTitle = productsCategoryObject.optString(RestConstants.TITLE);
            // Products
            JSONArray productsObject = productsCategoryObject.getJSONArray(RestConstants.PRODUCTS);
            if (CollectionUtils.isNotEmpty(productsObject)) {
                mProducts = new ArrayList<>();
                for (int j = 0; j < productsObject.length(); j++) {
                    // Only use products properly initialized
                    FeaturedItemProduct product = new FeaturedItemProduct();
                    if (product.initialize(productsObject.getJSONObject(j))) {
                        mProducts.add(product);
                    }
                }
            }
        }
        // Brands
        JSONArray featuredBrandBoxObject = metadataObject.optJSONArray(RestConstants.FEATURED_BRANDBOX);
        if (CollectionUtils.isNotEmpty(featuredBrandBoxObject)) {
            // First item
            JSONObject brandsCategoryObject = featuredBrandBoxObject.getJSONObject(IntConstants.DEFAULT_POSITION);
            // Title
            mBrandsTitle = brandsCategoryObject.optString(RestConstants.TITLE);
            // Brands
            JSONArray brandsObject = brandsCategoryObject.getJSONArray(RestConstants.BRANDS);
            if (CollectionUtils.isNotEmpty(brandsObject)) {
                mBrands = new ArrayList<>();
                for (int j = 0; j < brandsObject.length(); j++) {
                    // Only use products properly initialized
                    FeaturedItemBrand brand = new FeaturedItemBrand();
                    if (brand.initialize(brandsObject.getJSONObject(j))) {
                        mBrands.add(brand);
                    }
                }
            }
        }
        // Search tips
        JSONObject searchTipsObject = metadataObject.optJSONObject(RestConstants.SEARCH_TIPS);
        if (searchTipsObject != null) {
            mSearchTips = searchTipsObject.optString(RestConstants.TEXT);
        }
        // Messages
        mErrorMessage = metadataObject.getString(RestConstants.ERROR_MESSAGE);
        mNoticeMessage = metadataObject.getString(RestConstants.NOTICE_MESSAGE);
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

    public String getProductsTitle() {
        return mProductsTitle;
    }

    public ArrayList<FeaturedItem> getProducts() {
        return mProducts;
    }

    public String getBrandsTitle() {
        return mBrandsTitle;
    }

    public ArrayList<FeaturedItem> getBrands() {
        return mBrands;
    }

    public String getSearchTips() {
        return mSearchTips;
    }

    public String getErrorMessage() {
        return mErrorMessage;
    }

    public String getNoticeMessage() {
        return mNoticeMessage;
    }

    /*
     * ########### Parcelable ###########
     */

    protected FeaturedBox(Parcel in) {
        mProductsTitle = in.readString();
        if (in.readByte() == 0x01) {
            mProducts = new ArrayList<>();
            in.readList(mProducts, FeaturedItem.class.getClassLoader());
        } else {
            mProducts = null;
        }
        mBrandsTitle = in.readString();
        if (in.readByte() == 0x01) {
            mBrands = new ArrayList<>();
            in.readList(mBrands, FeaturedItem.class.getClassLoader());
        } else {
            mBrands = null;
        }
        mSearchTips = in.readString();
        mErrorMessage = in.readString();
        mNoticeMessage = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mProductsTitle);
        if (mProducts == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mProducts);
        }
        dest.writeString(mBrandsTitle);
        if (mBrands == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mBrands);
        }
        dest.writeString(mSearchTips);
        dest.writeString(mErrorMessage);
        dest.writeString(mNoticeMessage);
    }

    public static final Parcelable.Creator<FeaturedBox> CREATOR = new Parcelable.Creator<FeaturedBox>() {
        @Override
        public FeaturedBox createFromParcel(Parcel in) {
            return new FeaturedBox(in);
        }

        @Override
        public FeaturedBox[] newArray(int size) {
            return new FeaturedBox[size];
        }
    };

}
