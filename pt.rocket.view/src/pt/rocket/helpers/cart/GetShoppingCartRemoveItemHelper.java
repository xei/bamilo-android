/**
 * @author Manuel Silva
 * 
 */
package pt.rocket.helpers.cart;

import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.app.JumiaApplication;
import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.objects.ShoppingCart;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import pt.rocket.helpers.BaseHelper;
import pt.rocket.helpers.HelperPriorityConfiguration;
import android.os.Bundle;
import de.akquinet.android.androlog.Log;

/**
 * Get Shopping Cart Items helper
 * 
 * @author Manuel Silva
 * 
 */
public class GetShoppingCartRemoveItemHelper extends BaseHelper {

    private static String TAG = GetShoppingCartRemoveItemHelper.class.getSimpleName();

    private static final EventType EVENT_TYPE = EventType.REMOVE_ITEM_FROM_SHOPPING_CART_EVENT;

    public static final String ITEM = "item";

    public static final String UPDATE_CART = "update_cart";

    private boolean isToUpdateCart = true;

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.helpers.BaseHelper#generateRequestBundle(android.os.Bundle)
     */
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Log.d(TAG, "ON REQUEST");
        // Set isToUpdateCart to false if UPDATE_CART that came from Bundle is false
        // Used when parsing response to update Global Cart if isToUpdateCart is true
        isToUpdateCart = args.getBoolean(UPDATE_CART, true);

        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.REMOVE_ITEM_FROM_SHOPPING_CART_EVENT.action);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.POST);
        bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, args.getParcelable(ITEM));
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(EVENT_TYPE.name()));
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.REMOVE_ITEM_FROM_SHOPPING_CART_EVENT);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.helpers.BaseHelper#parseResponseBundle(android.os.Bundle, org.json.JSONObject)
     */
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        Log.d(TAG, "ON PARSE RESPONSE");
        // Don't continue if isToUpdateCart was set as false on generateRequestBundle()
        if (!isToUpdateCart) return null;

        JumiaApplication.INSTANCE.setCart(null);
        ShoppingCart cart = new ShoppingCart();
        try {
            cart.initialize(jsonObject);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JumiaApplication.INSTANCE.setCart(cart);
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, cart);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.REMOVE_ITEM_FROM_SHOPPING_CART_EVENT);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.helpers.BaseHelper#parseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        Log.d(TAG, "ON PARSE ERROR");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.REMOVE_ITEM_FROM_SHOPPING_CART_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.helpers.BaseHelper#parseResponseErrorBundle(android.os.Bundle)
     */
    @Override
    public Bundle parseResponseErrorBundle(Bundle bundle) {
        Log.d(TAG, "ON PARSE RESPONSE ERROR");
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.REMOVE_ITEM_FROM_SHOPPING_CART_EVENT);
        bundle.putBoolean(Constants.BUNDLE_ERROR_OCURRED_KEY, true);
        return bundle;
    }
}
