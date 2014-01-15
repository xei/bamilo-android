package pt.rocket.controllers;

import java.lang.ref.WeakReference;

import pt.rocket.helpers.GetLogoutHelper;
import pt.rocket.interfaces.IResponseCallback;
import pt.rocket.utils.JumiaApplication;
import pt.rocket.utils.TrackerDelegator;
import pt.rocket.utils.dialogfragments.DialogProgressFragment;
import pt.rocket.view.BaseActivity;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

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
        final DialogProgressFragment dialog = DialogProgressFragment.newInstance();
        dialog.show(((FragmentActivity) activityRef.get()).getSupportFragmentManager(), null);

        JumiaApplication.INSTANCE.sendRequest(new GetLogoutHelper(), null, new IResponseCallback() {

            @Override
            public void onRequestError(Bundle bundle) {
                if (((BaseActivity) activityRef.get()) != null) {
                    ((BaseActivity) activityRef.get()).handleSuccessEvent(bundle);
                }
            }

            @Override
            public void onRequestComplete(Bundle bundle) {
                TrackerDelegator.trackLogoutSuccessful(((BaseActivity) activityRef.get()));
                if (((BaseActivity) activityRef.get()) != null) {
                    ((BaseActivity) activityRef.get()).updateSlidingMenuCompletly();
                    ((BaseActivity) activityRef.get()).handleSuccessEvent(bundle);
                }
                dialog.dismiss();
            }
        });
    }

}
