package com.bamilo.android.appmodule.bamiloapp.controllers;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.appmodule.bamiloapp.helpers.session.GetLogoutHelper;
import com.bamilo.android.appmodule.bamiloapp.interfaces.IResponseCallback;
import com.bamilo.android.framework.service.pojo.BaseResponse;
import com.bamilo.android.framework.service.rest.AigHttpClient;
import com.bamilo.android.framework.service.utils.EventTask;
import com.bamilo.android.framework.service.utils.cache.WishListCache;
import com.bamilo.android.appmodule.bamiloapp.utils.TrackerDelegator;
import com.bamilo.android.appmodule.bamiloapp.view.BaseActivity;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.BaseFragment;

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
    public static void perform(@NonNull final WeakReference<BaseActivity> activityRef, @Nullable final BaseFragment fragmentRef) {
        final BaseActivity baseActivity = activityRef.get();
        if (baseActivity != null) {
            // Show progress
            baseActivity.showProgress();
            // Try notify mob api
            BamiloApplication.INSTANCE.sendRequest(new GetLogoutHelper(), null, new IResponseCallback() {
                @Override
                public void onRequestComplete(BaseResponse baseResponse) {
                    // Clean customer data
                    cleanCustomerData(baseActivity);
                    // Inform activity to update views
                    try {
                        baseActivity.onLogOut();
//                        TrackerManager.trackEvent(baseActivity, EmarsysEventConstants.Logout, EmarsysEventFactory.logout(true));
                    } catch (IllegalStateException e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onRequestError(BaseResponse baseResponse) {
                    baseActivity.dismissProgress();
                    if (fragmentRef != null) {
                        baseResponse.setEventTask(EventTask.ACTION_TASK);
                        fragmentRef.handleErrorEvent(baseResponse);
                    }
//                    TrackerManager.trackEvent(baseActivity, EmarsysEventConstants.Logout, EmarsysEventFactory.logout(false));
                }
            });
        }
    }

    /**
     * Clear cart data from memory and other components.
     */
    public static void cleanCustomerData(Activity activity) {
        // Clear cookies, cart, credentials
        AigHttpClient.getInstance().clearCookieStore();
        // Clean wish list
        WishListCache.clean();
        // Clean cart
        BamiloApplication.INSTANCE.setCart(null);
        BamiloApplication.INSTANCE.getCustomerUtils().clearCredentials();
        // Update layouts to clean cart info
        if(activity instanceof BaseActivity) {
            ((BaseActivity) activity).updateCartInfo();
        }
        // Tracking
        TrackerDelegator.clearTransactionCount();
    }

}
