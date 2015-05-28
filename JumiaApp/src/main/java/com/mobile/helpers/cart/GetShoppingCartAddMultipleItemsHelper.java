package com.mobile.helpers.cart;

import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventTask;
import com.mobile.framework.utils.EventType;
import com.mobile.helpers.HelperPriorityConfiguration;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.cart.ShoppingCart;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.cart.RemoveAllShoppingCart;
import com.mobile.utils.TrackerDelegator;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import de.akquinet.android.androlog.Log;

public class GetShoppingCartAddMultipleItemsHelper extends SuperBaseHelper {

    private static String TAG = GetShoppingCartAddMultipleItemsHelper.class.getSimpleName();

    public static String PRODUCT_LIST_TAG = "productList";

    public static String getProductListSkuTag(int counter) {
        return PRODUCT_LIST_TAG + "["
                + counter + "]" + "[" + GetShoppingCartAddItemHelper.PRODUCT_SKU_TAG + "]";
    }

    public static String getProductListPTag(int counter) {
        return PRODUCT_LIST_TAG + "["
                + counter + "]" + "[" + GetShoppingCartAddItemHelper.PRODUCT_TAG + "]";
    }

    public static String ADD_ITEMS = "add_items";

    private static final EventType EVENT_TYPE = EventType.ADD_ITEMS_TO_SHOPPING_CART_EVENT;

//    public static final String ORDER_PRODUCT_SOLD_OUT = "ORDER_PRODUCT_SOLD_OUT";
//
//    public static final String ORDER_PRODUCT_ADDED = "ORDER_PRODUCT_ADDED";
//
//    public static final String PRODUCT_NOT_FOUND = "PRODUCT_NOT_FOUND";

    private HashMap<String, String> productBySku;


    @Override
    public EventType getEventType() {
        return EventType.ADD_ITEMS_TO_SHOPPING_CART_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.NORMAL_TASK;
    }

    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        return productBySku;
    }

    @Override
    protected RequestBundle createRequest(Bundle args) {
        productBySku = (HashMap<String, String>) args.getSerializable(ADD_ITEMS);
        return super.createRequest(args);
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new RemoveAllShoppingCart(JumiaApplication.INSTANCE.getApplicationContext(), requestBundle, this).execute();
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Log.i(TAG, "########### ON REQUEST COMPLETE: " + baseResponse.success);
        JumiaApplication.INSTANCE.setCart(null);
        ShoppingCart cart = (ShoppingCart) baseResponse.metadata.getData();
        JumiaApplication.INSTANCE.setCart(cart);
        Log.d(TAG, "ADD CART: " + cart.getCartValue());
        // Track the new cart value
        TrackerDelegator.trackCart(cart.getPriceForTracking(), cart.getCartCount());
        // Create bundle
        Bundle bundle = generateSuccessBundle(baseResponse);
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, cart);
        mRequester.onRequestComplete(bundle);
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Log.i(TAG, "########### ON REQUEST ERROR: " + baseResponse.message);
        Bundle bundle = generateErrorBundle(baseResponse);
        mRequester.onRequestError(bundle);
    }



//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        // Create request
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, EVENT_TYPE.action);
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
//        productBySku = (HashMap<String, String>) args.getSerializable(ADD_ITEMS);
//        bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, createContentValues(productBySku));
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        return bundle;
//    }

//    private ContentValues createContentValues(HashMap<String, String> values) {
//        ContentValues valuesToReturn = new ContentValues();
//        int counter = 0;
//        if (values != null && !values.isEmpty()) {
//            for (Map.Entry<String, String> entry : values.entrySet()) {
//                valuesToReturn.put(getProductListSkuTag(counter), entry.getKey());
//                valuesToReturn.put(getProductListPTag(counter), entry.getValue());
//                counter++;
//            }
//        }
//        return valuesToReturn;
//    }

//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.d(TAG, "ON PARSE RESPONSE BUNDLE");
//
//        try {
//            ShoppingCart cart = new ShoppingCart();
//
//            cart.initialize(jsonObject);
//            JumiaApplication.INSTANCE.setCart(null);
//            JumiaApplication.INSTANCE.setCart(cart);
//            bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, cart);
//            bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//
//            // Track the new cart value
//            TrackerDelegator.trackCart(cart.getPriceForTracking(), cart.getCartCount());
//
//        } catch (JSONException e) {
//            Log.e(TAG, "ERROR PARSING CART OBJECT");
//        }
//
//        return bundle;
//    }

//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "ON PARSE ERROR BUNDLE");
//        return parseResponseErrorBundle(bundle);
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EVENT_TYPE);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
//        return parseResponseErrorBundle(bundle);
//    }


    /*
     * TODO: Validate messages from base response
     */

    //@Override
    protected void handleSuccess(JSONObject messagesObject, Bundle bundle) {
        JSONObject successObject = messagesObject.optJSONObject(RestConstants.JSON_SUCCESS_TAG);
        if (successObject != null && successObject.length() > 0) {
            bundle.putSerializable(Constants.BUNDLE_RESPONSE_SUCCESS_MESSAGE_KEY, checkAddedProducts(successObject));
        }
        handleError(messagesObject, bundle);
    }

    //@Override
    protected void handleError(JSONObject messagesObject, Bundle bundle) {
        JSONObject errorObject = messagesObject.optJSONObject(RestConstants.JSON_ERROR_TAG);
        if (errorObject != null && errorObject.length() > 0) {
            bundle.putSerializable(Constants.BUNDLE_RESPONSE_ERROR_MESSAGE_KEY, checkNotAddedProducts(errorObject));
        }
    }
    
    /**
     * Check added products from result object
     * 
     * @return Array of products sku's
     */
    protected ArrayList<String> checkAddedProducts(JSONObject sucessObject) {
        ArrayList<String> added = new ArrayList<>();
        Iterator<?> keys = sucessObject.keys();

        while (keys.hasNext()) {
            added.add(productBySku.get(keys.next()));
        }
        return added;
    }

    /**
     * Check products that were not added from result object
     * 
     * @return Array of products sku's
     */
    protected ArrayList<String> checkNotAddedProducts(JSONObject errorObject) {
        ArrayList<String> notAdded = new ArrayList<>();
        Iterator<?> keys = errorObject.keys();

        while (keys.hasNext()) {
            notAdded.add(productBySku.get(keys.next()));
        }
        return notAdded;
    }

}
