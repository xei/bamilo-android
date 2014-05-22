package pt.rocket.preferences;



import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.framework.Darwin;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Class used to save the shared preferences for a shop
 * @author sergiopereira
 */
public class ShopPreferences {
    
    private static final String TAG = ShopPreferences.class.getSimpleName();
    
    private static final String SHOP_NOT_SELECTED = null;
    
    /**
     * Function used to get the shop id
     * 
     * @author sergiopereira
     */
    public static String getShopId(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String shopId = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_ID, SHOP_NOT_SELECTED);
        Log.d(TAG, "SHOP ID: " + shopId);
        return shopId;
    }

}