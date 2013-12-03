package pt.rocket.controllers;

import java.lang.ref.WeakReference;

import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.RequestEvent;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseListener;
import pt.rocket.utils.dialogfragments.DialogProgressFragment;
import pt.rocket.view.BaseActivity;
import android.app.Activity;
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
        dialog.show(((FragmentActivity)activityRef.get()).getSupportFragmentManager(), null);
        EventManager.getSingleton().triggerRequestEvent(new RequestEvent(EventType.LOGOUT_EVENT),
                new ResponseListener() {

                    @Override
                    public void handleEvent(ResponseEvent event) {
                        if(((BaseActivity) activityRef.get()) != null)
                                ((BaseActivity) activityRef.get()).updateSlidingMenuCompletly();
                        dialog.dismiss();                        
                    }

                    @Override
                    public boolean removeAfterHandlingEvent() {
                        return true;
                    }

                    @Override
                    public String getMD5Hash() {
                        // TODO Auto-generated method stub
                        return null;
                    }

                });
    }

}
