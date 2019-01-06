package com.bamilo.android.appmodule.bamiloapp.managers;

import android.content.Context;
import com.bamilo.android.appmodule.bamiloapp.models.BaseEventModel;
import com.bamilo.android.appmodule.bamiloapp.models.BaseScreenModel;
import com.bamilo.android.appmodule.bamiloapp.interfaces.tracking.IBaseTracker;
import com.bamilo.android.appmodule.bamiloapp.interfaces.tracking.IEventTracker;
import com.bamilo.android.appmodule.bamiloapp.interfaces.tracking.IScreenTracker;
import java.lang.reflect.Method;
import java.util.HashMap;

/**
 * Created by Narbeh M. on 4/26/17.
 */

public final class TrackerManager {

    private static HashMap<String, IBaseTracker> trackers = new HashMap<>();

    public static void addTracker(IBaseTracker tracker) {
        trackers.put(tracker.getTrackerName(), tracker);
    }

    public static boolean removeEventTracker(String key) {
        if (trackers.containsKey(key)) {
            trackers.remove(key);
            return true;
        }
        return false;
    }

    public static void trackEvent(Context context, String eventName, BaseEventModel eventModel) {
        for (IBaseTracker tracker : trackers.values()) {
            if (tracker instanceof IEventTracker) {
                Method eventMethod = null;
                try {
                    eventMethod = ((IEventTracker) tracker).getClass()
                            .getMethod("trackEvent" + eventName, Context.class,
                                    BaseEventModel.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                try {
                    eventMethod.invoke(tracker, context, eventModel);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void trackScreen(Context context, BaseScreenModel screenModel,
            boolean trackTiming) {
        for (IBaseTracker tracker : trackers.values()) {
            if (tracker instanceof IScreenTracker) {
//                if (trackTiming) {
//                    ((IScreenTracker) tracker).trackScreenAndTiming(context, screenModel);
//                } else {
                    ((IScreenTracker) tracker).trackScreen(context, screenModel);
//                }
            }
        }
    }

    public static void trackScreenTiming(Context context, BaseScreenModel screenModel) {
        for (IBaseTracker tracker : trackers.values()) {
            if (tracker instanceof IScreenTracker) {
//                ((IScreenTracker) tracker).trackScreenTiming(context, screenModel);
            }
        }
    }

    public static void setCampaignUrl(String campaignUrl) {
        for (IBaseTracker tracker : trackers.values()) {
            tracker.setCampaignUrl(campaignUrl);
        }
    }
}
