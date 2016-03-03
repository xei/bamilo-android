package com.mobile.app;

import com.mobile.view.R;
import com.mobile.view.SplashScreenActivity;

/**
 *
 */
public class DebugSplashScreenActivity extends SplashScreenActivity {

    @Override
    protected Class<?> getActivityClassForDevice() {
        return !getResources().getBoolean(R.bool.isTablet) ? DebugActivity.class : DebugTabletActivity.class;
    }
}
