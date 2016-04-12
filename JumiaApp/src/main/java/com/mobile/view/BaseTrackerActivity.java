package com.mobile.view;

import android.os.Bundle;

import com.mobile.utils.TrackerDelegator;
import com.parkourmethod.parkour.Parkour;

/**
 * Activity used to manage the tracking and life cycle.
 * @author spereira
 */
public abstract class BaseTrackerActivity extends Parkour {

    private long mLaunchTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // For tracking
        mLaunchTime = System.currentTimeMillis();
        // Tracking
        TrackerDelegator.onCreateActivity(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Tracking
        TrackerDelegator.onResumeActivity(mLaunchTime);
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
