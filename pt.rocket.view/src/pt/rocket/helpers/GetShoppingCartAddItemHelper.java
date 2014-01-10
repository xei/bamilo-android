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

import pt.rocket.framework.database.SectionsTablesHelper;
import pt.rocket.framework.enums.RequestType;
import pt.rocket.framework.objects.Section;
import pt.rocket.framework.objects.ShoppingCart;
import pt.rocket.framework.objects.ShoppingCartItem;
import pt.rocket.framework.objects.VersionInfo;
import pt.rocket.framework.rest.RestConstants;
import pt.rocket.framework.utils.Constants;
import pt.rocket.framework.utils.EventType;
import pt.rocket.framework.utils.Utils;
import pt.rocket.utils.JumiaApplication;
import android.content.ContentValues;
import android.os.Bundle;

/**
 * Get Shopping Cart Items helper
 * 
 * @author Manuel Silva
 * 
 */
public class GetShoppingCartAddItemHelper extends BaseHelper {
    
    private static String TAG = GetShoppingCartAddItemHelper.class.getSimpleName();
    
    public static final String ADD_ITEM = "add_item";

    @Override
    public Bundle generateRequestBundle(Bundle args) {
        Bundle bundle = new Bundle();
        
        
        bundle.putString(Constants.BUNDLE_URL_KEY, EventType.GET_SHOPPING_CART_ITEMS_EVENT.action);
        bundle.putBoolean(Constants.BUNDLE_PRIORITY_KEY, HelperPriorityConfiguration.IS_NOT_PRIORITARY);
        bundle.putSerializable(Constants.BUNDLE_TYPE_KEY, RequestType.GET);
        bundle.putParcelable(Constants.BUNDLE_FORM_DATA_KEY, args.getParcelable(ADD_ITEM));
        bundle.putString(Constants.BUNDLE_MD5_KEY, Utils.uniqueMD5(Constants.BUNDLE_MD5_KEY));
        return bundle;
    }
    
    @Override
    public Bundle parseResponseBundle(Bundle bundle, JSONObject jsonObject) {
        android.util.Log.d("TRACK", "parseResponseBundle GetShoppingCartAddItemHelper");
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


        return bundle;
    }

    @Override   
    public Bundle parseErrorBundle(Bundle bundle) {
    	android.util.Log.d("TRACK", "parseErrorBundle GetShoppingCartItemsHelper");
        bundle.putString(Constants.BUNDLE_URL_KEY, " GetShoppingCartItemsHelper");
        return bundle;
    }
}
