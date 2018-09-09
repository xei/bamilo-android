package com.bamilo.android.appmodule.bamiloapp.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bamilo.android.appmodule.bamiloapp.utils.TrackerDelegator;

/**
 * Activity used to manage the tracking and life cycle.
 * @author spereira
 */
public abstract class BaseTrackerActivity extends AppCompatActivity {

    private long mTrackingLaunchTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Tracking
        mTrackingLaunchTime = System.currentTimeMillis();
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tracking
        TrackerDelegator.onResumeActivity(mTrackingLaunchTime);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Tracking
        TrackerDelegator.onPauseActivity();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Tracking
        TrackerDelegator.onDestroyActivity();
    }
}
