/**
 * 
 */
package pt.rocket.app;

import pt.rocket.framework.ErrorCode;
import pt.rocket.utils.PushNotificationIntentReceiver;
import pt.rocket.view.R;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

import com.urbanairship.AirshipConfigOptions;
import com.urbanairship.UAirship;
import com.urbanairship.push.BasicPushNotificationBuilder;
import com.urbanairship.push.PushManager;

/**
 * @author nutzer2
 * 
 */
public class UrbanAirshipComponent extends ApplicationComponent {

    private static final String TAG = UrbanAirshipComponent.class.getSimpleName();

    private Context context;

    /*
     * (non-Javadoc)
     * 
     * @see pt.rocket.utils.ApplicationComponent#init(pt.rocket.utils.Context)
     */
    @Override
    protected ErrorCode initInternal(Application app) {
        Log.i(TAG, "INIT INTERNAL");
        context = app.getApplicationContext();
        AirshipConfigOptions options = AirshipConfigOptions.loadDefaultOptions(app);
        UAirship.takeOff(app, options);
        BasicPushNotificationBuilder bnb = new BasicPushNotificationBuilder();
        bnb.iconDrawableId = R.drawable.ic_push_status_bar;
        // Set the builder
        PushManager.shared().setNotificationBuilder(bnb);
        PushManager.shared().setIntentReceiver(PushNotificationIntentReceiver.class);
        Log.i("Jumia Application", "My Application onCreate - appid: " + PushManager.shared().getAPID());
        setUserPushSettings();
        return ErrorCode.NO_ERROR;
    }

    /**
     * Set user settings
     */
    public void setUserPushSettings() {
        Log.i(TAG, "SET USER PUSH SETTINGS");
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        if (prefs.getBoolean(context.getString(R.string.pref_notification), 
                context.getResources().getBoolean(R.bool.default_notification))) {
            Log.d(TAG, "Enable push notifications");
            PushManager.enablePush();
            PushManager
                    .shared()
                    .getPreferences()
                    .setVibrateEnabled(
                            prefs.getBoolean(context.getString(R.string.pref_vibrate), context
                                    .getResources()
                                    .getBoolean(R.bool.default_vibrate)));
            PushManager
                    .shared()
                    .getPreferences()
                    .setSoundEnabled(
                            prefs.getBoolean(context.getString(R.string.pref_sound), context
                                    .getResources()
                                    .getBoolean(R.bool.default_sound)));
        } else {
            Log.d(TAG, "Disable push notifications");
            PushManager.disablePush();
        }
    }

}
