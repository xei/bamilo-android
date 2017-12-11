package com.mobile.managers;

import com.mobile.classes.models.BaseEventModel;
import com.mobile.interfaces.tracking.IBaseTracker;
import com.mobile.interfaces.tracking.IEventTracker;
import com.mobile.interfaces.tracking.IScreenTracker;
import com.mobile.view.BaseActivity;

import java.lang.reflect.*;
import java.util.HashMap;

/**
 * Created by Narbeh M. on 4/26/17.
 */

public final class TrackerManager {
    private static HashMap<String, IBaseTracker> trackers = new HashMap<>();

    public static void addTracker(IBaseTracker tracker) {
        trackers.put(tracker.getTrackerName(), tracker);
    }

    public static void removeEventTracker(String key) {
        trackers.remove(key);
    }

    public static void trackEvent(BaseActivity activity, String eventName, BaseEventModel eventModel) {
        for (IBaseTracker tracker : trackers.values()) {
            if(tracker instanceof IEventTracker) {
                Method eventMethod = null;
                try {
                    eventMethod = ((IEventTracker)tracker).getClass().getMethod("trackEvent" + eventName, BaseActivity.class, BaseEventModel.class);
                }
                catch (SecurityException e) { }
                catch (NoSuchMethodException e) { }

                try {
                    eventMethod.invoke(activity, eventModel);
                }
                catch (IllegalArgumentException e) { }
                catch (IllegalAccessException e) { }
                catch (InvocationTargetException e) { }
            }
        }
    }

    public static void trackScreen(BaseActivity activity, String screenName) {
        for (IBaseTracker tracker : trackers.values()) {
            if(tracker instanceof IScreenTracker) {
                ((IScreenTracker)tracker).trackScreen(activity, screenName);
            }
        }
    }
}
