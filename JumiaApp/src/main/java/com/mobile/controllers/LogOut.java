package com.mobile.controllers;

import android.app.Activity;

import com.mobile.app.JumiaApplication;
import com.mobile.helpers.session.GetLogoutHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.newFramework.objects.cart.PurchaseEntity;
import com.mobile.newFramework.pojo.BaseResponse;
import com.mobile.newFramework.rest.AigHttpClient;
import com.mobile.newFramework.utils.cache.WishListCache;
import com.mobile.utils.TrackerDelegator;
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
     * @param activityRef
     *            The activity where the logout is called from
     *            
     * TODO: Improve this method, if is being discarded the server response why we perform a request...
     * 
     */
    public static void perform(final WeakReference<Activity> activityRef) {

        BaseActivity baseActivity = (BaseActivity) activityRef.get();
        if (baseActivity != null) {
            baseActivity.showProgress();
        }

        JumiaApplication.INSTANCE.sendRequest(new GetLogoutHelper(), null, new IResponseCallback() {

            @Override
            public void onRequestError(BaseResponse baseResponse) {
                BaseActivity baseActivity = (BaseActivity) activityRef.get();
                if (baseActivity != null) {
                    cleanData(baseActivity);
                }
            }

            @Override
            public void onRequestComplete(BaseResponse baseResponse) {
                BaseActivity baseActivity = (BaseActivity) activityRef.get();
                if (baseActivity != null) {
                    cleanData(baseActivity);
                }
            }
        });
    }
    
    /**
     * Clear cart data from memory and other components.
     */
    private static void cleanData(BaseActivity baseActivity) {
        // Facebook logout
        FacebookHelper.facebookLogout();
        // Clear cookies, cart, credentials
        AigHttpClient.getInstance().clearCookieStore();
        // Clean wish list
        WishListCache.clean();
        // Clean cart
        JumiaApplication.INSTANCE.setCart(new PurchaseEntity());
        JumiaApplication.INSTANCE.setLoggedIn(false);
        JumiaApplication.INSTANCE.getCustomerUtils().clearCredentials();
        // Update layouts to clean cart info
        baseActivity.updateCartInfo();
        // Inform parent activity
        baseActivity.onLogOut();
        // Tracking
        TrackerDelegator.clearTransactionCount();
    }

}
