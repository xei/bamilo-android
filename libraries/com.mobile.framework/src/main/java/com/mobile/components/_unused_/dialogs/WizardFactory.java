package com.mobile.components._unused_.dialogs;


import android.content.Context;

import com.mobile.newFramework.utils.output.Print;

@SuppressWarnings("unused")
public class WizardFactory {
    
    public static final String TAG = WizardFactory.class.getSimpleName();

    /**
     * Factory used to show a wizard for first time
     */
    public static void show(WizardPreferences.WizardType type, Context context) {
        // Validate if is first time
        if(!WizardPreferences.isFirstTime(context, type)) return;
        // Show the wizard    
        switch (type) {
        case HOME:
            break;
        case CATALOG:
            break;
        case GALLERY:
            break;
        case PRODUCT_DETAIL:
            break;
        case NAVIGATION:
            // Removed Categories TAB
            /*Log.i(TAG, "ON SHOW NAVIGATION WIZARD");
            WizardGenericFragment.getInstance(type, R.layout.wizard_navigation_categories, R.id.wizard_nav_button).show(fragmentManager, tag);*/
            break;
        default:
            Print.w(TAG, "WARNING ON SHOW WIZARD: UNKNOWN TYPE");
            break;
        }
    }
    
}
