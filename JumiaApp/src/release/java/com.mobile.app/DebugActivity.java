package com.mobile.app;

import com.mobile.newFramework.utils.EventType;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.BaseActivity;

import java.util.Set;

/**
 * Fake debug activity for release
 * @author sergiopereira
 */
public abstract class DebugActivity extends BaseActivity {

    public DebugActivity(@NavigationAction.Type int action, Set<MyMenuItem> enabledMenuItems, Set<EventType> userEvents, int titleResId, int contentLayoutId) {
        super(action, enabledMenuItems, userEvents, titleResId, contentLayoutId);
    }

    public DebugActivity(int activityLayoutId, @NavigationAction.Type int action, Set<MyMenuItem> enabledMenuItems, int titleResId) {
        super(activityLayoutId, action, enabledMenuItems, titleResId);
    }
}
