package com.mobile.utils.tracking.ga;

import com.mobile.interfaces.tracking.IEventTracker;
import com.mobile.interfaces.tracking.IScreenTracker;
import com.mobile.view.BaseActivity;

import java.util.HashMap;

/**
 * Created by narbeh on 12/3/17.
 */

public final class GATracker implements IEventTracker, IScreenTracker {

    private static GATracker instance = null;

    protected GATracker() {}

    public static GATracker getInstance() {
        if(instance == null) {
            instance = new GATracker();
        }
        return instance;
    }

    @Override
    public String getTrackerName() {
        return "GATracker";
    }

    @Override
    public void trackEvent(BaseActivity activity, String event, HashMap<String, Object> attributes) {

    }

    @Override
    public void trackScreen(BaseActivity activity, String screen) {

    }
}
