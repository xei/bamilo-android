package com.mobile.newFramework.objects.catalog;


import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.TextUtils;

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
 *
 */
public class FeaturedBox implements IJSONSerializable, Parcelable {

    public static final String TAG = FeaturedBox.class.getSimpleName();

    private String productsTitle;
    private String brandsTitle;
    private String searchTips;
    private String errorMessage;
    private String noticeMessage;
    private ArrayList<FeaturedItem> brands;
    private ArrayList<FeaturedItem> products;

    public FeaturedBox() {

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
//        Log.d(TAG, "FILTER: FEATURED BOX");
        JSONArray featuredBoxObject = metadataObject.getJSONArray(RestConstants.JSON_FEATURED_BOX_TAG);
        if (featuredBoxObject != null) {
            // one List for all products
            products = new ArrayList<>();
            try{

                if (featuredBoxObject != null && featuredBoxObject.length() > 0) {
                    // get products only from the first list
                    JSONObject productsCategoryObject = featuredBoxObject.getJSONObject(0);
                    if (productsCategoryObject != null) {
                        // get title
                        if (TextUtils.isEmpty(productsTitle)) {
                            productsTitle = productsCategoryObject.optString(RestConstants.TITLE);
                        }

                        JSONArray productsObject = productsCategoryObject.getJSONArray(RestConstants.PRODUCTS);
                        if (productsObject != null && productsObject.length() > 0) {
                            // get products
                            for (int j = 0; j < productsObject.length(); j++) {
                                JSONObject productObject = productsObject.getJSONObject(j);
                                FeaturedItemProduct product = new FeaturedItemProduct();

                                // only use products properly initialized
                                if (product.initialize(productObject)) {
                                    products.add(product);
                                }
                            }
                        }
                    }
                }
            } catch(JSONException ex){
//                Log.e(TAG, "ERROR PARSING FEATURE BOX");
            }
            // one list for all brands
            brands = new ArrayList<>();

            try{
                JSONArray featuredBrandboxObject = metadataObject.getJSONArray(RestConstants.JSON_FEATURED_BRAND_BOX_TAG);
                if (featuredBrandboxObject != null && featuredBrandboxObject.length() > 0) {
                    // get brands only from the first list
                    JSONObject brandsCategoryObject = featuredBrandboxObject.getJSONObject(0);
                    if (brandsCategoryObject != null) {
                        // get title from fist list of brands
                        if (TextUtils.isEmpty(brandsTitle)) {
                            brandsTitle = brandsCategoryObject.optString(RestConstants.TITLE);
                        }

                        JSONArray brandsObject = brandsCategoryObject.getJSONArray(RestConstants.JSON_BRANDS_TAG);
                        if (brandsObject != null && brandsObject.length() > 0) {
                            // get brands
                            for (int j = 0; j < brandsObject.length(); j++) {
                                JSONObject brandObject = brandsObject.getJSONObject(j);
                                FeaturedItemBrand brand = new FeaturedItemBrand();

                                // only use brands properly initialized
                                if (brand.initialize(brandObject)) {
                                    brands.add(brand);
                                }
                            }
                        }
                    }
                }
            } catch(JSONException ex){
//                Log.e(TAG, "ERROR PARSING FEATURE BRAND BOX");
            }

            JSONObject searchTipsObject = metadataObject.optJSONObject(RestConstants.JSON_FEATURED_SEARCH_TIPS_TAG);
            if (searchTipsObject != null) {
                searchTips = searchTipsObject.optString(RestConstants.TEXT);
            }

            errorMessage = metadataObject.getString(RestConstants.JSON_ERROR_MESSAGE_TAG);

            noticeMessage = metadataObject.getString(RestConstants.JSON_NOTICE_MESSAGE_TAG);
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

    public String getProductsTitle() {
        return productsTitle;
    }

    public ArrayList<FeaturedItem> getProducts() {
        return products;
    }

    public String getBrandsTitle() {
        return brandsTitle;
    }

    public ArrayList<FeaturedItem> getBrands() {
        return brands;
    }

    public String getSearchTips() {
        return searchTips;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public String getNoticeMessage() {
        return noticeMessage;
    }

    /*
     * ########### Parcelable ###########
     */

    protected FeaturedBox(Parcel in) {
        productsTitle = in.readString();
        if (in.readByte() == 0x01) {
            products = new ArrayList<>();
            in.readList(products, FeaturedItem.class.getClassLoader());
        } else {
            products = null;
        }
        brandsTitle = in.readString();
        if (in.readByte() == 0x01) {
            brands = new ArrayList<>();
            in.readList(brands, FeaturedItem.class.getClassLoader());
        } else {
            brands = null;
        }
        searchTips = in.readString();
        errorMessage = in.readString();
        noticeMessage = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(productsTitle);
        if (products == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(products);
        }
        dest.writeString(brandsTitle);
        if (brands == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(brands);
        }
        dest.writeString(searchTips);
        dest.writeString(errorMessage);
        dest.writeString(noticeMessage);
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
