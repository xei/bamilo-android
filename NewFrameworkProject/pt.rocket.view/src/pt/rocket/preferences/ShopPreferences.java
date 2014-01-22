package pt.rocket.preferences;



import pt.rocket.constants.ConstantsSharedPrefs;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * Class used to save the shared preferences for a shop
 * @author sergiopereira
 */
public class ShopPreferences {
    
    private static final String TAG = ShopPreferences.class.getSimpleName();
    
    private static final int SHOP_NOT_SELECTED = -1;
    
    /**
     * Function used to get the shop id
     * 
     * @author sergiopereira
     */
    public static int getShopId(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        int shopId = sharedPrefs.getInt(ConstantsSharedPrefs.KEY_COUNTRY, SHOP_NOT_SELECTED);
        Log.d(TAG, "SHOP ID: " + shopId);
        return shopId;
    }

}