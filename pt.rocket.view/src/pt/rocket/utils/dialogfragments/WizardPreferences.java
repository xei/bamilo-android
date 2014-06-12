package pt.rocket.utils.dialogfragments;



import pt.rocket.constants.ConstantsSharedPrefs;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class WizardPreferences {
    
    
    public enum WizardType {
        HOME,
        CATALOG,
        GALLERY,
        PRODUCT_DETAIL, 
        NAVIGATION
    }

    private static final String TAG = WizardPreferences.class.getSimpleName();
    
    /**
     * Function used to save session token
     * 
     * @param token
     * @author sergiopereira
     */
    public static void changeState(Context context, WizardType type) {
        // Validate context
        if(context == null){
            Log.w(TAG, "WIZARD SAVE PREFS: CONTEXT IS NULL FOR TYPE: " + type.toString());
            return;
        }
        Log.d(TAG, "WIZARD SAVE PREFS: TYPE " + type.toString());
        // Get shared prefs
        SharedPreferences sharedPreferences = context.getSharedPreferences(ConstantsSharedPrefs.WIZARDS_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        // Save
        Editor editor = sharedPreferences.edit();
        switch (type) {
        case HOME:
            editor.putBoolean(WizardType.HOME.toString(), false);
            break;
        case CATALOG:
            editor.putBoolean(WizardType.CATALOG.toString(), false);
            break;
        case GALLERY:
            editor.putBoolean(WizardType.GALLERY.toString(), false);
            break;
        case PRODUCT_DETAIL:
            editor.putBoolean(WizardType.PRODUCT_DETAIL.toString(), false);
            break;
        case NAVIGATION:
            editor.putBoolean(WizardType.NAVIGATION.toString(), false);
            break;
        default:
            break;
        }
        editor.commit();
    }

    /**
     * Function used to get the session token
     * 
     * @param context
     * @return token or null
     * @author sergiopereira
     */
    public static Boolean isFirstTime(Context context, WizardType type) {
        // Validate context
        if(context == null){
            Log.w(TAG, "WIZARD GET PREFS: CONTEXT IS NULL FOR TYPE: " + type.toString());
            return false;
        }
        Log.d(TAG, "WIZARD GET PREFS: TYPE " + type.toString());
        // Get prefs
        SharedPreferences sharedPreferences = context.getSharedPreferences(ConstantsSharedPrefs.WIZARDS_SHARED_PREFERENCES, Context.MODE_PRIVATE);
        Boolean value = true;
        switch (type) {
        case HOME:
            value = sharedPreferences.getBoolean(WizardType.HOME.toString(), true);
            break;
        case CATALOG:
            value = sharedPreferences.getBoolean(WizardType.CATALOG.toString(), true);
            break;
        case GALLERY:
            value = sharedPreferences.getBoolean(WizardType.GALLERY.toString(), true);
            break;
        case PRODUCT_DETAIL:
            value = sharedPreferences.getBoolean(WizardType.PRODUCT_DETAIL.toString(), true);
            break;
        case NAVIGATION:
            value = sharedPreferences.getBoolean(WizardType.NAVIGATION.toString(), true);
            break;
        default:
            break;
        }
        return value;
    }

}
