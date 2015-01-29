package pt.rocket.utils.social;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import pt.rocket.utils.ui.UIUtils;
import pt.rocket.view.R;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.content.pm.PackageManager.NameNotFoundException;
import android.support.v4.app.Fragment;
import android.util.Base64;
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
    
    private static final String FB_PERMISSION_PUB_PROFILE = "public_profile";
    
    private static final String FB_PERMISSION_FRIENDS = "user_friends";

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
     * Set the Facebook button
     * @param button
     * @param fragment
     * @author sergiopereira
     */
    private final static void setFacebookLogin(LoginButton button, Fragment fragment) {
        // Set the UI control
        button.setFragment(fragment);
        // Set the Facebook Login (from app or from dialog)
        button.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
        // Set Facebook permissions
        button.setReadPermissions(Arrays.asList(FB_PERMISSION_EMAIL, FB_PERMISSION_PUB_PROFILE, FB_PERMISSION_FRIENDS));
    }
    
    /**
     * Clean the facebook session
     * @author sergiopereira
     */
    public final static void cleanFacebookSession(){
        Session s = Session.getActiveSession();
        s.closeAndClearTokenInformation();
    }
    
    /**
     * Log the hash key for Facebook dashboard
     * @param context
     * @author sergiopereira
     */
    public final static void logHashKey(Context context) {
        try {
            String name = context.getApplicationInfo().packageName;
            PackageInfo info = context.getPackageManager().getPackageInfo(name, PackageManager.GET_SIGNATURES);
            android.util.Log.i("Facebook", "Package name: " + name);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                android.util.Log.i("Facebook", "KeyHash:\n" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (NameNotFoundException e) {
            // ...
        } catch (NoSuchAlgorithmException e) {
            // ...
        }
    }
    
}
