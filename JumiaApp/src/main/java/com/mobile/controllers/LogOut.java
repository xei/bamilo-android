package com.mobile.controllers;

import android.support.annotation.NonNull;

import com.mobile.app.JumiaApplication;
import com.mobile.helpers.session.GetLogoutHelper;
import com.mobile.newFramework.rest.AigHttpClient;
import com.mobile.newFramework.utils.cache.WishListCache;
import com.mobile.utils.TrackerDelegator;
import com.mobile.utils.emarsys.EmarsysTracker;
import com.mobile.utils.pushwoosh.PushWooshTracker;
import com.mobile.utils.social.FacebookHelper;
import com.mobile.view.BaseActivity;

import java.lang.ref.WeakReference;

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
 *
 */
public class LogOut {

    /**
     * Performs the Logout
     */
    public static void perform(@NonNull final WeakReference<BaseActivity> activityRef) {
        BaseActivity baseActivity = activityRef.get();
        if (baseActivity != null) {
            // Show progress
            baseActivity.showProgress();
            // Try notify mob api
            JumiaApplication.INSTANCE.sendRequest(new GetLogoutHelper(), null, null);
            // Clean customer data
            cleanCustomerData(baseActivity);
            // Inform activity to update views
            try {
                baseActivity.onLogOut();
                PushWooshTracker.logOut(baseActivity, true);
                EmarsysTracker.logOut(true);
            } catch (IllegalStateException e){
                e.printStackTrace();
            }
        }
    }

    /**
     * Clear cart data from memory and other components.
     */
    public static void cleanCustomerData(BaseActivity baseActivity) {
        // Facebook logout
        FacebookHelper.facebookLogout();
        // Clear cookies, cart, credentials
        AigHttpClient.getInstance().clearCookieStore();
        // Clean wish list
        WishListCache.clean();
        // Clean cart
        JumiaApplication.INSTANCE.setCart(null);
        JumiaApplication.INSTANCE.getCustomerUtils().clearCredentials();
        // Update layouts to clean cart info
        baseActivity.updateCartInfo();
        // Tracking
        TrackerDelegator.clearTransactionCount();
    }

}
