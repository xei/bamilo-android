package pt.rocket.preferences;



import pt.rocket.app.JumiaApplication;
import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.framework.Darwin;
import pt.rocket.framework.database.FavouriteTableHelper;
import pt.rocket.framework.database.LastViewedTableHelper;
import pt.rocket.utils.TrackerDelegator;
import android.content.Context;
import android.content.SharedPreferences;
import de.akquinet.android.androlog.Log;

/**
 * Class used to save the shared preferences for a shop
 * @author sergiopereira
 */
public class ShopPreferences {
    
    private static final String TAG = ShopPreferences.class.getSimpleName();
    
    public static final String SHOP_NOT_SELECTED = null;
    
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
    
    /**
     * Method used to set the shop with the position
     * @param context
     * @param shopPosition
     * @author sergiopereira
     */
    public static void setShopId(Context context, int shopPosition) {
        JumiaApplication.INSTANCE.cleanAllPreviousCountryValues();
        LastViewedTableHelper.deleteAllLastViewed();
        FavouriteTableHelper.deleteAllFavourite();
        
        //System.gc();
        SharedPreferences sharedPrefs = context.getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(Darwin.KEY_SELECTED_COUNTRY_ID, JumiaApplication.INSTANCE.countriesAvailable.get(shopPosition).getCountryIso().toLowerCase());
        editor.putBoolean(Darwin.KEY_COUNTRY_CHANGED, true);
        editor.putBoolean(ConstantsSharedPrefs.KEY_SHOW_PROMOTIONS, true);
        /**
         * Save the Selected Country Configs 
         * KEY_SELECTED_COUNTRY_ID will contain the Country ISO that will be use to identify the selected country al over the App.
         */
        Log.i(TAG, "code1DarwinComponent : selected : "+JumiaApplication.INSTANCE.countriesAvailable.get(shopPosition).getCountryName());
        editor.putString(Darwin.KEY_SELECTED_COUNTRY_NAME, JumiaApplication.INSTANCE.countriesAvailable.get(shopPosition).getCountryName());
        editor.putString(Darwin.KEY_SELECTED_COUNTRY_URL, JumiaApplication.INSTANCE.countriesAvailable.get(shopPosition).getCountryUrl());
        editor.putString(Darwin.KEY_SELECTED_COUNTRY_FLAG, JumiaApplication.INSTANCE.countriesAvailable.get(shopPosition).getCountryFlag());
        editor.putString(Darwin.KEY_SELECTED_COUNTRY_ISO, JumiaApplication.INSTANCE.countriesAvailable.get(shopPosition).getCountryIso().toLowerCase());
        editor.putBoolean(Darwin.KEY_SELECTED_COUNTRY_FORCE_HTTP, JumiaApplication.INSTANCE.countriesAvailable.get(shopPosition).isCountryForceHttps());
        editor.putBoolean(Darwin.KEY_SELECTED_COUNTRY_IS_LIVE, JumiaApplication.INSTANCE.countriesAvailable.get(shopPosition).isCountryIsLive());
        editor.putBoolean(ConstantsSharedPrefs.KEY_COUNTRY_CONFIGS_AVAILABLE, false);
        editor.commit();
        
        TrackerDelegator.trackShopChanged();
    }

}