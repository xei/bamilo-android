/**
 * @author Manuel Silva
 * 
 */
package pt.rocket.helpers;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import pt.rocket.app.JumiaApplication;
import pt.rocket.framework.database.SectionsTablesHelper;
import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.objects.Section;
import pt.rocket.framework.objects.ShoppingCart;
import pt.rocket.framework.objects.VersionInfo;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import android.os.Bundle;
import android.util.Log;

/**
 * Get Shopping Cart Items helper
 * 
 * @author Manuel Silva
 * 
 */
public class GetShoppingCartItemsHelper extends BaseHelper {
    
    private static String TAG = GetShoppingCartItemsHelper.class.getSimpleName();
   
    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.GET_SHOPPING_CART_ITEMS_EVENT.action);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_NOT_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        String md5 = Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY);
        bundle.putString(Constants.BUNDLE_MD5_KEY, md5);
//        Log.i(TAG, "code1removing making callback from request type : "+ EventType.GET_SHOPPING_CART_ITEMS_EVENT);
//        Log.i(TAG, "code1removing making callback with id : "+ md5);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_SHOPPING_CART_ITEMS_EVENT);
        return bundle;
    }
    
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        android.util.Log.d("TRACK", "parseResponseBundle GetShoppingCartItemsHelper");
        JumiaApplication.INSTANCE.setCart(null);
        ShoppingCart cart = new ShoppingCart(JumiaApplication.INSTANCE.getItemSimpleDataRegistry());
        try {
            cart.initialize(jsonObject);
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        JumiaApplication.INSTANCE.setCart(cart);
        bundle.putParcelable(Constants.BUNDLE_RESPONSE_KEY, cart);
        bundle.putSerializable(Constants.BUNDLE_EVENT_TYPE_KEY, EventType.GET_SHOPPING_CART_ITEMS_EVENT);
//        long elapsed = System.currentTimeMillis() - JumiaApplication.INSTANCE.timeTrackerMap.get(EventType.GET_SHOPPING_CART_ITEMS_EVENT);
//        Log.i("REQUEST", "event type response : "+bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY)+" time spent : "+elapsed);
//        String trackValue = bundle.getSerializable(Constants.BUNDLE_EVENT_TYPE_KEY) + " : "+elapsed;
//        JumiaApplication.INSTANCE.writeToTrackerFile(trackValue);
        return bundle;
    }

    @Override
    public Bundle parseErrorBundle(Bundle bundle) {
        android.util.Log.d(TAG, "parseErrorBundle GetShoppingCartItemsHelper");
     
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
}
