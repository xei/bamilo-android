package pt.rocket.controllers;

import java.lang.ref.WeakReference;

import pt.rocket.app.JumiaApplication;
import pt.rocket.framework.rest.RestClientSingleton;
import pt.rocket.helpers.session.GetLogoutHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.view.BaseActivity;
import android.app.Activity;
import android.os.Bundle;

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
     */
    public static void performLogOut(final WeakReference<Activity> activityRef) {
//        final DialogProgressFragment dialog = DialogProgressFragment.newInstance();
//        dialog.show(((FragmentActivity) activityRef.get()).getSupportFragmentManager(), null);

        BaseActivity baseActivity = (BaseActivity) activityRef.get();
        if (baseActivity != null) {
            baseActivity.showProgress();
        }

        JumiaApplication.INSTANCE.sendRequest(new GetLogoutHelper(), null, new IResponseCallback() {

            @Override
            public void onRequestError(Bundle bundle) {
                BaseActivity baseActivity = (BaseActivity) activityRef.get();

                if (baseActivity != null) {
                    baseActivity.dismissProgress();

                    RestClientSingleton.getSingleton(baseActivity).getCookieStore().clear();
                    JumiaApplication.INSTANCE.setLoggedIn(false);
                    JumiaApplication.INSTANCE.getCustomerUtils().clearCredentials();
                    TrackerDelegator.trackLogoutSuccessful(baseActivity);
                    baseActivity.updateSlidingMenuCompletly();
                    baseActivity.handleSuccessEvent(bundle);
                }
            }

            @Override
            public void onRequestComplete(Bundle bundle) {
                BaseActivity baseActivity = (BaseActivity) activityRef.get();

                if (baseActivity != null) {
                    baseActivity.dismissProgress();

                    JumiaApplication.INSTANCE.setLoggedIn(false);
                    TrackerDelegator.trackLogoutSuccessful(baseActivity);
                    JumiaApplication.INSTANCE.getCustomerUtils().clearCredentials();
                    baseActivity.updateSlidingMenuCompletly();
                    baseActivity.handleSuccessEvent(bundle);
                }
                
                //dialog.dismiss();
            }
        });
    }

}
