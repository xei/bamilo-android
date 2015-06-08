package com.mobile.helpers.products;

import android.content.ContentValues;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;

import com.mobile.framework.ErrorCode;
import com.mobile.framework.database.RelatedItemsTableHelper;
import com.mobile.framework.output.Print;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.interfaces.AigApiInterface;
import com.mobile.newFramework.objects.catalog.Catalog;
import com.mobile.newFramework.objects.catalog.CatalogPage;
import com.mobile.newFramework.objects.product.Product;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.catalog.GetCatalogFiltered;
import com.mobile.newFramework.rest.RestUrlUtils;

import java.util.ArrayList;
import java.util.Map;

/**
 * Get Catalog Page helper
 *
 * @author sergiopereira
 *
 */
public class GetCatalogPageHelper extends SuperBaseHelper {

    private static String TAG = GetCatalogPageHelper.class.getSimpleName();

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

    @Override
    public EventType getEventType() {
        return EventType.GET_PRODUCTS_EVENT;
    }

    @Override
    protected RequestBundle createRequest(Bundle args) {
        // Is to save related items in case popularity sort, first page and not filter applied
        isToSaveRelatedItems = args.getBoolean(SAVE_RELATED_ITEMS);
        //
        return super.createRequest(args);
    }

    @Override
    protected String getRequestUrl(Bundle args) {
        // Get catalog URL
        String baseUrl = args.getString(URL);
        // Case search then url is empty
        if (TextUtils.isEmpty(baseUrl)) baseUrl = mEventType.action;
        //
        return RestUrlUtils.completeUri(Uri.parse(baseUrl)).toString();
    }

    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        // Get catalog parameters
        ContentValues catalogArguments = args.getParcelable(Constants.BUNDLE_DATA_KEY);
        // Get page number
        mCurrentPage = catalogArguments.getAsInteger(PAGE);
        //
        return convertContentValuesToMap(catalogArguments);
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
//        new GetCatalogFiltered(requestBundle, this).execute();
        new BaseRequest(requestBundle, this).execute(AigApiInterface.getCatalogFiltered);
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST COMPLETE: " + baseResponse.hadSuccess());
        //
        Catalog catalog = (Catalog) baseResponse.getMetadata().getData();
        catalog.getCatalogPage().setPage(mCurrentPage);
        // Persist related Items when initially loading products for POPULARITY tab
        if (isToSaveRelatedItems) {
            final ArrayList<Product> aux = new ArrayList<>(catalog.getCatalogPage().getProducts());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        RelatedItemsTableHelper.insertRelatedItemsAndClear(aux);
                    } catch (IllegalStateException | InterruptedException e) {
                        Print.w(TAG, "WARNING: IE ON SAVE RELATED ITEMS FROM CATALOG");
                    }
                }
            }).start();
        }
        //
        Bundle bundle = generateSuccessBundle(baseResponse);

        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, catalog.getCatalogPage());
        mRequester.onRequestComplete(bundle);
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST ERROR: " + baseResponse.getMessage());
        // Generic error
        Bundle bundle = generateErrorBundle(baseResponse);
        // Validate Featured Box
        Catalog catalog = (Catalog) baseResponse.getMetadata().getData();
        if(baseResponse.getError().getErrorCode() == ErrorCode.REQUEST_ERROR && catalog != null){
            bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, catalog.getFeaturedBox());
            bundle.putInt(Constants.BUNDLE_OBJECT_TYPE_KEY, FEATURE_BOX_TYPE);
        }
        mRequester.onRequestError(bundle);
    }

//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle generateRequestBundle(Bundle params) {
//        Log.d(TAG, "FILTER REQUEST");
//        // Is to save related items in case popularity sort, first page and not filter applied
//        isToSaveRelatedItems = params.getBoolean(SAVE_RELATED_ITEMS);
//
//        // Get catalog URL
//        String baseUrl = params.getString(URL);
//        // Case search then url is empty
//        if (TextUtils.isEmpty(baseUrl)) baseUrl = EventType.GET_PRODUCTS_EVENT.action;
//        // Get catalog parameters
//        ContentValues catalogArguments = params.getParcelable(CATALOG_ARGUMENTS);
//        // Get page number
//        mCurrentPage = catalogArguments.getAsInteger(PAGE);
//        // Build a complete catalog URL
//        String url = createCatalogRequest(baseUrl, catalogArguments);
//
//        // Create bundle for service
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, url);
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCTS_EVENT);
//        return bundle;
//    }

//    /**
//     * Build a catalog URL with respective URL and parameters.<br>
//     * All parameters should be final, used to sent for service.<br>
//     * A null value will be ignored.
//     * @param url
//     * @param parameters
//     * @return A complete catalog URL.
//     * @author sergiopereira
//     */
//    private String createCatalogRequest(String url, ContentValues parameters) {
//        // Create
//        Builder builder = Uri.parse(url).buildUpon();
//        // Add parameters
//        if (parameters != null) {
//            for (Entry<String, Object> entry : parameters.valueSet()) {
//                // Case null goto next
//                if(entry.getValue() == null) continue;
//                // Append data
//                builder.appendQueryParameter(entry.getKey(), String.valueOf(entry.getValue()));
//            }
//        }
//        return builder.build().toString();
//    }

//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
//     */
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.i(TAG, "ON PARSE RESPONSE");
//        try {
//            // Create catalog
//            CatalogPage catalog = new CatalogPage();
//            catalog.initialize(jsonObject);
//            catalog.setPage(mCurrentPage);
//            bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, catalog);
//            // Persist related Items when initially loading products for POPULARITY tab
//            if (isToSaveRelatedItems) {
//                final ArrayList<Product> aux = new ArrayList<>(catalog.getProducts());
//                new Thread(new Runnable() {
//                    @Override
//                    public void run() {
//                        try {
//                            RelatedItemsTableHelper.insertRelatedItemsAndClear(aux);
//                        } catch (IllegalStateException | InterruptedException e) {
//                            Log.w(TAG, "WARNING: IE ON SAVE RELATED ITEMS FROM CATALOG");
//                        }
//                    }
//                }).start();
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//            return parseErrorBundle(bundle);
//        }
//
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCTS_EVENT);
//        return bundle;
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "parseErrorBundle GetProductsHelper");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_PRODUCTS_EVENT);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle, org.json.JSONObject)
//     */
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.d("TRACK", "parseResponseErrorBundle GetProductsHelper");
//        ErrorCode errorCode = (ErrorCode) bundle.getSerializable(Constants.BUNDLE_ERROR_KEY);
//        try {
//            if (errorCode == ErrorCode.REQUEST_ERROR) {
//                Log.i(TAG, "REQUEST_ERROR");
//                HashMap<String, List<String>> errors = (HashMap<String, List<String>>) bundle.getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
//                if (errors != null) {
//                    List<String> errorMessages = errors.get(Constants.BUNDLE_ERROR_KEY);
//                    if (errorMessages.size() > 0) {
//                        String specificErrorMessage = errorMessages.get(0);
//                        if (SEARCH_NO_RESULTS.equals(specificErrorMessage)) {
//                            FeaturedBox featuredBox = new FeaturedBox();
//                            if (featuredBox.initialize(jsonObject)) {
//                                bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, featuredBox);
//                                bundle.putInt(Constants.BUNDLE_OBJECT_TYPE_KEY, FEATURE_BOX_TYPE);
//                            }
//
//                        }
//                    }
//                }
//            }
//        } catch (JSONException e) {
//            //Catches exception if there isn't errorMessage or noticeMessage
//            e.printStackTrace();
//        }
//        return parseErrorBundle(bundle);
//    }
}
