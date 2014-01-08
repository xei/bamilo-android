/**
 * 
 */
package pt.rocket.helpers;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.objects.ProductsPage;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.text.TextUtils;

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
    
    ProductsPage mProductsPage= new ProductsPage();

    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, getRequestURI(args.getString(PRODUCT_URL), args.getString(SEARCH_QUERY), args.getInt(PAGE_NUMBER), args.getInt(TOTAL_COUNT), args.getInt(SORT), args.getInt(DIRECTION)));
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        return bundle;
    }

    private String getRequestURI(String productUrl, String searchQuery, int pageNumber, int totalCount, int sort, int direction){
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
        }

        return bundle;
    }

    @Override   
    public Bundle parseErrorBundle(Bundle bundle) {
    	android.util.Log.d("TRACK", "parseErrorBundle GetCategoriesHelper");
        bundle.putString(Constants.BUNDLE_URL_KEY, " GetCategories");
        return bundle;
    }
}
