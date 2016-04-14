package com.mobile.view;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.mobile.utils.TrackerDelegator;

/**
 * Activity used to manage the tracking and life cycle.
 * @author spereira
 */
public abstract class BaseTrackerActivity extends AppCompatActivity {

    private long mLaunchTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // For tracking
        mLaunchTime = System.currentTimeMillis();
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
