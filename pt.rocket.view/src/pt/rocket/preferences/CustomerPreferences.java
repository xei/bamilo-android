package pt.rocket.preferences;



import pt.rocket.constants.ConstantsSharedPrefs;
import android.content.Context;
import android.content.SharedPreferences;
import de.akquinet.android.androlog.Log;

/**
 * Class used to save the shared preferences for customer            
 * @author sergiopereira
 */
public class CustomerPreferences {
    
    private static final String TAG = CustomerPreferences.class.getSimpleName();
    
    /**
     * Function used to persist user email or empty that value after successfull login
     * 
     * @author sergiopereira
     */
    public static void setRememberedEmail(Context context, String email) {
        Log.i(TAG, "SET REMMBERED EMAIL: " + email);
        SharedPreferences sharedPrefs = context.getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPrefs.edit();
        editor.putString(ConstantsSharedPrefs.KEY_REMEMBERED_EMAIL, email);
        editor.commit();
    }
    
    /**
     * Function used to return the user email or null value
     * @return email or null
     * @author sergiopereira
     */    
    public static String getRememberedEmail(Context context) {
        SharedPreferences sharedPrefs = context.getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        String rememberedEmail = sharedPrefs.getString(ConstantsSharedPrefs.KEY_REMEMBERED_EMAIL, null);
        Log.i(TAG, "GET REMMBERED EMAIL: " + rememberedEmail);
        return rememberedEmail;
    }
    
    /**
     * TODO : Add here more customer saved prefs
     */

}