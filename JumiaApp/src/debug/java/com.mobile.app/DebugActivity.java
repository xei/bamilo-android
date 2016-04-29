package com.mobile.app;

import android.os.Bundle;

import com.mobile.app.drawer.AbcDebugDrawerView;
import com.mobile.app.drawer.DebugMobileApiModel;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.BaseActivity;

import java.util.Set;

import io.palaima.debugdrawer.DebugDrawer;

/**
 * DebugActivity
 * @author sergiopereira
 */
public abstract class DebugActivity extends BaseActivity implements DebugMobileApiModel.ICustomMobileApi {

    private DebugDrawer mDebugDrawer;

    /**
     * Constructor used to initialize the navigation list component and the autocomplete handler
     */
    public DebugActivity(@NavigationAction.Type int action, Set<MyMenuItem> enabledMenuItems, int titleResId) {
        super(action, enabledMenuItems, titleResId);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Create debug drawer
        mDebugDrawer = AbcDebugDrawerView.onCreate(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start debug drawer
        AbcDebugDrawerView.onStart(mDebugDrawer);
    }

    @Override
    public void onResume() {
        super.onResume();
        // Resume debug drawer
        AbcDebugDrawerView.onResume(mDebugDrawer);
    }

    @Override
    public void onPause() {
        super.onPause();
        // Pause debug drawer
        AbcDebugDrawerView.onPause(mDebugDrawer);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Stop debug drawer
        AbcDebugDrawerView.onStop(mDebugDrawer);
    }

    @Override
    public void restartAppFlow() {
        super.restartAppFlow();
    }
}
