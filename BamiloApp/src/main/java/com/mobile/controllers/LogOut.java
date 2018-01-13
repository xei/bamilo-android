package com.mobile.controllers;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.mobile.app.BamiloApplication;
import com.mobile.constants.tracking.EmarsysEventConstants;
import com.mobile.factories.EmarsysEventFactory;
import com.mobile.helpers.session.GetLogoutHelper;
import com.mobile.interfaces.IResponseCallback;
import com.mobile.managers.TrackerManager;
import com.mobile.service.pojo.BaseResponse;
import com.mobile.service.rest.AigHttpClient;
import com.mobile.service.utils.EventTask;
import com.mobile.service.utils.cache.WishListCache;
import com.mobile.utils.TrackerDelegator;
import com.mobile.view.BaseActivity;
import com.mobile.view.fragments.BaseFragment;

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
    public static void cleanCustomerData(BaseActivity baseActivity) {
        // Clear cookies, cart, credentials
        AigHttpClient.getInstance().clearCookieStore();
        // Clean wish list
        WishListCache.clean();
        // Clean cart
        BamiloApplication.INSTANCE.setCart(null);
        BamiloApplication.INSTANCE.getCustomerUtils().clearCredentials();
        // Update layouts to clean cart info
        baseActivity.updateCartInfo();
        // Tracking
        TrackerDelegator.clearTransactionCount();
    }

}
