/**
 * @author Manuel Silva
 * 
 */
package com.mobile.helpers.cart;

import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.framework.enums.RequestType;
import com.mobile.framework.objects.ShoppingCart;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.Utils;
import com.mobile.helpers.BaseHelper;
import com.mobile.helpers.HelperPriorityConfiguration;
import com.mobile.utils.TrackerDelegator;

import org.json.JSONException;
import org.json.JSONObject;

import de.akquinet.android.androlog.Log;

/**
 * Get Shopping Cart Items helper
 * 
 * @author Manuel Silva
 * 
 */
public class GetShoppingCartChangeItemQuantityHelper extends BaseHelper {
    
    private static String TAG = GetShoppingCartChangeItemQuantityHelper.class.getSimpleName();
    
    private static final EventType EVENT_TYPE = EventType.CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT;
    
    public static final String CART_ITEMS = "cart_items";

    public static final String ITEM_QTY = "qty_";

    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT.action);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
        bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, args.getParcelable(CART_ITEMS));
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT);
        return bundle;
    }
    
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.d("TRACK", "parseResponseBundle GetShoppingCartChangeItemQuantityHelper");
        JumiaApplication.INSTANCE.setCart(null);
        ShoppingCart cart = new ShoppingCart();
        try {
            cart.initialize(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JumiaApplication.INSTANCE.setCart(cart);
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, cart);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT);
        
        // Track the new cart value
        TrackerDelegator.trackCart(cart.getPriceForTracking(), cart.getCartCount());
        
        return bundle;
    }

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "parseErrorBundle GetShoppingCartItemsHelper");
     
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.CHANGE_ITEM_QUANTITY_IN_SHOPPING_CART_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
    
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
        return parseResponseErrorBundle(bundle);
    }
}
