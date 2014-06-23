/**
 * 
 */
package pt.rocket.helpers;

import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.ErrorCode;
import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.objects.FeaturedBox;
import pt.rocket.framework.objects.ProductsPage;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import android.content.ContentValues;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

/**
 * Get Products Page helper
 * 
 * @author Manuel Silva
 * 
 */
public class GetProductsHelper extends BaseHelper {
    
    private static String TAG = GetProductsHelper.class.getSimpleName();
    
    public static final String PRODUCT_URL = "productUrl";
    public static final String SEARCH_QUERY = "searchQuery";
    public static final String PAGE_NUMBER = "pageNumber";
    public static final String TOTAL_COUNT = "totalCount";
    public static final String SORT = "sort";
    public static final String DIRECTION = "direction";
    public static final String FILTERS = "filters";
    public static final String SEARCH_NO_RESULTS = "SEARCH_NO_RESULTS";
    
    ProductsPage mProductsPage= new ProductsPage();

    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Log.d(TAG, "FILTER REQUEST");
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, getRequestURI(args.getString(PRODUCT_URL), args.getString(SEARCH_QUERY), args.getInt(PAGE_NUMBER), args.getInt(TOTAL_COUNT), args.getInt(SORT), args.getInt(DIRECTION), (ContentValues) args.getParcelable(FILTERS)));
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCTS_EVENT);
        return bundle;
    }

    private String getRequestURI(String productUrl, String searchQuery, int pageNumber, int totalCount, int sort, int direction, ContentValues filters){
        Uri productsUri;
        if (TextUtils.isEmpty(productUrl)) {
            productsUri = Uri.parse(EventType.GET_PRODUCTS_EVENT.action);
        } else {
            productsUri = Uri.parse(productUrl);
        }
        Builder uriBuilder = productsUri.buildUpon();

        if (!TextUtils.isEmpty(searchQuery)) {
            uriBuilder.appendQueryParameter("q", searchQuery);
        }

        if (pageNumber > 0)
            uriBuilder.appendQueryParameter("page", "" + pageNumber);

        if (totalCount > 0)
            uriBuilder.appendQueryParameter("maxitems", "" + totalCount);

        if (sort > 0) {
            String sortString = "";
            switch (sort) {
            case 0:
                sortString = "popularity";
                break;
            case 1:
                sortString = "name";
                break;
            case 2:
                sortString = "price";
                break;
            case 3:
                sortString = "brand";
                break;
            case 4:
                sortString = "newest";
                break;
            case 5:
                sortString = "rating";
                break;
            }
            if (!TextUtils.isEmpty(sortString)) {
                uriBuilder.appendQueryParameter("sort", "" + sortString);
            }

            sortString = "";
            switch (direction) {
            case 0:
                sortString = "asc";
                break;
            case 1:
                sortString = "desc";
                break;
            }
            if (!TextUtils.isEmpty(sortString)) {
                uriBuilder.appendQueryParameter("dir", "" + sortString);
            }            
        }
        
        // Append filter values
        //Log.d(TAG, "APPEND FILTER VALUES");
        if(filters != null) {
            for (Entry<String, Object> entry : filters.valueSet()) {
                //Log.d(TAG, "ADD FILTER ENTRY: " + entry.getKey() + " " + entry.getValue());
                if(entry.getKey().equalsIgnoreCase("md5")) 
                    continue;
                uriBuilder.appendQueryParameter(entry.getKey(), (String) entry.getValue());
            }
        }
        
        return uriBuilder.build().toString();
    }

    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        try {
        	android.util.Log.d("TRACK", "parseResponseBundle GetProductsHelper");
        	
        	ProductsPage products = new ProductsPage();
            products.initialize(jsonObject);
            bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, products);
            
        } catch (JSONException e) {
            e.printStackTrace();
            return parseErrorBundle(bundle);
        }
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCTS_EVENT);
        return bundle;
    }
    
    
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        android.util.Log.d(TAG, "parseErrorBundle GetProductsHelper");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCTS_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
        try {
            android.util.Log.d("TRACK", "parseResponseErrorBundle GetProductsHelper");
            ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
            if (errorCode == ErrorCode.REQUEST_ERROR) {
                Log.i(TAG, "REQUEST_ERROR");
                HashMap<String, List<String>> errors = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
                if (errors != null) {
                    List<String> errorMessages = (List<String>) errors.get(Constants.BUNDLE_ERROR_KEY);
                    if (errorMessages.size() > 0) {
                        String specificErrorMessage = errorMessages.get(0);
                        if (SEARCH_NO_RESULTS.equals(specificErrorMessage)) {
                            FeaturedBox featuredBox = new FeaturedBox();
                            featuredBox.initialize(jsonObject);
                            bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, featuredBox);
                        }
                    }
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return parseErrorBundle(bundle);
        }
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCTS_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
}
