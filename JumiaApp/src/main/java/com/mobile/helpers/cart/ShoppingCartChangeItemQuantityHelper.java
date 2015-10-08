/**
 * @author Manuel Silva
 * 
 */
package com.mobile.helpers.cart;

import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.helpers.SuperBaseHelper;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.requests.BaseRequest;
import com.mobile.newFramework.requests.RequestBundle;
import com.mobile.newFramework.rest.interfaces.AigApiInterface;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.EventTask;
import com.mobile.newFramework.utils.EventType;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.TrackerDelegator;

/**
 * Get Shopping Cart Items helper
 * 
 * @author Manuel Silva
 * 
 */
public class ShoppingCartChangeItemQuantityHelper extends SuperBaseHelper {
    
    private static String TAG = ShoppingCartChangeItemQuantityHelper.class.getSimpleName();
    
    //private static final EventType EVENT_TYPE = EventType.CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT;
    
    public static final String CART_ITEMS = "cart_items";

    public static final String ITEM_QTY = "qty_";


    @Override
    public EventType getEventType() {
        return EventType.CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT;
    }

    @Override
    protected EventTask setEventTask() {
        return EventTask.SMALL_TASK;
    }

    @Override
    public void onRequest(RequestBundle requestBundle) {
//        new UpdateQuantityShoppingCart(requestBundle, this).execute();
        new BaseRequest(requestBundle, this).execute(AigApiInterface.updateQuantityShoppingCart);
    }

    @Override
    public void createSuccessBundleParams(BaseResponse baseResponse, Bundle bundle) {
        super.createSuccessBundleParams(baseResponse, bundle);

        //TODO move to observable
        JumiaApplication.INSTANCE.setCart(null);
        PurchaseEntity cart = (PurchaseEntity) baseResponse.getMetadata().getData();
        JumiaApplication.INSTANCE.setCart(cart);
        Print.d(TAG, "ADD CART: " + cart.getTotal());
        // Track the new cart value
        TrackerDelegator.trackCart(cart.getPriceForTracking(), cart.getCartCount(), cart.getAttributeSetIdList());

        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, cart);
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
