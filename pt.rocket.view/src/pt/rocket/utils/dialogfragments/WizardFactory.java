package pt.rocket.utils.dialogfragments;



import pt.rocket.utils.dialogfragments.WizardPreferences.WizardType;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.util.Log;

public class WizardFactory {
    
    public static final String TAG = WizardFactory.class.getSimpleName();

    /**
     * Factory used to show a wizard for first time
     * @param type
     * @param context
     * @param fragmentManager
     * @param tag
     * @author sergiopereira
     */
    public static void show(WizardType type, Context context, FragmentManager fragmentManager, String tag) {
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
            Log.w(TAG, "WARNING ON SHOW WIZARD: UNKNOWN TYPE");
            break;
        }
    }
    
}
