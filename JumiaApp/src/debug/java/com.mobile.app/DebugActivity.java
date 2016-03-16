package com.mobile.app;

import android.os.Bundle;

import com.mobile.app.drawer.DebugDrawerView;
import com.mobile.newFramework.utils.EventType;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.BaseActivity;

import java.util.Set;

import io.palaima.debugdrawer.DebugDrawer;

/**
 * DebugActivity
 */
public abstract class DebugActivity extends BaseActivity {

    private DebugDrawer mDebugDrawer;

    public DebugActivity(@NavigationAction.Type int action, Set<MyMenuItem> enabledMenuItems, Set<EventType> userEvents, int titleResId, int contentLayoutId) {
        super(action, enabledMenuItems, userEvents, titleResId, contentLayoutId);
    }

    public DebugActivity(int activityLayoutId, @NavigationAction.Type int action, Set<MyMenuItem> enabledMenuItems, int titleResId) {
        super(activityLayoutId, action, enabledMenuItems, titleResId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create debug drawer
        mDebugDrawer = DebugDrawerView.onCreate(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start debug drawer
        DebugDrawerView.onStart(mDebugDrawer);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Resume debug drawer
        DebugDrawerView.onResume(mDebugDrawer);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Pause debug drawer
        DebugDrawerView.onPause(mDebugDrawer);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Stop debug drawer
        DebugDrawerView.onStop(mDebugDrawer);
    }
}
