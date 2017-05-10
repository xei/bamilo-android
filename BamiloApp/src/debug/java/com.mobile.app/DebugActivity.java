package com.mobile.app;

import android.os.Bundle;

import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.BaseActivity;

import java.util.Set;

/**
 * DebugActivity
 * @author sergiopereira
 */
public abstract class DebugActivity extends BaseActivity {

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
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start debug drawer
    }

    @Override
    public void onResume() {
        super.onResume();
        // Resume debug drawer
    }

    @Override
    public void onPause() {
        super.onPause();
        // Pause debug drawer
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Stop debug drawer
    }

    @Override
    public void restartAppFlow() {
        super.restartAppFlow();
    }
}
