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
import com.mobile.helpers.HelperPriorityConfiguration;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.cart.ShoppingCart;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.cart.RemoveItemShoppingCart;
import com.mobile.utils.TrackerDelegator;

import java.util.Map;

/**
 * Get Shopping Cart Items helper
 * 
 * @author Manuel Silva
 * 
 */
public class GetShoppingCartRemoveItemHelper extends SuperBaseHelper {

    private static String TAG = GetShoppingCartRemoveItemHelper.class.getSimpleName();

    // private static final EventType EVENT_TYPE = EventType.REMOVE_ITEM_FROM_SHOPPING_CART_EVENT;

    public static final String ITEM = "item";

    public static final String UPDATE_CART = "update_cart";

    private boolean isToUpdateCart = true;

    @Override
    public EventType getEventType() {
        return EventType.REMOVE_ITEM_FROM_SHOPPING_CART_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.NORMAL_TASK;
    }

    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        return convertContentValuesToMap((ContentValues) args.getParcelable(ITEM));
    }

    @Override
    protected RequestBundle createRequest(Bundle args) {
        isToUpdateCart = args.getBoolean(UPDATE_CART, true);
        return super.createRequest(args);
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new RemoveItemShoppingCart(JumiaApplication.INSTANCE.getApplicationContext(), requestBundle, this).execute();
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST COMPLETE: " + baseResponse.hadSuccess());

        // TODO: VALIDATE THIS ???
        // Don't continue if isToUpdateCart was set as false on generateRequestBundle()
        if (!isToUpdateCart) return;

        JumiaApplication.INSTANCE.setCart(null);
        ShoppingCart cart = (ShoppingCart) baseResponse.getMetadata().getData();
        JumiaApplication.INSTANCE.setCart(cart);
        Print.d(TAG, "ADD CART: " + cart.getCartValue());
        // Track the new cart value
        TrackerDelegator.trackCart(cart.getPriceForTracking(), cart.getCartCount());
        // Create bundle
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, mEventType);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TASK, EventTask.NORMAL_TASK);
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, cart);
        mRequester.onRequestComplete(bundle);
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Print.i(TAG, "########### ON REQUEST ERROR: " + baseResponse.getMessage());
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BUNDLE_ERROR_KEY, baseResponse.getError().getErrorCode());
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, mEventType);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        mRequester.onRequestError(bundle);
    }


//    /*
//     * (non-Javadoc)
//     *
//     * @see com.mobile.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
//     */
//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Log.d(TAG, "ON REQUEST");
//        // Set isToUpdateCart to false if UPDATE_CART that came from Bundle is false
//        // Used when parsing response to update Global Cart if isToUpdateCart is true
//        isToUpdateCart = args.getBoolean(UPDATE_CART, true);
//
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.REMOVE_ITEM_FROM_SHOPPING_CART_EVENT.action);
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
//        bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, args.getParcelable(ITEM));
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.REMOVE_ITEM_FROM_SHOPPING_CART_EVENT);
//        return bundle;
//    }

//    /*
//     * (non-Javadoc)
//     *
//     * @see com.mobile.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
//     */
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.d(TAG, "ON PARSE RESPONSE");
//        // Don't continue if isToUpdateCart was set as false on generateRequestBundle()
//        if (!isToUpdateCart) return null;
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
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.REMOVE_ITEM_FROM_SHOPPING_CART_EVENT);
//
//        // Track the new cart value
//        TrackerDelegator.trackCart(cart.getPriceForTracking(), cart.getCartCount());
//
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
//        Log.d(TAG, "ON PARSE ERROR");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.REMOVE_ITEM_FROM_SHOPPING_CART_EVENT);
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
//        Log.d(TAG, "ON PARSE RESPONSE ERROR");
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.REMOVE_ITEM_FROM_SHOPPING_CART_EVENT);
//        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
//        return bundle;
//    }
//
//    @Override
//    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
//        return parseResponseErrorBundle(bundle);
//    }
}
