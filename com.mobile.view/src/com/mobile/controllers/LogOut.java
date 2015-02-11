package com.mobile.controllers;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.os.Bundle;

import com.mobile.app.JumiaApplication;
import com.mobile.framework.objects.ShoppingCart;
import com.mobile.framework.rest.RestClientSingleton;
import com.mobile.helpers.session.GetLogoutHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.view.BaseActivity;

/**
 * This Class is responsible to show the log out dialog.
 * <p/>
 * <br>
 * 
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * <p/>
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
 * 
 * @author Sergio Pereira
 * @modifyed: Nuno Castro
 * @modified: Manuel Silva
 * 
 * @version 1.5
 * 
 *          2012/06/19
 * 
 */
public class LogOut {

    /**
     * Performs the Logout
     * 
     * @param activity
     *            The activity where the logout is called from
     *            
     * TODO: Improve this method, if is being discarded the server response why we perform a request...
     * 
     */
    public static void performLogOut(final WeakReference<Activity> activityRef) {

        BaseActivity baseActivity = (BaseActivity) activityRef.get();
        if (baseActivity != null) {
            baseActivity.showProgress();
        }

        JumiaApplication.INSTANCE.sendRequest(new GetLogoutHelper(), null, new IResponseCallback() {

            @Override
            public void onRequestError(Bundle bundle) {
                BaseActivity baseActivity = (BaseActivity) activityRef.get();
                if (baseActivity != null) {
                    cleanCartData(baseActivity, bundle);
                }
            }

            @Override
            public void onRequestComplete(Bundle bundle) {
                BaseActivity baseActivity = (BaseActivity) activityRef.get();
                if (baseActivity != null) {
                    cleanCartData(baseActivity, bundle);
                }
            }
        });
    }
    
    /**
     * Clear cart data from memory and other components.
     * @param baseActivity
     * @param bundle
     * @author sergiopereira
     */
    private static void cleanCartData(BaseActivity baseActivity, Bundle bundle) {
        // Clear cookies, cart, credentials
        RestClientSingleton.getSingleton(baseActivity).clearCookieStore();
        JumiaApplication.INSTANCE.setCart(new ShoppingCart());
        JumiaApplication.INSTANCE.setLoggedIn(false);
        JumiaApplication.INSTANCE.getCustomerUtils().clearCredentials();
        // Update layouts to clean cart info
        baseActivity.updateCartInfo();
        baseActivity.updateSlidingMenuCompletly();
        // Inform parent activity
        baseActivity.onLogOut();
    }

}
