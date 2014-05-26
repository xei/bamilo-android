/**
 * 
 */
package pt.rocket.app;


import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.framework.Darwin;
import pt.rocket.framework.DarwinMode;
import pt.rocket.framework.ErrorCode;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

/**
 * @author nutzer2
 * @modified Manuel Silva
 */
public class DarwinComponent extends ApplicationComponent {
    
    private static final String TAG = DarwinComponent.class.getName();

    /* (non-Javadoc)
     * @see pt.rocket.app.ApplicationComponent#init(android.app.Application)
     */
    @Override
    public ErrorCode init(Application app) {
        return initInternal(app);
    }

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.app.ApplicationComponent#initInternal(android.app.Application)
     */
    @Override
    protected ErrorCode initInternal(Application app) {
        
        SharedPreferences sharedPrefs = app.getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String shopId = sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_ID, null);
        boolean countriesConfigs = sharedPrefs.getBoolean(Darwin.KEY_COUNTRIES_CONFIGS_LOADED, false);
        boolean countryConfigsAvailable = sharedPrefs.getBoolean(ConstantsSharedPrefs.KEY_COUNTRY_CONFIGS_AVAILABLE, false);
        boolean isChangeShop = sharedPrefs.getBoolean(Darwin.KEY_COUNTRY_CHANGED, false);
        Log.i(TAG, "DarwinComponent shopId :  "+shopId+ " countriesConfigs : "+countriesConfigs + " " + isChangeShop);
        if (shopId == null && countriesConfigs && !isChangeShop) {
            Log.i(TAG, "DarwinComponent AUTO_COUNTRY_SELECTION");
            return ErrorCode.AUTO_COUNTRY_SELECTION;
        }
        
        if(!countriesConfigs && !countryConfigsAvailable) {
            Log.i(TAG, "DarwinComponent NO_COUNTRIES_CONFIGS");
            if(Darwin.initialize(DarwinMode.DEBUG, app.getApplicationContext())){
                return ErrorCode.NO_COUNTRIES_CONFIGS;    
            }
            Log.i(TAG, "DarwinComponent NO_COUNTRIES_CONFIGS UNKNOWN_ERROR");
            return ErrorCode.UNKNOWN_ERROR;
        }
        
        if(!countryConfigsAvailable){
            Log.i(TAG, "DarwinComponent NO_COUNTRY_CONFIGS_AVAILABLE");
            if(Darwin.initialize(DarwinMode.DEBUG, app.getApplicationContext(), sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_URL, null), sharedPrefs.getString(Darwin.KEY_SELECTED_COUNTRY_REST_BASE, null))){
                return ErrorCode.NO_COUNTRY_CONFIGS_AVAILABLE;   
            }
            Log.i(TAG, "DarwinComponent NO_COUNTRY_CONFIGS_AVAILABLE UNKNOWN_ERROR");
            return ErrorCode.UNKNOWN_ERROR;
            
        }
        Log.i(TAG, "DarwinComponent shop id is : "+ shopId);
        if (Darwin.initialize(DarwinMode.DEBUG, app.getApplicationContext(), shopId, isChangeShop)) {
            Log.i(TAG, "DarwinComponent NO_ERROR");
            Editor editor = sharedPrefs.edit();
            editor.putBoolean(Darwin.KEY_COUNTRIES_CONFIGS_LOADED, false);
            editor.commit();
            return ErrorCode.NO_ERROR;
        }
        Log.i(TAG, "DarwinComponent NO_ERROR UNKNOWN_ERROR");
        return ErrorCode.UNKNOWN_ERROR;
    }

}
