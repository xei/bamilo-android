package pt.rocket.controllers;

import java.lang.ref.WeakReference;

import pt.rocket.framework.event.EventManager;
import pt.rocket.framework.event.EventType;
import pt.rocket.framework.event.RequestEvent;
import pt.rocket.framework.event.ResponseEvent;
import pt.rocket.framework.event.ResponseListener;
import pt.rocket.framework.utils.AnalyticsGoogle;
import pt.rocket.utils.DialogProgress;
import pt.rocket.view.R;
import android.app.Activity;

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
        final DialogProgress dialog = new DialogProgress(activityRef.get());
        dialog.show();
        EventManager.getSingleton().triggerRequestEvent(new RequestEvent(EventType.LOGOUT_EVENT),
                new ResponseListener() {

                    @Override
                    public void handleEvent(ResponseEvent event) {
                        dialog.dismiss();
                    }

                    @Override
                    public boolean removeAfterHandlingEvent() {
                        return true;
                    }

                });
    }

}
