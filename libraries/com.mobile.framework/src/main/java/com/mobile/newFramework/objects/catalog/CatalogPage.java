package com.mobile.newFramework.objects.catalog;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.catalog.filters.CatalogFilter;
import com.mobile.newFramework.objects.catalog.filters.CatalogFilters;
import com.mobile.newFramework.objects.product.pojo.ProductRegular;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.CollectionUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Dictionary;

/**
 * Class used to represent a catalog page.<br>
 * @author sergiopereira
 */
public class CatalogPage implements IJSONSerializable, Parcelable {

    protected static final String TAG = CatalogPage.class.getSimpleName();

    private String mCategoryId;

    private String mBrandId;

    private String mName;

    private int mTotal;

    private int mPage = IntConstants.FIRST_PAGE;

    private int mMaxPages = IntConstants.FIRST_PAGE;

    private ArrayList<ProductRegular> mProducts;

    private Banner mCatalogBanner;

    private String mSearchTerm;

    private String mSort;

    private ArrayList<CatalogFilter> filters;

    public ArrayList<String> getBreadcrumb() {
        return breadcrumb;
    }



    private ArrayList<String> breadcrumb;

    /*
     * ########### CONSTRUCTOR ###########
     */

    /**
     * Empty constructor
     */
    public CatalogPage() {
        //...
    }

    public CatalogPage(JSONObject metadataObject) throws JSONException {
        this();
        initialize(metadataObject);
    }

    /*
     * ############### IJSON ###############
     */

    /* (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject metadataObject) throws JSONException {
        // Get data
        mCategoryId = metadataObject.optString(RestConstants.CATEGORIES);
        mBrandId = metadataObject.optString(RestConstants.BRANDS);
        mName = metadataObject.optString(RestConstants.TITLE);
        mSearchTerm = metadataObject.optString(RestConstants.SEARCH_TERM);
        mTotal = metadataObject.optInt(RestConstants.TOTAL_PRODUCTS);
        mSort = metadataObject.optString(RestConstants.SORT);
        // Set the max pages that application can request
        mMaxPages = calcMaxPages();
        // Get products
        mProducts = new ArrayList<>();
        JSONArray productObjectArray = metadataObject.getJSONArray(RestConstants.RESULTS);
        for (int i = 0; i < productObjectArray.length(); ++i) {
            JSONObject productObject = productObjectArray.getJSONObject(i);
            ProductRegular product = new ProductRegular();
            product.initialize(productObject);
            mProducts.add(product);
        }
        // Get filters
        filters = new CatalogFilters(metadataObject);
        // Get Banner
        if (!metadataObject.isNull(RestConstants.BANNER)) {
            JSONObject bannerObject = metadataObject.getJSONObject(RestConstants.BANNER);
            Banner banner = new Banner();
            banner.initialize(bannerObject);
            mCatalogBanner = banner;
        }

        breadcrumb = new ArrayList<>();
        JSONArray breadObjectArray = metadataObject.getJSONArray(RestConstants.Breadcrumb);
        for (int i = 0; i < breadObjectArray.length(); ++i) {
            JSONObject breadObject = breadObjectArray.getJSONObject(i);
            //ProductRegular product = new ProductRegular();
            //product.initialize(productObject);
            breadcrumb.add(breadObject.getString("title"));
        }

        return true;
    }

    /* (non-Javadoc)
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

    /**
     * Calculate the max request per page.
     * @return number or pages
     */
    private int calcMaxPages() {
        return (mTotal / IntConstants.MAX_ITEMS_PER_PAGE) + ((mTotal % IntConstants.MAX_ITEMS_PER_PAGE) > 0 ? 1 : 0);
    }

    /**
     * The current catalog page
     * @return int
     */
    public int getPage() {
        return mPage;
    }

    /**
     * Set the current page
     * @param page - the new page number
     */
    public void setPage(int page) {
        this.mPage = page;
    }

    /**
     * Update the current catalog with the new catalog data.<br>
     *     Case first page replace all content.
     * @param catalog - the new catalog
     */
    public void update(CatalogPage catalog) {
        // Update current page
        mPage = catalog.getPage();
        // Update total
        mTotal = catalog.getTotal();
        // Update the max pages that application can request
        mMaxPages = calcMaxPages();
        // Case replace data
        if(mPage == IntConstants.FIRST_PAGE) mProducts = catalog.getProducts();
        // Case append data
        else CollectionUtils.addAll(mProducts, catalog.getProducts());
    }

    /**
     * Get products
     * @return a list of products
     */
    public ArrayList<ProductRegular> getProducts() {
        return mProducts;
    }

    /**
     * Get max pages
     * @return the max number of pages to request
     */
    public int getMaxPages() {
        return mMaxPages;
    }

    /**
     * Validate if the current catalog has more pages to load
     * @return true or false
     */
    public boolean hasMorePagesToLoad() {
        return mPage < mMaxPages;
    }

    /**
     * @return the totalProducts
     */
    public int getTotal() {
        return mTotal;
    }

    /**
     * @return the filters
     */
    public ArrayList<CatalogFilter> getFilters() {
        return filters;
    }

    /**
     * @return the filters
     */
    public boolean hasFilters() {
        return CollectionUtils.isNotEmpty(filters);
    }

    /**
     * Validate products is empty or not.
     * @return true or false
     */
    public boolean hasProducts() {
        return CollectionUtils.isNotEmpty(mProducts);
    }

    /**
     * Get name
     * @return String
     */
    public String getName(){
        return mName;
    }

    public Banner getCatalogBanner() {
        return mCatalogBanner;
    }

    /**
     * Get search term
     * @return String
     */
    public String getSearchTerm(){
        return mSearchTerm;
    }

    /**
     * Get id
     * @return String
     */
    public String getCategoryId(){
        return mCategoryId;
    }

    /**
     * Get brand id
     * @return String
     */
    public String getBrandId(){
        return mBrandId;
    }

    /**
     * Get Applied Sort
     */
    public String getSort(){
        return mSort;
    }


    /*
     * ############### Parcelable ###############
     */

    protected CatalogPage(Parcel in) {
        mCategoryId = in.readString();
        mBrandId = in.readString();
        mName = in.readString();
        mTotal = in.readInt();
        mPage = in.readInt();
        mMaxPages = in.readInt();
        if (in.readByte() == 0x01) {
            mProducts = new ArrayList<>();
            in.readList(mProducts, ProductRegular.class.getClassLoader());
        } else {
            mProducts = null;
        }
        mCatalogBanner = (Banner) in.readValue(Banner.class.getClassLoader());
        mSearchTerm = in.readString();
        if (in.readByte() == 0x01) {
            filters = new ArrayList<>();
            in.readList(filters, CatalogFilter.class.getClassLoader());
        } else {
            filters = null;
        }
        mSort = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mCategoryId);
        dest.writeString(mBrandId);
        dest.writeString(mName);
        dest.writeInt(mTotal);
        dest.writeInt(mPage);
        dest.writeInt(mMaxPages);
        if (mProducts == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mProducts);
        }
        dest.writeValue(mCatalogBanner);
        dest.writeString(mSearchTerm);
        if (filters == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(filters);
        }

        dest.writeString(mSort);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<CatalogPage> CREATOR = new Parcelable.Creator<CatalogPage>() {
        @Override
        public CatalogPage createFromParcel(Parcel in) {
            return new CatalogPage(in);
        }

        @Override
        public CatalogPage[] newArray(int size) {
            return new CatalogPage[size];
        }
    };

}
