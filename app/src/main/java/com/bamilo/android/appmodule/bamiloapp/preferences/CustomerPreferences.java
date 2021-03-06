package com.bamilo.android.appmodule.bamiloapp.preferences;


import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsSharedPrefs;
import com.bamilo.android.framework.service.utils.Constants;

/**
 * Class used to save the shared preferences for customer            
 * @author sergiopereira
 */
public class CustomerPreferences {
    
    private static final String TAG = CustomerPreferences.class.getSimpleName();
    
    private static final String GRID_CATALOG_TYPE = "2";
    /*
     * ############# BASE #############
     */
    
    /**
     * Load a value from key.
     * @return String or null if not exist
     * @author sergiopereira
     */
    private static String load(Context context, String key) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        return sharedPrefs.getString(key, null);
    }
    
    /**
     * Store a pair key value.
     * @author sergiopereira
     */
    private static void store(Context context, String key, String value) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(key, value);
        editor.apply();
    }

    /**
     * Function used to persist user email or empty that value after successfully login
     * 
     * @author sergiopereira
     */
    public static void setRememberedEmail(Context context, String email) {
        store(context, ConstantsSharedPrefs.KEY_REMEMBERED_EMAIL, email);
    }
    
    /**
     * Function used to return the user email or null value
     * @return email or null
     * @author sergiopereira
     */    
    public static String getRememberedEmail(Context context) {
        return load(context, ConstantsSharedPrefs.KEY_REMEMBERED_EMAIL);
    }
    
    /**
     * Get
     */
    public static String getCatalogLayout(Context context) {
        String level = load(context, ConstantsSharedPrefs.KEY_CATALOG_VIEW);
        return TextUtils.isEmpty(level) ? GRID_CATALOG_TYPE : level;
    }
    
    /**
     * Set 
     */
    public static void saveCatalogLayout(Context context, String level) {
        store(context, ConstantsSharedPrefs.KEY_CATALOG_VIEW, level);
    }
    
    /*
     * TODO : Add here more customer saved prefs
     */

}