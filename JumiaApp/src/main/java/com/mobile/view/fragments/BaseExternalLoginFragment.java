package com.mobile.view.fragments;

import android.content.ContentValues;
import android.os.Bundle;
import android.text.TextUtils;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.mobile.newFramework.utils.CustomerUtils;
import com.mobile.newFramework.utils.NetworkConnectivity;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.Toast;
import com.mobile.utils.social.FacebookHelper;
import com.mobile.view.R;

import org.json.JSONObject;

import java.util.Collections;
import java.util.Set;

import de.akquinet.android.androlog.Log;

/**
 * Class created to control all external login interfaces that are identical on session login and the checkout about you step.
 *
 * @author pcarvalho
 * @modified sergiopereira
 */
public abstract class BaseExternalLoginFragment extends BaseFragment implements GraphRequest.GraphJSONObjectCallback, FacebookCallback<LoginResult> {

    private static final String TAG = BaseExternalLoginFragment.class.getSimpleName();

    protected boolean isNetworkFacebookError = false;

    protected boolean facebookLoginClicked = false;

    protected CallbackManager mFacebookCallbackManager;

    /**
     * Constructor
     */
    public BaseExternalLoginFragment(Set<MyMenuItem> enabledMenuItems, NavigationAction action, int layoutResId, int titleResId, KeyboardState adjust_state) {
        super(enabledMenuItems, action, layoutResId, titleResId, adjust_state);
    }

    /**
     * Constructor for checkout
     */
    public BaseExternalLoginFragment(Set<MyMenuItem> enabledMenuItems, NavigationAction action, int layoutResId, int titleResId, KeyboardState adjust_state, int titleCheckout) {
        super(enabledMenuItems, action, layoutResId, titleResId, adjust_state, titleCheckout);
    }

    /*
     * ###### LIFE CYCLE ######
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mFacebookCallbackManager = CallbackManager.Factory.create();
    }

    /*
     * ###### FACEBOOK LOGIN CALLBACK ######
     */

    @Override
    public void onSuccess(LoginResult loginResult) {
        Log.i(TAG, "FacebookCallback onSuccess");
        if(loginResult.getRecentlyDeniedPermissions().contains(FacebookHelper.FB_PERMISSION_EMAIL)){
            Toast.makeText(getBaseActivity(), getString(R.string.facebook_permission), Toast.LENGTH_LONG).show();
        }
        onFacebookSuccessLogin();
    }

    @Override
    public void onCancel() {
        Log.i(TAG, "FacebookCallback onCancel");
        facebookLoginClicked = false;
        FacebookHelper.facebookLogout();
        showFragmentContentContainer();
        checkNetworkStatus();
    }

    @Override
    public void onError(FacebookException e) {
        Log.i(TAG, "FacebookCallback onError");
        facebookLoginClicked = false;
        e.printStackTrace();
        FacebookHelper.facebookLogout();
        checkNetworkStatus();
    }

    protected void checkNetworkStatus(){
        isNetworkFacebookError = !NetworkConnectivity.isConnected(getBaseActivity().getApplicationContext());
    }

    /**
     * function that validates if a facebook network error has occurred
     *
     */
    protected void validateFacebookNetworkError(){
        if(isNetworkFacebookError){
            showNoNetworkWarning();
            isNetworkFacebookError = false;
        }
    }

    /*
     * ###### FACEBOOK GRAPH REQUEST CALLBACK ######
     */

    /**
     * When the facebook login is done with success
     */
    private void onFacebookSuccessLogin() {
        GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), this).executeAsync();
    }

    @Override
    public void onCompleted(JSONObject user, GraphResponse response) {
        Log.i(TAG, "FacebookCallback onCompleted");
        if (user != null) {
            requestFacebookLogin(user);
        } else {
            FacebookHelper.facebookLogout();
            showFragmentContentContainer();
        }
    }

    /**
     * If the user does not accept the email permission, re-request the permission<br>
     * Arrays.asList(FacebookHelper.FB_PERMISSION_EMAIL)
     */
    public void repeatFacebookEmailRequest(){
        if(!isOnStoppingProcess) {
            showFragmentLoading();
            try {
                LoginManager.getInstance().logInWithReadPermissions(this, Collections.singletonList(FacebookHelper.FB_PERMISSION_EMAIL));
            } catch (NullPointerException ex) {
                Print.e(TAG, "Error trying to launch facebook dialog for requesting permissions", ex);
            }
        }
    }

    /*
     * ###### TRIGGER ######
     */

    /**
     * Function that parses the information from facebook success response
     */
    private void requestFacebookLogin(JSONObject user) {
        // Validate required permission
        String email = user.optString(FacebookHelper.FACEBOOK_EMAIL_TAG);
        if (TextUtils.isEmpty(email)) {
            repeatFacebookEmailRequest();
            return;
        }
        // Trigger
        ContentValues values = new ContentValues();
        values.put(FacebookHelper.FACEBOOK_EMAIL_TAG, email);
        values.put(FacebookHelper.FACEBOOK_FIRST_NAME_TAG, user.optString(FacebookHelper.FACEBOOK_FIRST_NAME_TAG));
        values.put(FacebookHelper.FACEBOOK_LAST_NAME_TAG, user.optString(FacebookHelper.FACEBOOK_LAST_NAME_TAG));
        values.put(FacebookHelper.FACEBOOK_GENDER_TAG, user.optString(FacebookHelper.FACEBOOK_GENDER_TAG));
        values.put(CustomerUtils.INTERNAL_AUTO_LOGIN_FLAG, true);
        triggerFacebookLogin(values, true);
    }

    /**
     * Method to be override on the SessionLogin and CheckoutAboutYou fragments
     */
    public abstract void triggerFacebookLogin(ContentValues values, boolean autoLogin);

}
