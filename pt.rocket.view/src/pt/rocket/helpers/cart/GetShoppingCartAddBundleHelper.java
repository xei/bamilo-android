/**
 * @author Manuel Silva
 * 
 */
package pt.rocket.helpers.cart;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.app.JumiaApplication;
import pt.rocket.framework.database.FavouriteTableHelper;
import pt.rocket.framework.database.LastViewedTableHelper;
import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.objects.ShoppingCart;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import pt.rocket.helpers.BaseHelper;
import pt.rocket.helpers.HelperPriorityConfiguration;
import pt.rocket.utils.TrackerDelegator;
import android.os.Bundle;
import android.text.TextUtils;
import de.akquinet.android.androlog.Log;

/**
 * Get Shopping Cart Items helper
 * 
 * @author Manuel Silva
 * 
 */
public class GetShoppingCartAddBundleHelper extends BaseHelper {
    
    private static String TAG = GetShoppingCartAddBundleHelper.class.getSimpleName();
    
    private static final EventType EVENT_TYPE = EventType.ADD_ITEM_TO_SHOPPING_CART_EVENT;
    
    public static final String ADD_BUNDLE = "add_bundle";
    
    public static final String BUNDLE_ID = "bundleId";
    
//  product-item-selector[0]
//  ...
    public static final String PRODUCT_SKU_TAG = "product-item-selector[";
    
//  product-simple-selector[0]
//  ... 
    public static final String PRODUCT_SIMPLE_SKU_TAG = "product-simple-selector[";
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
     */
    @Override
    public Bundle generateRequestBundle(Bundle args) {

        // Create request
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.ADD_PRODUCT_BUNDLE.action);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
        bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, args.getParcelable(ADD_BUNDLE));
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.ADD_PRODUCT_BUNDLE);
        return bundle;
    }
    
    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
     */
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.d(TAG, "ON PARSE RESPONSE BUNDLE");
        
        JumiaApplication.INSTANCE.setCart(null);
        ShoppingCart cart = new ShoppingCart();
        try {
            Log.d("BUNDLE ", "ADD BUNDLE RESPONSE:"+jsonObject.toString(4));
            cart.initialize(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JumiaApplication.INSTANCE.setCart(cart);
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, cart);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.ADD_PRODUCT_BUNDLE);
        
        // Track the new cart value
        TrackerDelegator.trackCart(cart.getPriceForTracking(), cart.getCartCount());
        
       

        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "parseErrorBundle GetShoppingCartItemsHelper");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.ADD_PRODUCT_BUNDLE);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        // Add specific data
        return bundle;
    }

    /*
     * (non-Javadoc)
     * @see pt.rocket.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.ADD_PRODUCT_BUNDLE);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        // Add specific data
        return bundle;
    }
}
