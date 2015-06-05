/**
 * 
 */
package com.mobile.helpers.account;

import android.os.Bundle;

import com.mobile.framework.output.Print;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventTask;
import com.mobile.framework.utils.EventType;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.SuperOrder;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.orders.GetOrdersList;

import java.util.HashMap;
import java.util.Map;

/**
 * Helper used to retrieve order history
 * 
 * @author Paulo Carvalho
 */
public class GetMyOrdersListHelper extends SuperBaseHelper {

    private static String TAG = GetMyOrdersListHelper.class.getSimpleName();

    public static final String PAGE_NUMBER = "page";

    public static final String PER_PAGE = "per_page";

    public static final String CURRENT_PAGE = "current_page";

    public static final String TOTAL_PAGES = "total_pages";


    @Override
    public EventType getEventType() {
        return EventType.GET_MY_ORDERS_LIST_EVENT;
    }

    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        Map<String, String> data = new HashMap<>();
        data.put(PAGE_NUMBER, ""+args.getInt(PAGE_NUMBER));
        data.put(PER_PAGE, ""+args.getInt(PER_PAGE));
        return data;
    }

    @Override
    protected void onRequest(RequestBundle requestBundle) {
        new GetOrdersList(requestBundle, this).execute();
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST COMPLETE: " + baseResponse.hadSuccess());
        SuperOrder orders = (SuperOrder) baseResponse.getMetadata().getData();
        // Create bundle
        Bundle bundle = generateSuccessBundle(baseResponse);
        // Get order summary from response
        bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, orders.getOrders());
        bundle.putInt(CURRENT_PAGE, orders.getCurrentPage());
        bundle.putInt(TOTAL_PAGES, orders.getTotalOrders());
        mRequester.onRequestComplete(bundle);
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST ERROR: " + baseResponse.getMessage());
        Bundle bundle = generateErrorBundle(baseResponse);
        mRequester.onRequestError(bundle);
    }





//    /*
//     * (non-Javadoc)
//     *
//     * @see com.mobile.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, getRequestURI(args));
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        return bundle;
//    }
//
//    private String getRequestURI(Bundle args) {
//        Uri productsUri;
//        int pageNumber;
//        int totalCount;
//        productsUri = Uri.parse(EVENT_TYPE.action);
//        Builder uriBuilder = productsUri.buildUpon();
//
//        if (args != null) {
//            if (args.containsKey(PAGE_NUMBER)){
//                pageNumber = args.getInt(PAGE_NUMBER);
//                uriBuilder.appendQueryParameter(PAGE_NUMBER, "" + pageNumber);
//            }
//            if (args.containsKey(PER_PAGE)){
//                totalCount = args.getInt(PER_PAGE);
//                uriBuilder.appendQueryParameter(PER_PAGE, "" + totalCount);
//            }
//        }
//        Log.w(TAG, "REQUEST: " + uriBuilder.build().toString());
//
//        return uriBuilder.build().toString();
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see com.mobile.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
//     */
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        try {
//
//            int currentPage = 0;
//            int numPages = 0;
//            JSONObject paginationObject = jsonObject.optJSONObject(RestConstants.JSON_ORDER_PAGINATION_TAG);
//            currentPage = paginationObject.optInt(RestConstants.JSON_ORDER_CURRENT_PAGE_TAG, 0);
//            numPages = paginationObject.optInt(RestConstants.JSON_ORDER_TOTAL_PAGES_TAG, 0);
//
//            int totalOrders = jsonObject.optInt(RestConstants.JSON_ORDER_TOTAL_NUM_TAG, -1);
//            Log.d(TAG, "ORDERS TOTAL: " + totalOrders);
//            ArrayList<Order> orders = new ArrayList<>();
//            // Get order history
//            JSONArray ordersArray = jsonObject.optJSONArray(RestConstants.JSON_ORDERS_TAG);
//            if (null != ordersArray && ordersArray.length() > 0)
//                for (int i = 0; i < ordersArray.length(); i++) {
//                    Order order = new Order(ordersArray.getJSONObject(i));
//                    if (totalOrders > 0)
//                        order.setTotalOrdersHistory(totalOrders);
//                    orders.add(order);
//                }
//
//            bundle.putParcelableArrayList(Constants.BUNDLE_RESPONSE_KEY, orders);
//            bundle.putInt(CURRENT_PAGE, currentPage);
//            bundle.putInt(TOTAL_PAGES, numPages);
//        } catch (JSONException e) {
//            Log.w(TAG, "ERROR ON PARSE: " + e.getMessage());
//            return parseErrorBundle(bundle);
//        }
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        return bundle;
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see com.mobile.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "PARSE ERROR BUNDLE");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    /*
//     * (non-Javadoc)
//     *
//     * @see com.mobile.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "PARSE RESPONSE BUNDLE");
////        try {
////            if (bundle.containsKey(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY)) {
////                HashMap<String, List<String>> errorsmap = (HashMap<String, List<String>>) bundle
////                        .getSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY);
////                bundle.putSerializable(Constants.BUNDLE_ERROR_KEY,
////                        errorsmap.get(RestConstants.JSON_ERROR_TAG).get(0));
////            }
////        } catch (Exception e) {
////            e.printStackTrace();
////        }
//
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
//        return parseResponseErrorBundle(bundle);
//    }
}
