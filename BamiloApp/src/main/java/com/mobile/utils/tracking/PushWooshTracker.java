package com.mobile.utils.tracking;

import android.app.Activity;
import android.content.Context;

import com.mobile.utils.tracking.emarsys.EmarsysTracker;
import com.mobile.view.BaseActivity;
import com.pushwoosh.inapp.InAppFacade;

import java.util.HashMap;
import java.util.Map;

public final class PushWooshTracker extends EmarsysTracker {

    private static PushWooshTracker instance = null;

    private Activity activity;

    protected PushWooshTracker() {}

    public static EmarsysTracker getInstance(Activity activity) {
        if(instance == null) {
            instance = new PushWooshTracker();
            instance.activity = activity;
        }
        return instance;
    }

    @Override
    public String getTrackerName() {
        return "PushWooshTracker";
    }

    @Override
    protected void sendEventToEmarsys(Context context, String event, Map<String, Object> attributes) {
        Map<String, Object> pushWooshAttrs = getBasicAttributes();
        pushWooshAttrs.putAll(attributes);
        InAppFacade.postEvent(activity, event, pushWooshAttrs);
    }
}
