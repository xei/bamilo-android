package com.mobile.utils.pushwoosh;

import com.mobile.app.JumiaApplication;
import com.mobile.utils.EventTracker;
import com.mobile.utils.emarsys.EmarsysTracker;
import com.mobile.view.BaseActivity;
import com.pushwoosh.inapp.InAppFacade;

import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;

/**
 * Created by shahrooz on 4/12/17.
 */

public class PushWooshTracker extends EmarsysTracker {
    private static PushWooshTracker instance = null;
    protected PushWooshTracker() {}

    public static EmarsysTracker getInstance() {
        if(instance == null) {
            instance = new PushWooshTracker();
        }
        return instance;
    }

    @Override
    public void postEvent(BaseActivity activity, String event, HashMap<String, Object> attributes) {
        InAppFacade.postEvent(activity, event, attributes);
    }
}
