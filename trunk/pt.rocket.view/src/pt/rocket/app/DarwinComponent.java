/**
 * 
 */
package pt.rocket.app;

import pt.rocket.constants.ConstantsSharedPrefs;
import pt.rocket.framework.Darwin;
import pt.rocket.framework.DarwinMode;
import pt.rocket.framework.ErrorCode;
import pt.rocket.view.ChangeCountryActivity;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

/**
 * @author nutzer2
 * 
 */
public class DarwinComponent extends ApplicationComponent {
    
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
        SharedPreferences sharedPrefs = app.getSharedPreferences(
                ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        int shopId = sharedPrefs.getInt(ChangeCountryActivity.KEY_COUNTRY, -1);
        if (shopId == -1) {
            Intent intent = new Intent(app, ChangeCountryActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            app.startActivity(intent);
            return ErrorCode.REQUIRES_USER_INTERACTION;
        }
        if (Darwin.initialize(DarwinMode.DEBUG, app.getApplicationContext(), shopId)) {
            return ErrorCode.NO_ERROR;
        }
        return ErrorCode.UNKNOWN_ERROR;
    }

}
