/**
 * @author Manuel Silva
 * 
 */
package com.mobile.helpers.cart;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.framework.output.Print;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventTask;
import com.mobile.framework.utils.EventType;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.cart.ShoppingCart;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.cart.AddBundleShoppingCart;
import com.mobile.utils.TrackerDelegator;

import java.util.Map;

/**
 * Get Shopping Cart Items helper
 * 
 * @author Manuel Silva
 * 
 */
public class GetShoppingCartAddBundleHelper extends SuperBaseHelper {
    
    private static String TAG = GetShoppingCartAddBundleHelper.class.getSimpleName();
    
    //private static final EventType EVENT_TYPE = EventType.ADD_ITEM_TO_SHOPPING_CART_EVENT;
    
    public static final String ADD_BUNDLE = "add_bundle";
    
    public static final String BUNDLE_ID = "bundleId";
    
//  product-item-selector[0]
//  ...
    public static final String PRODUCT_SKU_TAG = "product-item-selector[";
    
//  product-simple-selector[0]
//  ... 
    public static final String PRODUCT_SIMPLE_SKU_TAG = "product-simple-selector[";


    @Override
    public EventType getEventType() {
        return EventType.ADD_PRODUCT_BUNDLE;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.NORMAL_TASK;
    }

    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        return convertContentValuesToMap((ContentValues) args.getParcelable(ADD_BUNDLE));
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new AddBundleShoppingCart(JumiaApplication.INSTANCE.getApplicationContext(), requestBundle, this).execute();
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST COMPLETE: " + baseResponse.hadSuccess());
        //
        JumiaApplication.INSTANCE.setCart(null);
        ShoppingCart cart = (ShoppingCart) baseResponse.getMetadata().getData();
        JumiaApplication.INSTANCE.setCart(cart);
        Print.d(TAG, "ADD CART: " + cart.getCartValue());
        // Track the new cart value
        TrackerDelegator.trackCart(cart.getPriceForTracking(), cart.getCartCount());
        // Create bundle
        Bundle bundle = generateSuccessBundle(baseResponse);
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, cart);
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
//     * @see com.mobile.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//
//        // Create request
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.ADD_PRODUCT_BUNDLE.action);
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
//        bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, args.getParcelable(ADD_BUNDLE));
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.ADD_PRODUCT_BUNDLE);
//        return bundle;
//    }
    
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
//     */
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.d(TAG, "ON PARSE RESPONSE BUNDLE");
//
//        JumiaApplication.INSTANCE.setCart(null);
//        ShoppingCart cart = new ShoppingCart();
//        try {
//            cart.initialize(jsonObject);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        JumiaApplication.INSTANCE.setCart(cart);
//        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, cart);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.ADD_PRODUCT_BUNDLE);
//
//        // Track the new cart value
//        TrackerDelegator.trackCart(cart.getPriceForTracking(), cart.getCartCount());
//
//
//
//        return bundle;
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle parseErrorBundle(Bundle bundle) {
//        Log.d(TAG, "parseErrorBundle GetShoppingCartItemsHelper");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.ADD_PRODUCT_BUNDLE);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        // Add specific data
//        return bundle;
//    }
//
//    /*
//     * (non-Javadoc)
//     * @see com.mobile.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle) {
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.ADD_PRODUCT_BUNDLE);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        // Add specific data
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
//        return parseResponseErrorBundle(bundle);
//    }
}
