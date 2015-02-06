/**
 * 
 */
package com.mobile.helpers.account;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.mobile.framework.enums.RequestType;
import com.mobile.framework.objects.Order;
import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.Utils;
import com.mobile.helpers.BaseHelper;
import com.mobile.helpers.HelperPriorityConfiguration;
import android.net.Uri;
import android.net.Uri.Builder;
import android.os.Bundle;
import de.akquinet.android.androlog.Log;

/**
 * Helper used to retrieve order history
 * 
 * @author Paulo Carvalho
 */
public class GetMyOrdersListHelper extends BaseHelper {

    private static String TAG = GetMyOrdersListHelper.class.getSimpleName();

    private static final EventType EVENT_TYPE = EventType.GET_MY_ORDERS_LIST_EVENT;

    public static final String PAGE_NUMBER = "page";
    public static final String PER_PAGE = "per_page";
    public static final String CURRENT_PAGE = "current_page";
    public static final String TOTAL_PAGES = "total_pages";

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
     */
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, getRequestURI(args));
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
        return bundle;
    }

    private String getRequestURI(Bundle args) {
        Uri productsUri;
        int pageNumber;
        int totalCount;
        productsUri = Uri.parse(EVENT_TYPE.action);
        Builder uriBuilder = productsUri.buildUpon();

        if (args != null) {
            if (args.containsKey(PAGE_NUMBER)){
                pageNumber = args.getInt(PAGE_NUMBER);
                uriBuilder.appendQueryParameter(PAGE_NUMBER, "" + pageNumber);
            }
            if (args.containsKey(PER_PAGE)){
                totalCount = args.getInt(PER_PAGE);
                uriBuilder.appendQueryParameter(PER_PAGE, "" + totalCount);
            }
        }
        Log.w(TAG, "REQUEST: " + uriBuilder.build().toString());

        return uriBuilder.build().toString();
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
     */
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        try {

            int currentPage = 0;
            int numPages = 0;
            JSONObject paginationObject = jsonObject.optJSONObject(RestConstants.JSON_ORDER_PAGINATION_TAG);
            currentPage = paginationObject.optInt(RestConstants.JSON_ORDER_CURRENT_PAGE_TAG, 0);
            numPages = paginationObject.optInt(RestConstants.JSON_ORDER_TOTAL_PAGES_TAG, 0);
            
            int totalOrders = jsonObject.optInt(RestConstants.JSON_ORDER_TOTAL_NUM_TAG, -1);
            Log.d(TAG, "ORDERS TOTAL: " + totalOrders);
            ArrayList<Order> orders = new ArrayList<Order>();
            // Get order history
            JSONArray ordersArray = jsonObject.optJSONArray(RestConstants.JSON_ORDERS_TAG);
            if (null != ordersArray && ordersArray.length() > 0)
                for (int i = 0; i < ordersArray.length(); i++) {
                    Order order = new Order(ordersArray.getJSONObject(i));
                    if (totalOrders > 0)
                        order.setTotalOrdersHistory(totalOrders);
                    orders.add(order);
                }

            bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, orders);
            bundle.putInt(CURRENT_PAGE, currentPage);
            bundle.putInt(TOTAL_PAGES, numPages);
        } catch (JSONException e) {
            Log.w(TAG, "ERROR ON PARSE: " + e.getMessage());
            return parseErrorBundle(bundle);
        }
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "PARSE ERROR BUNDLE");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.mobile.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        Log.d(TAG, "PARSE RESPONSE BUNDLE");
//        try {
//            if (bundle.containsKey(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY)) {
//                HashMap<String, List<String>> errorsmap = (HashMap<String, List<String>>) bundle
//                        .getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
//                bundle.putSerializable(Constants.BUNDLE_ERROR_KEY,
//                        errorsmap.get(RestConstants.JSON_ERROR_TAG).get(0));
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
    
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
        return parseResponseErrorBundle(bundle);
    }
}
