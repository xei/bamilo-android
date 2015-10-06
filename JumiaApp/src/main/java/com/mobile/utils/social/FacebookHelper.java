package com.mobile.utils.social;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.view.View;

import com.facebook.FacebookSdk;
import com.facebook.login.LoginBehavior;
import com.facebook.login.LoginManager;
import com.facebook.login.widget.LoginButton;
import com.mobile.newFramework.Darwin;
import com.mobile.newFramework.utils.Constants;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.ui.UIUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import de.akquinet.android.androlog.Log;

/**
 * Facebook helper.
 * @author sergiopereira
 */
public class FacebookHelper {

    private static final String TAG = FacebookHelper.class.getSimpleName();

    public static final String FB_PERMISSION_EMAIL = "email";

    private static final String FB_PERMISSION_PUB_PROFILE = "public_profile";

    private static final String FB_PERMISSION_FRIENDS = "user_friends";

    public final static String FACEBOOK_EMAIL_TAG = "email";

    public final static String FACEBOOK_FIRST_NAME_TAG = "first_name";

    public final static String FACEBOOK_LAST_NAME_TAG = "last_name";

    public final static String FACEBOOK_GENDER_TAG = "gender";

    /**
     * Method used to show or hide Facebook views using a flag.<br>
     * Hide view case Build.VERSION
     * Case the Frament implements the OnClickView is defined to the callback for click listener.
     * @param fragment
     * @param views
     * @author sergiopereira
     */
    public static void showOrHideFacebookButton(Fragment fragment, View... views) {
        // Validate the Facebook configuration
        SharedPreferences sharedPrefs = fragment.getActivity().getApplicationContext().getSharedPreferences(Constants.SHARED_PREFERENCES, Context.MODE_PRIVATE);
        boolean hideFacebook = !sharedPrefs.getBoolean(Darwin.KEY_SELECTED_FACEBOOK_IS_AVAILABLE, false);

        Log.i(TAG, "ENABLED FACEBOOK: " + hideFacebook);
        // Case hide Facebook view
        if(hideFacebook) UIUtils.showOrHideViews(View.GONE, views);
        // Case show Facebook view
        else showFacebookViews(fragment, views);
    }

    /**
     * Show Facebook views and associate buttons to fragment.
     * @param fragment
     * @param views
     * @author sergiopereira
     */
    private static void showFacebookViews(Fragment fragment, View... views) {
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
    private static void setFacebookLogin(LoginButton button, Fragment fragment) {
        // Set the UI control
        button.setFragment(fragment);
        // Set the Facebook Login (from app or from dialog)
        button.setLoginBehavior(LoginBehavior.NATIVE_WITH_FALLBACK);
        // Set Facebook permissions
        button.setReadPermissions(Arrays.asList(FB_PERMISSION_EMAIL, FB_PERMISSION_PUB_PROFILE));
//        button.setReadPermissions(Arrays.asList(FB_PERMISSION_EMAIL, FB_PERMISSION_PUB_PROFILE, FB_PERMISSION_FRIENDS));
        // Set click listener
        if(fragment instanceof View.OnClickListener) button.setOnClickListener((View.OnClickListener) fragment);
    }

    /**
     * Log the hash key for Facebook dashboard
     * @param context
     * @author sergiopereira
     */
    public static void logHashKey(Context context) {
        try {
            String name = context.getApplicationInfo().packageName;
            PackageInfo info = context.getPackageManager().getPackageInfo(name, PackageManager.GET_SIGNATURES);
            Print.i("Facebook", "Package name: " + name);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Print.i("Facebook", "KeyHash:\n" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException | NoSuchAlgorithmException e) {
            // ...
        }
    }

    /**
     *  Logs out of facebook in the case he is logged in via facebook
     *
     */
    public static void facebookLogout(){
        if(FacebookSdk.isInitialized()){
            LoginManager.getInstance().logOut();
        }
    }

}
