package com.mobile.newFramework.objects.catalog;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.framework.R;
import com.mobile.newFramework.Darwin;
import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.product.NewProductPartial;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.CollectionUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Class used to represent a catalog page.<br>
 * @author sergiopereira
 */
public class CatalogPage implements IJSONSerializable, Parcelable {

    protected static final String TAG = CatalogPage.class.getSimpleName();

    public static final int MAX_ITEMS_PER_PAGE = 24;

    public static final int FIRST_PAGE = 1;

    private String mId;

    private String mName;

    private int mTotal;

    private int mPage = 1;

    private int mMaxPages = 1;

    private ArrayList<NewProductPartial> mProducts;

    private ArrayList<CatalogFilter> mFilters;

    private Banner mCatalogBanner;

    private String mSearchTerm;

    /*
     * ########### CONSTRUCTOR ###########
     */

    /**
     * Empty constructor
     */
    public CatalogPage() {
        //...
    }

    public CatalogPage(JSONObject metadataObject) throws JSONException{
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
        mId = metadataObject.optString(RestConstants.JSON_CATEGORIES_TAG);
        mName = metadataObject.optString(RestConstants.JSON_TITLE_TAG);
        mSearchTerm = metadataObject.optString(RestConstants.JSON_SEARCH_TERM_TAG);
        mTotal = metadataObject.optInt(RestConstants.JSON_TOTAL_PRODUCTS_TAG);
        // Set the max pages that application can request
        mMaxPages = calcMaxPages();
        // Get products
        mProducts = new ArrayList<>();
        JSONArray productObjectArray = metadataObject.getJSONArray(RestConstants.JSON_RESULTS_TAG);
        for (int i = 0; i < productObjectArray.length(); ++i) {
            JSONObject productObject = productObjectArray.getJSONObject(i);
            NewProductPartial product = new NewProductPartial();
            product.initialize(productObject);
            mProducts.add(product);
        }
        // Get filters
        mFilters = new ArrayList<>();

        // Get category filter
        /*
        if (!metadataObject.isNull(RestConstants.JSON_CATEGORIES_TAG)) {
            // Validate array
            JSONArray categoriesArray = metadataObject.optJSONArray(RestConstants.JSON_CATEGORIES_TAG);
            if(categoriesArray != null && categoriesArray.length() > 0)
                parseCategoryFilter(categoriesArray);
            else
                Log.d(TAG, "THERE IS NO CATEGORY FILTER");
        }*/

        // Get the other filters
        /* TODO: uncomment to support filters
        if(!metadataObject.isNull(RestConstants.JSON_FILTERS_TAG)){
            JSONArray jsonArray = metadataObject.getJSONArray(RestConstants.JSON_FILTERS_TAG);
            for (int i = 0; i < jsonArray.length(); i++) {
                // Get JSON filter
                JSONObject jsonFilter = jsonArray.getJSONObject(i);
                // Create catalog filter
                CatalogFilter catalogFilter = new CatalogFilter(jsonFilter);
                // save filter
                mFilters.add(catalogFilter);
            }
        }*/

        //Get Banner
        if(!metadataObject.isNull(RestConstants.JSON_BANNER_TAG)){
            JSONObject bannerObject = metadataObject.getJSONObject(RestConstants.JSON_BANNER_TAG);
            Banner banner = new Banner();
            banner.initialize(bannerObject);
            mCatalogBanner = banner;
        }

        return true;
    }



    /**
     * Parse the JSON for categories, supported parent and leaf structure
     * @param categoriesArray - the json array
     * @throws JSONException
     */
    @SuppressWarnings("unused")
    private void parseCategoryFilter(JSONArray categoriesArray) throws JSONException{
//        Log.d(TAG, "PARSE CATEGORIES: # " + categoriesArray.length());
        JSONArray categoryArray = null;
        // Get the first position
        JSONObject parentObject = categoriesArray.optJSONObject(0);
        JSONArray leafObject = categoriesArray.optJSONArray(0);
        // IS PARENT    - If first item is a JSON object
        if(parentObject != null) {
//            Log.d(TAG, "CURRENT CATEGORY IS PARENT");
            categoryArray = parentObject.optJSONArray(RestConstants.JSON_CHILDREN_TAG);
        }
        // IS LEAF      - If first item is a JSON array
        else if(leafObject != null) {
//            Log.d(TAG, "CURRENT CATEGORY IS LEAF");
            categoryArray = leafObject;
        }
        // Create category option and save it
        ArrayList<CatalogFilterOption> options = new ArrayList<>();
        if(categoryArray != null) {
//            Log.d(TAG, "PARSE ADD TO CATALOG");
            for (int i = 0; i < categoryArray.length(); ++i) {
                JSONObject json = categoryArray.optJSONObject(i);
                if(json != null) {
                    CategoryFilterOption opt = new CategoryFilterOption(json);
                    options.add(opt);
                }
            }
        }
        // Create the category filter and save it
        mFilters.add(new CatalogFilter("category", Darwin.context.getString(R.string.framework_category_label), false, options));
    }

    /* (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#toJSON()
     */
    @Override
    public JSONObject toJSON() {
        return null;
    }

    public RequiredJson getRequiredJson(){
        return RequiredJson.METADATA;
    }
    /**
     * Calculate the max request per page.
     * @return number or pages
     */
    private int calcMaxPages() {
        return (mTotal / MAX_ITEMS_PER_PAGE) + ((mTotal % MAX_ITEMS_PER_PAGE) > 0 ? 1 : 0);
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
        if(mPage == FIRST_PAGE) mProducts = catalog.getProducts();
        // Case append data
        else CollectionUtils.addAll(mProducts, catalog.getProducts());
    }

    /**
     * Get products
     * @return a list of products
     */
    public ArrayList<NewProductPartial> getProducts() {
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
        return mFilters;
    }

    /**
     * @return the filters
     */
    public boolean hasFilters() {
        return CollectionUtils.isNotEmpty(mFilters);
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
        return mId;
    }

    /*
     * ############### Parcelable ###############
     */

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mId);
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
        if (mFilters == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(mFilters);
        }
        dest.writeValue(mCatalogBanner);
        dest.writeString(mSearchTerm);

    }

    /**
     * Parcel constructor
     */
    private CatalogPage(Parcel in){
        mId = in.readString();
        mName = in.readString();
        mTotal = in.readInt();
        mPage = in.readInt();
        mMaxPages = in.readInt();
        if (in.readByte() == 0x01) {
            mProducts = new ArrayList<>();
            in.readList(mProducts, NewProductPartial.class.getClassLoader());
        } else {
            mProducts = null;
        }
        if (in.readByte() == 0x01) {
            mFilters = new ArrayList<>();
            in.readList(mFilters, CatalogFilter.class.getClassLoader());
        } else {
            mFilters = null;
        }
        mCatalogBanner = (Banner) in.readValue(Banner.class.getClassLoader());
        mSearchTerm = in.readString();
    }

    public static final Parcelable.Creator<CatalogPage> CREATOR = new Parcelable.Creator<CatalogPage>() {
        public CatalogPage createFromParcel(Parcel in) {
            return new CatalogPage(in);
        }

        public CatalogPage[] newArray(int size) {
            return new CatalogPage[size];
        }
    };


}
