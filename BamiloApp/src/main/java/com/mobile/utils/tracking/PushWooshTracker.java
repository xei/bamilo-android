package com.mobile.utils.tracking;

import com.mobile.utils.tracking.emarsys.EmarsysTracker;
import com.mobile.view.BaseActivity;
import com.pushwoosh.inapp.InAppFacade;

import java.util.HashMap;

public final class PushWooshTracker extends EmarsysTracker {

    private static PushWooshTracker instance = null;

    protected PushWooshTracker() {}

    public static EmarsysTracker getInstance() {
        if(instance == null) {
            instance = new PushWooshTracker();
        }
        return instance;
    }

    @Override
    public String getTrackerName() {
        return "PushWooshTracker";
    }

    /*@Override
    public void trackEvent(BaseActivity activity, String event, HashMap<String, Object> attributes) {
        InAppFacade.postEvent(activity, event, attributes);
    }*/
}
