/**
 * @author Manuel Silva
 * 
 */
package com.mobile.helpers.cart;

import android.content.ContentValues;
import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventTask;
import com.mobile.framework.utils.EventType;
import com.mobile.helpers.HelperPriorityConfiguration;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.cart.ShoppingCart;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.requests.cart.UpdateQuantityShoppingCart;
import com.mobile.utils.TrackerDelegator;

import java.util.Map;

import de.akquinet.android.androlog.Log;

/**
 * Get Shopping Cart Items helper
 * 
 * @author Manuel Silva
 * 
 */
public class GetShoppingCartChangeItemQuantityHelper extends SuperBaseHelper {
    
    private static String TAG = GetShoppingCartChangeItemQuantityHelper.class.getSimpleName();
    
    //private static final EventType EVENT_TYPE = EventType.CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT;
    
    public static final String CART_ITEMS = "cart_items";

    public static final String ITEM_QTY = "qty_";


    @Override
    public EventType getEventType() {
        return EventType.CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.NORMAL_TASK;
    }

    @Override
    protected Map<String, String> getRequestData(Bundle args) {
        return convertContentValuesToMap((ContentValues) args.getParcelable(CART_ITEMS));
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
        new UpdateQuantityShoppingCart(JumiaApplication.INSTANCE.getApplicationContext(), requestBundle, this).execute();
    }

    @Override
    public void onRequestComplete(BaseResponse baseResponse) {
        Log.i(TAG, "########### ON REQUEST COMPLETE: " + baseResponse.success);
        //
        JumiaApplication.INSTANCE.setCart(null);
        ShoppingCart cart = (ShoppingCart) baseResponse.metadata.getData();
        JumiaApplication.INSTANCE.setCart(cart);
        Log.d(TAG, "ADD CART: " + cart.getCartValue());
        // Track the new cart value
        TrackerDelegator.trackCart(cart.getPriceForTracking(), cart.getCartCount());
        // Create bundle
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, mEventType);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_NOT_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TASK, EventTask.NORMAL_TASK);
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, cart);
        mRequester.onRequestComplete(bundle);
    }

    @Override
    public void onRequestError(BaseResponse baseResponse) {
        Log.i(TAG, "########### ON REQUEST ERROR: " + baseResponse.message);
        Bundle bundle = new Bundle();
        bundle.putSerializable(Constants.BUNDLE_ERROR_KEY, baseResponse.error.getErrorCode());
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, mEventType);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        mRequester.onRequestError(bundle);
    }



//    @Override
//    public Bundle generateRequestBundle(Bundle args) {
//        Bundle bundle = new Bundle();
//        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT.action);
//        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
//        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
//        bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, args.getParcelable(CART_ITEMS));
//        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT);
//        return bundle;
//    }
    
//    @Override
//    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
//        Log.d("TRACK", "parseResponseBundle GetShoppingCartChangeItemQuantityHelper");
//        JumiaApplication.INSTANCE.setCart(null);
//        ShoppingCart cart = new ShoppingCart();
//        try {
//            cart.initialize(jsonObject);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        JumiaApplication.INSTANCE.setCart(cart);
//        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, cart);
//        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT);
//
//        // Track the new cart value
//        TrackerDelegator.trackCart(cart.getPriceForTracking(), cart.getCartCount());
//
//        return bundle;
//    }


}
