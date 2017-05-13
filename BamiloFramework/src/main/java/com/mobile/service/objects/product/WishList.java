package com.mobile.service.objects.product;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.service.objects.IJSONSerializable;
import com.mobile.service.objects.RequiredJson;
import com.mobile.service.objects.product.pojo.ProductMultiple;
import com.mobile.service.pojo.IntConstants;
import com.mobile.service.pojo.RestConstants;
import com.mobile.service.utils.CollectionUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;

/**
 * Class used to represent a wish list page.
 * @author spereira
 */
public class WishList implements IJSONSerializable, Parcelable {

    private int mPage = IntConstants.FIRST_PAGE;
    private int mMaxPages = IntConstants.FIRST_PAGE;
    private ArrayList<ProductMultiple> mProducts;
    private HashSet<String> mWishListCache;

    /**
     * Empty constructor
     */
    @SuppressWarnings("unused")
    public WishList() {
        super();
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        // Pagination
        JSONObject pagination = jsonObject.getJSONObject(RestConstants.PAGINATION);
        // Page
        mPage = pagination.getInt(RestConstants.CURRENT_PAGE);
        // Max pages
        mMaxPages = pagination.getInt(RestConstants.TOTAL_PAGES);
        // Products
        JSONArray productsArray = jsonObject.getJSONArray(RestConstants.PRODUCTS);
        int size = productsArray.length();
        if (size > 0) {
            mProducts = new ArrayList<>();
            mWishListCache = new HashSet<>();
            for (int i = 0; i < size; i++) {
                JSONObject simpleObject = productsArray.getJSONObject(i);
                ProductMultiple product = new ProductMultiple();
                if (product.initialize(simpleObject)) {
                    mProducts.add(product);
                    mWishListCache.add(product.getSku());
                }
            }
        }
        return true;
    }

    public HashSet<String> getWishListCache() {
        return mWishListCache;
    }

    public int getPage() {
        return mPage;
    }

    public int getMaxPages() {
        return mMaxPages;
    }

    public ArrayList<ProductMultiple> getProducts() {
        return mProducts;
    }

    public boolean hasMorePages() {
        return mPage < mMaxPages;
    }

    public boolean hasProducts() {
        return CollectionUtils.isNotEmpty(mProducts);
    }

    public void update(WishList wishList) {
        this.mPage = wishList.getPage();
        this.mMaxPages = wishList.getMaxPages();
        // Case replace data
        if(mPage == IntConstants.FIRST_PAGE) mProducts = wishList.getProducts();
        // Case append data
        else CollectionUtils.addAll(mProducts, wishList.getProducts());
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.METADATA;
    }

    /*
     * ############ PARCELABLE ############
	 */

    protected WishList(Parcel in) {
        mPage = in.readInt();
        mMaxPages = in.readInt();
        if (in.readByte() == 0x01) {
            mProducts = new ArrayList<>();
            in.readList(mProducts, ProductMultiple.class.getClassLoader());
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
        dest.writeInt(mPage);
        dest.writeInt(mMaxPages);
        if (mProducts == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mProducts);
        }
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<WishList> CREATOR = new Parcelable.Creator<WishList>() {
        @Override
        public WishList createFromParcel(Parcel in) {
            return new WishList(in);
        }

        @Override
        public WishList[] newArray(int size) {
            return new WishList[size];
        }
    };

}
