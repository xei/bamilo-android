package com.mobile.utils;

import com.mobile.app.JumiaApplication;
import com.mobile.constants.EventConstants;
import com.mobile.managers.AppManager;
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

        attributes.put(EventConstants.AppVersion, AppManager.getAppFullFormattedVersion());
        attributes.put(EventConstants.Platform, "android");
        attributes.put(EventConstants.Connection, UIUtils.networkType(JumiaApplication.INSTANCE.getApplicationContext()));
        attributes.put(EventConstants.Date, DateUtils.getWebNormalizedDateTimeString(new Date()));
        if(JumiaApplication.INSTANCE.isCustomerLoggedIn()) {
            String userGender = JumiaApplication.CUSTOMER.getGender();
            if(userGender != null) {
                attributes.put(EventConstants.Gender, userGender);
            }
        }

        return attributes;
    }
}
