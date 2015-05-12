/**
 *
 */
package com.mobile.helpers.products;

import android.content.ContentValues;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import android.text.TextUtils;

import com.mobile.framework.ErrorCode;
import com.mobile.framework.database.RelatedItemsTableHelper;
import com.mobile.framework.enums.RequestType;
import com.mobile.framework.objects.CatalogPage;
import com.mobile.framework.objects.FeaturedBox;
import com.mobile.framework.objects.Product;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.Utils;
import com.mobile.helpers.BaseHelper;
import com.mobile.helpers.HelperPriorityConfiguration;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import de.akquinet.android.androlog.Log;

/**
 * Get Catalog Page helper
 *
 * @author sergiopereira
 *
 */
public class GetCatalogPageHelper extends BaseHelper {

    private static String TAG = GetCatalogPageHelper.class.getSimpleName();

    private static final EventType EVENT_TYPE = EventType.GET_PRODUCTS_EVENT;

    public static final String SEARCH_NO_RESULTS = "SEARCH_NO_RESULTS";

    public static final String CATALOG_ARGUMENTS = "catalog_arguments";

    public static final String SAVE_RELATED_ITEMS = "save_related_items";

    public static final int MAX_ITEMS_PER_PAGE = CatalogPage.MAX_ITEMS_PER_PAGE;

    public static final int FIRST_PAGE_NUMBER = 1;

    public static final String URL = Constants.BUNDLE_URL_KEY;

    public static final int FEATURE_BOX_TYPE = 999;
    //
    private int mCurrentPage = FIRST_PAGE_NUMBER;
    // Request parameters
    public static final String PAGE = "page";
    public static final String MAX_ITEMS = "maxitems";
    public static final String SORT = "sort";
    public static final String DIRECTION = "dir";
    public static final String QUERY = "q";
    public static final String BRAND = "brand";
    // Flag used to save some items as related items
    private boolean isToSaveRelatedItems = false;

    /*
     * (non-Javadoc)
     * @see com.mobile.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
     */
    @Override
    public Bundle generateRequestBundle(Bundle params) {
        Log.d(TAG, "FILTER REQUEST");
        // Is to save related items in case popularity sort, first page and not filter applied
        isToSaveRelatedItems = params.getBoolean(SAVE_RELATED_ITEMS);

        // Get catalog URL
        String baseUrl = params.getString(URL);
        // Case search then url is empty
        if (TextUtils.isEmpty(baseUrl)) baseUrl = EventType.GET_PRODUCTS_EVENT.action;
        // Get catalog parameters
        ContentValues catalogArguments = params.getParcelable(CATALOG_ARGUMENTS);
        // Get page number
        mCurrentPage = catalogArguments.getAsInteger(PAGE);
        // Build a complete catalog URL
        String url = createCatalogRequest(baseUrl, catalogArguments);

        // Create bundle for service
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, url);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCTS_EVENT);
        return bundle;
    }

    /**
     * Build a catalog URL with respective URL and parameters.<br>
     * All parameters should be final, used to sent for service.<br>
     * A null value will be ignored.
     * @param url
     * @param parameters
     * @return A complete catalog URL.
     * @author sergiopereira
     */
    private String createCatalogRequest(String url, ContentValues parameters) {
        // Create
        Builder builder = Uri.parse(url).buildUpon();
        // Add parameters
        if (parameters != null) {
            for (Entry<String, Object> entry : parameters.valueSet()) {
                // Case null goto next
                if(entry.getValue() == null) continue;
                // Append data
                builder.appendQueryParameter(entry.getKey(), String.valueOf(entry.getValue()));
            }
        }
        return builder.build().toString();
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
     */
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.i(TAG, "ON PARSE RESPONSE");
        try {
            // Create catalog
            CatalogPage catalog = new CatalogPage();
            catalog.initialize(jsonObject);
            catalog.setPage(mCurrentPage);
            bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, catalog);
            // Persist related Items when initially loading products for POPULARITY tab
            if (isToSaveRelatedItems) {
                final ArrayList<Product> aux = new ArrayList<>(catalog.getProducts());
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            RelatedItemsTableHelper.insertRelatedItemsAndClear(aux);
                        } catch (IllegalStateException | InterruptedException e) {
                            Log.w(TAG, "WARNING: IE ON SAVE RELATED ITEMS FROM CATALOG");
                        }
                    }
                }).start();
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return parseErrorBundle(bundle);
        }

        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCTS_EVENT);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "parseErrorBundle GetProductsHelper");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCTS_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle, org.json.JSONObject)
     */
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
        Log.d("TRACK", "parseResponseErrorBundle GetProductsHelper");
        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
        try {
            if (errorCode == ErrorCode.REQUEST_ERROR) {
                Log.i(TAG, "REQUEST_ERROR");
                HashMap<String, List<String>> errors = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
                if (errors != null) {
                    List<String> errorMessages = errors.get(Constants.BUNDLE_ERROR_KEY);
                    if (errorMessages.size() > 0) {
                        String specificErrorMessage = errorMessages.get(0);
                        if (SEARCH_NO_RESULTS.equals(specificErrorMessage)) {
                            FeaturedBox featuredBox = new FeaturedBox();
                            if (featuredBox.initialize(jsonObject)) {
                                bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, featuredBox);
                                bundle.putInt(Constants.BUNDLE_OBJECT_TYPE_KEY, FEATURE_BOX_TYPE);
                            }

                        }
                    }
                }
            }
        } catch (JSONException e) {
            //Catches exception if there isn't errorMessage or noticeMessage
            e.printStackTrace();
        }
        return parseErrorBundle(bundle);
    }
}