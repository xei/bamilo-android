/**
 * 
 */
package com.mobile.framework.objects;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobile.framework.Darwin;
import com.mobile.framework.R;
import com.mobile.framework.rest.RestConstants;
import android.os.Parcel;
import android.os.Parcelable;
import de.akquinet.android.androlog.Log;

/**
 * @author nutzer2
 * @modified sergiopereira
 *
 */
public class ProductsPage implements IJSONSerializable, Parcelable {
    
    private static final String TAG = ProductsPage.class.getSimpleName();
        
    private int totalProducts;
    
    private ArrayList<String> products;
    private HashMap<String, Product> productsMap;
    
    private ArrayList<CatalogFilter> mFilters;

    private String mPageName;
    
    private String mCategoryId;
    
    public ProductsPage() {
    
    }

    /* (non-Javadoc)
     * @see com.mobile.framework.objects.IJSONSerializable#initialize(org.json.JSONObject)
     */
    @Override
    public boolean initialize(JSONObject metadataObject) throws JSONException {
        Log.d(TAG, "FILTER: PRODUCT PAGE");
        
        products = new ArrayList<String>();
        mFilters = new ArrayList<CatalogFilter>();
        productsMap = new HashMap<String, Product>();
        totalProducts = metadataObject.optInt(RestConstants.JSON_PRODUCT_COUNT_TAG, 0);
        mPageName = metadataObject.optString(RestConstants.JSON_CATALOG_NAME_TAG, "");
        mCategoryId = metadataObject.optString(RestConstants.JSON_CATALOG_IDS_TAG, "");

        JSONArray productObjectArray = metadataObject.getJSONArray(RestConstants.JSON_RESULTS_TAG);

        // Get products
        for (int i = 0; i < productObjectArray.length(); ++i) {
            JSONObject productObject = productObjectArray.getJSONObject(i);
            Product product = new Product();
            product.initialize(productObject);
            products.add(product.getSKU());
            productsMap.put(product.getSKU(), product);
        }
        
        // Get category filter
        if (!metadataObject.isNull(RestConstants.JSON_CATEGORIES_TAG)) {
            // Validate array
            JSONArray categoriesArray = metadataObject.optJSONArray(RestConstants.JSON_CATEGORIES_TAG);
            if(categoriesArray != null && categoriesArray.length() > 0)
                parseCategoryFilter(categoriesArray);
            else
                Log.d(TAG, "THERE IS NO CATEGORY FILTER");
        }
        // Get the other filters
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
        }
        return true;
    }

    
    /**
     * Parse the JSON for categories, supported parent and leaf structure
     * @param categoriesArray
     * @throws JSONException
     * @author sergiopereira
     */
    private void parseCategoryFilter(JSONArray categoriesArray) throws JSONException{
        Log.d(TAG, "PARSE CATEGORIES: # " + categoriesArray.length());
        JSONArray categoryArray = null;
        // Get the first position
        JSONObject parentObject = categoriesArray.optJSONObject(0);
        JSONArray leafObject = categoriesArray.optJSONArray(0);
        // IS PARENT    - If first item is a JSON object
        if(parentObject != null) {
            Log.d(TAG, "CURRENT CATEGORY IS PARENT");
            categoryArray = parentObject.optJSONArray(RestConstants.JSON_CHILDREN_TAG);
        }
        // IS LEAF      - If first item is a JSON array
        else if(leafObject != null) {
            Log.d(TAG, "CURRENT CATEGORY IS LEAF");
            categoryArray = leafObject;
        }
        // Create category option and save it 
        ArrayList<CatalogFilterOption> options = new ArrayList<CatalogFilterOption>();
        if(categoryArray != null) {
            Log.d(TAG, "PARSE ADD TO CATALOG");
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
    
    public ArrayList<String> getProducts() {
        return products;
    }

    public HashMap<String, Product> getProductsMap() {
        return productsMap;
    }

    public ArrayList<Product> getProductsList() {
        ArrayList<Product> prodList = new ArrayList<Product>();
        for(String sku : products) {
            prodList.add(productsMap.get(sku));
        }
        return prodList;
    }
    
    /**
     * @return the totalProducts
     */
    public int getTotalProducts() {
        return totalProducts;
    }
    
    /**
     * @return the filters
     */
    public ArrayList<CatalogFilter> getFilters() {
        return mFilters;
    }
    
    public String getName(){
        return mPageName;
    }
    
    public String getCategoryId(){
        return mCategoryId;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(totalProducts);
        dest.writeList(products);

        dest.writeInt(productsMap.size());
        for(String key : productsMap.keySet()){
            dest.writeString(key);
            dest.writeParcelable(productsMap.get(key), 0);
        }
        
        dest.writeList(mFilters);
    }
    
    private ProductsPage(Parcel in){
        totalProducts = in.readInt();
        products = new ArrayList<String>();
        in.readList(products, String.class.getClassLoader());
        
        int size = in.readInt();
        productsMap = new HashMap<String, Product>();
        for(int i = 0; i < size; i++){
            String key = in.readString();
            Product value = in.readParcelable(Product.class.getClassLoader());
            productsMap.put(key,value);
        }       
        
        mFilters = new ArrayList<CatalogFilter>();
        in.readList(mFilters, CatalogFilter.class.getClassLoader());
    }
    
    public static final Parcelable.Creator<ProductsPage> CREATOR = new Parcelable.Creator<ProductsPage>() {
        public ProductsPage createFromParcel(Parcel in) {
            return new ProductsPage(in);
        }

        public ProductsPage[] newArray(int size) {
            return new ProductsPage[size];
        }
    };

}
