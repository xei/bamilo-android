package com.mobile.managers;

import com.mobile.utils.EventTracker;
import com.mobile.view.BaseActivity;

import java.util.HashMap;
import java.util.Observable;

/**
 * Created by Narbeh M. on 4/26/17.
 */

public final class TrackerManager {
    private static HashMap<String, EventTracker> eventTrackers = new HashMap<>();

    public static void addEventTracker(String key, EventTracker tracker) {
        eventTrackers.put(key, tracker);
    }

    public static void removeEventTracker(String key) {
        eventTrackers.remove(key);
    }

    public static void postEvent(BaseActivity activity, String event, HashMap<String, Object> attributes) {
        for (EventTracker tracker : eventTrackers.values()) {
            tracker.postEvent(activity, event, attributes);
        }
    }
}
