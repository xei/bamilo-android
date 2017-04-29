package com.mobile.utils;

import com.mobile.app.JumiaApplication;
import com.mobile.constants.EventConstants;
import com.mobile.utils.ui.UIUtils;
import com.mobile.view.BaseActivity;

import java.util.Date;
import java.util.HashMap;

/**
 * Created by Narbeh M. on 4/29/17.
 */

public abstract class EventTracker {
    public abstract void postEvent(BaseActivity activity, String event, HashMap<String, Object> attributes);

    public static HashMap<String, Object> getBasicAttributes() {
        HashMap<String, Object> attributes = new HashMap<>();

        attributes.put(EventConstants.AppVersion, android.os.Build.VERSION.RELEASE);
        attributes.put(EventConstants.Platform, "android");
        attributes.put(EventConstants.Connection, UIUtils.networkType(JumiaApplication.INSTANCE.getApplicationContext()));
        attributes.put(EventConstants.Date, new Date());

        return attributes;
    }
}
