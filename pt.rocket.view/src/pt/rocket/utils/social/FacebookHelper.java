package pt.rocket.utils.social;

import java.util.Arrays;

import pt.rocket.utils.ui.UIUtils;
import pt.rocket.view.R;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;

import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.widget.LoginButton;

/**
 * 
 * @author sergiopereira
 *
 */
public class FacebookHelper {
    
    private static final String TAG = FacebookHelper.class.getSimpleName();
    
    private static final String FB_PERMISSION_EMAIL = "email";

    /**
     * Method used to show or hide facebook views
     * @param context
     * @param views
     * @author sergiopereira
     */
    public final static void showOrHideFacebookButton(Fragment fragment, View... views) {
        // Validate the Facebook configuration
        Boolean hideFacebook = fragment.getResources().getBoolean(R.bool.is_bamilo_specific);
        Log.i(TAG, "FACEBOOK IS ENABLED: " + hideFacebook);
        // Case hide Facebook view
        if(hideFacebook) UIUtils.showOrHideViews(View.GONE, views);
        // Case show Facebook view
        else showFacebookViews(fragment, views);
    }
    
    /**
     * Set the Facebook button
     * @param button
     * @param fragment
     * @author sergiopereira
     */
    private final static void setFacebookLogin(LoginButton button, Fragment fragment) {
        button.setFragment(fragment);
        button.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
        button.setReadPermissions(Arrays.asList(FB_PERMISSION_EMAIL));
    }

    /**
     * Show Facebook views and associate buttons to fragment.
     * @param visibility
     * @param views
     * @author sergiopereira
     */
    private final static void showFacebookViews(Fragment fragment, View... views) {
        // For each view associated to Facebook buttons
        for (View view : views)
            if (view != null) {
                // Case Facebook button connect to fragment
                if(view instanceof LoginButton) setFacebookLogin((LoginButton)view, fragment);
                // Show Facebook view
                view.setVisibility(View.VISIBLE);
            }
    }
    
    /**
     * Clean the facebook session
     * @author sergiopereira
     */
    public final static void cleanFacebookSession(){
        Session s = Session.getActiveSession();
        s.closeAndClearTokenInformation();
    }
    
}
