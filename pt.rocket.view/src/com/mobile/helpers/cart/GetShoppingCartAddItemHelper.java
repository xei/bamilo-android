/**
 * @author Manuel Silva
 * 
 */
package com.mobile.helpers.cart;

import org.json.JSONException;
import org.json.JSONObject;

import com.mobile.app.JumiaApplication;
import com.mobile.framework.database.FavouriteTableHelper;
import com.mobile.framework.database.LastViewedTableHelper;
import com.mobile.framework.enums.RequestType;
import com.mobile.framework.objects.ShoppingCart;
import com.mobile.framework.utils.Constants;
import com.mobile.framework.utils.EventType;
import com.mobile.framework.utils.Utils;
import com.mobile.helpers.BaseHelper;
import com.mobile.helpers.HelperPriorityConfiguration;
import com.mobile.utils.TrackerDelegator;
import android.os.Bundle;
import android.text.TextUtils;
import de.akquinet.android.androlog.Log;

/**
 * Get Shopping Cart Items helper
 * 
 * @author Manuel Silva
 * 
 */
public class GetShoppingCartAddItemHelper extends BaseHelper {
    
    private static String TAG = GetShoppingCartAddItemHelper.class.getSimpleName();
    
    private static final EventType EVENT_TYPE = EventType.ADD_ITEM_TO_SHOPPING_CART_EVENT;
    
    public static final String PRODUCT_TAG = "p";
    
    public static final String PRODUCT_SKU_TAG = "sku";
    
    public static final String PRODUCT_QT_TAG = "quantity";
    
    public static final String PRODUCT_RATING_TAG = "rating";
    
    public static final String ADD_ITEM = "add_item";
    
    public static final String PRODUCT_POS_TAG = "item_pos";
    
    public static final String REMOVE_FAVOURITE_TAG = "rmv_fv";
    
    public static final String REMOVE_RECENTLYVIEWED_TAG = "rmv_rv";

    public static final String PRODUCT_PRICE_TAG = "price";
    
    private String mCurrentSku;
    
    private int mCurrentPos = -1;

    private boolean isToRemoveFromFavourite;

    private boolean isToRemoveFromLastViewed;

    private double mCurrentPrice = 0d;
    
    private double mCurrentRating = -1d;
 
    /*
     * (non-Javadoc)
     * @see com.mobile.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
     */
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        // Get specific data
        mCurrentPos = args.getInt(PRODUCT_POS_TAG, -1);
        mCurrentSku = args.getString(PRODUCT_SKU_TAG);
        mCurrentPrice  = args.getDouble(PRODUCT_PRICE_TAG);
        if(args.containsKey(PRODUCT_RATING_TAG))mCurrentRating = args.getDouble(PRODUCT_RATING_TAG);
        isToRemoveFromFavourite = args.getBoolean(REMOVE_FAVOURITE_TAG, false);
        isToRemoveFromLastViewed = args.getBoolean(REMOVE_RECENTLYVIEWED_TAG, false);
        // Create request
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.ADD_ITEM_TO_SHOPPING_CART_EVENT.action);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
        bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, args.getParcelable(ADD_ITEM));
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.ADD_ITEM_TO_SHOPPING_CART_EVENT);
        return bundle;
    }
    
    /*
     * (non-Javadoc)
     * @see com.mobile.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
     */
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.d(TAG, "ON PARSE RESPONSE BUNDLE");
        
        JumiaApplication.INSTANCE.setCart(null);
        ShoppingCart cart = new ShoppingCart();
        try {
            cart.initialize(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JumiaApplication.INSTANCE.setCart(cart);
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, cart);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.ADD_ITEM_TO_SHOPPING_CART_EVENT);
        
        // Track the new cart value
        TrackerDelegator.trackCart(cart.getPriceForTracking(), cart.getCartCount());
        
        /**
         * Favourites 
         */
        // Add specific data
        bundle.putInt(PRODUCT_POS_TAG, mCurrentPos);
        bundle.putString(PRODUCT_SKU_TAG, mCurrentSku);
        // Validate if is to remove from favourite
        if (isToRemoveFromFavourite && !TextUtils.isEmpty(mCurrentSku)) { 
            FavouriteTableHelper.removeFavouriteProduct(mCurrentSku);
            TrackerDelegator.trackRemoveFromFavorites(mCurrentSku, mCurrentPrice, mCurrentRating);
        }

        /**
         * LastViewed
         */
        // Validate if is to remove from LastViewed
        if (isToRemoveFromLastViewed && !TextUtils.isEmpty(mCurrentSku)) LastViewedTableHelper.removeLastViewed(mCurrentSku);

        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "parseErrorBundle GetShoppingCartItemsHelper");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.ADD_ITEM_TO_SHOPPING_CART_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        // Add specific data
        bundle.putInt(PRODUCT_POS_TAG, mCurrentPos);
        bundle.putString(PRODUCT_SKU_TAG, mCurrentSku);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see com.mobile.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.ADD_ITEM_TO_SHOPPING_CART_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        // Add specific data
        bundle.putInt(PRODUCT_POS_TAG, mCurrentPos);
        bundle.putString(PRODUCT_SKU_TAG, mCurrentSku);
        return bundle;
    }
    
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle, JSONObject jsonObject) {
        return parseResponseErrorBundle(bundle);
    }
}
