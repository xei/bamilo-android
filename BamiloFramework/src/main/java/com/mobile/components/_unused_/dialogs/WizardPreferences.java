package com.mobile.components._unused_.dialogs;


import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.mobile.service.utils.output.Print;

public class WizardPreferences {
    
    
    public enum WizardType {
        HOME,
        CATALOG,
        GALLERY,
        PRODUCT_DETAIL, 
        NAVIGATION,
        SIZE_GUIDE
    }

    private static final String TAG = WizardPreferences.class.getSimpleName();

    public static String WIZARDS_SHARED_PREFERENCES = "wizards_prefs";
    
    /**
     * Function used to save session token
     * @author sergiopereira
     */
    public static void changeState(Context context, WizardType type) {
        // Validate context
        if(context == null){
            Print.w(TAG, "WIZARD SAVE PREFS: CONTEXT IS NULL FOR TYPE: " + type.toString());
            return;
        }
        Print.d(TAG, "WIZARD SAVE PREFS: TYPE " + type.toString());
        // Get shared prefs
        SharedPreferences sharedPreferences = context.getSharedPreferences(WIZARDS_SHARED_PREFERENCES, Context.MODE_PRIVATE);
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
        case SIZE_GUIDE:
            editor.putBoolean(WizardType.SIZE_GUIDE.toString(), false);
            break;
        default:
            break;
        }
        editor.apply();
    }

    /**
     * Function used to get the session token
     */
    public static Boolean isFirstTime(Context context, WizardType type) {
        // Validate context
        if(context == null){
            Print.w(TAG, "WIZARD GET PREFS: CONTEXT IS NULL FOR TYPE: " + type.toString());
            return false;
        }
        Print.d(TAG, "WIZARD GET PREFS: TYPE " + type.toString());
        // Get prefs
        SharedPreferences sharedPreferences = context.getSharedPreferences(WIZARDS_SHARED_PREFERENCES, Context.MODE_PRIVATE);
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
        case SIZE_GUIDE:
            value = sharedPreferences.getBoolean(WizardType.SIZE_GUIDE.toString(), true);
        default:
            break;
        }
        return value;
    }

}
