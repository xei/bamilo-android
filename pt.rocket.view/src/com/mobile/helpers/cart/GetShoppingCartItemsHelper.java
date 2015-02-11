/**
 * @author Manuel Silva
 * 
 */
package com.mobile.helpers.cart;

import org.json.JSONException;
import org.json.JSONObject;

import com.mobile.app.JumiaApplication;
import com.mobile.framework.enums.RequestType;
import com.mobile.framework.interfaces.IMetaData;
import com.mobile.framework.objects.ShoppingCart;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.Utils;
import com.mobile.helpers.BaseHelper;
import com.mobile.helpers.HelperPriorityConfiguration;
import android.os.Bundle;
import de.akquinet.android.androlog.Log;

/**
 * Get Shopping Cart Items helper
 * 
 * @author Manuel Silva
 * 
 */
public class GetShoppingCartItemsHelper extends BaseHelper {
    
    private static String TAG = GetShoppingCartItemsHelper.class.getSimpleName();
    
    private static final EventType EVENT_TYPE = EventType.GET_SHOPPING_CART_ITEMS_EVENT;
   
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.GET_SHOPPING_CART_ITEMS_EVENT.action);
        
        // If event hasn't priority IS_NOT_PRIORITARY is defined, otherwise is defined IS_PRIORITY
        if(args != null && !args.getBoolean(Constants.BUNDLE_PRIORITY_KEY)){
            bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_NOT_PRIORITARY);
        } else  {
            bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        }
        
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        String md5 = Utils.uniqueMD5(EVENT_TYPE.name());
        bundle.putString(Constants.BUNDLE_MD5_KEY, md5);
        bundle.putBoolean(IMetaData.MD_IGNORE_CACHE, true);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_SHOPPING_CART_ITEMS_EVENT);
        return bundle;
    }
    
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.d(TAG, "ON PARSE REPONSE" + jsonObject.toString());
        JumiaApplication.INSTANCE.setCart(null);
        Log.d(TAG, "CLEAN CART");        
        ShoppingCart cart = new ShoppingCart();
        try {
            cart.initialize(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JumiaApplication.INSTANCE.setCart(cart);
        Log.d(TAG, "ADD CART: " + cart.getCartValue());
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, cart);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_SHOPPING_CART_ITEMS_EVENT);
        return bundle;
    }

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "parseErrorBundle GetShoppingCartItemsHelper");
     
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_SHOPPING_CART_ITEMS_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_SHOPPING_CART_ITEMS_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
    
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
        return parseResponseErrorBundle(bundle);
    }
}