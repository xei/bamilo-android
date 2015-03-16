package com.mobile.utils.social;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.Signature;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.facebook.Request;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.widget.LoginButton;
import com.mobile.constants.ConstantsSharedPrefs;
import com.mobile.framework.Darwin;
import com.mobile.utils.ui.UIUtils;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

/**
 * Facebook helper.
 * @author sergiopereira
 */
public class FacebookHelper {
    
    private static final String TAG = FacebookHelper.class.getSimpleName();
    
    private static final String FB_PERMISSION_EMAIL = "email";
    
    private static final String FB_PERMISSION_PUB_PROFILE = "public_profile";
    
    private static final String FB_PERMISSION_FRIENDS = "user_friends";

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
        SharedPreferences sharedPrefs = fragment.getActivity().getApplicationContext().getSharedPreferences(ConstantsSharedPrefs.SHARED_PREFERENCES, Context.MODE_PRIVATE);
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
        button.setLoginBehavior(SessionLoginBehavior.SSO_WITH_FALLBACK);
        // Set Facebook permissions
        button.setReadPermissions(Arrays.asList(FB_PERMISSION_EMAIL, FB_PERMISSION_PUB_PROFILE, FB_PERMISSION_FRIENDS));
        // Set click listener
        if(fragment instanceof OnClickListener) button.setOnClickListener((OnClickListener) fragment);
    }
    
    /**
     * Clean the facebook session
     * @author sergiopereira
     */
    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    public static void cleanFacebookSession(){
        Session s = Session.getActiveSession();
        s.closeAndClearTokenInformation();
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
            android.util.Log.i("Facebook", "Package name: " + name);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                android.util.Log.i("Facebook", "KeyHash:\n" + Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (NameNotFoundException | NoSuchAlgorithmException e) {
            // ...
        }
    }
    
    /**
     * Validate if required permissions were granted in the seacond request.
     * @param session
     * @return true or false
     * @author sergiopereira
     */
    public static boolean userNotAcceptRequiredPermissions(Session session) {
        return !session.isPermissionGranted(FB_PERMISSION_EMAIL) && session.getState() == SessionState.OPENED_TOKEN_UPDATED;  
    }
    
    /**
     * Validate if required permissions were granted in the first request.
     * @param session
     * @return true or false
     * @author sergiopereira
     */
    public static boolean wereRequiredPermissionsGranted(Session session) {
        return !session.isPermissionGranted(FB_PERMISSION_EMAIL) && session.getState() != SessionState.OPENED_TOKEN_UPDATED ;  
    }
    
    /**
     * Make a new request to user with the required permission.
     * @param fragment
     * @param session
     * @param callback
     * @author sergiopereira
     */
    public static void makeNewRequiredPermissionsRequest(Fragment fragment, Session session, Session.StatusCallback callback) {
        // Make new permissions request 
        Session.NewPermissionsRequest newRequest = new Session.NewPermissionsRequest(fragment, FB_PERMISSION_EMAIL);
        newRequest.setCallback(callback);
        session.requestNewReadPermissions(newRequest);
    }
    
    /**
     * Get the FacebookGraphUser.
     * @param session
     * @param callback
     * @author sergiopereira
     */
    public static void makeGraphUserRequest(Session session, Request.GraphUserCallback callback) {
        // Make request to the me API
        Request request = Request.newMeRequest(session, callback);
        Request.executeBatchAsync(request);
    }
    
}
