/**
 * 
 */
package pt.rocket.app;

import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.framework.Darwin;
import pt.rocket.framework.DarwinMode;
import pt.rocket.framework.ErrorCode;
import pt.rocket.view.ChangeCountryFragmentActivity;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

/**
 * @author nutzer2
 * 
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
        int shopId = sharedPrefs.getInt(ChangeCountryFragmentActivity.KEY_COUNTRY, -1);
        boolean isChangeShop = sharedPrefs.getBoolean(ChangeCountryFragmentActivity.KEY_COUNTRY_CHANGED, false);
        if (shopId == -1) {
            Log.i(TAG, "DarwinComponent 1");
            return ErrorCode.REQUIRES_USER_INTERACTION;
        }
        Log.i(TAG, "DarwinComponent shop id is : "+ shopId);
        if (Darwin.initialize(DarwinMode.DEBUG, app.getApplicationContext(), shopId, isChangeShop)) {
            return ErrorCode.NO_ERROR;
        }
        return ErrorCode.UNKNOWN_ERROR;
    }

}
