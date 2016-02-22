package com.mobile.app;

import android.content.Context;
import android.support.multidex.MultiDex;

/**
 * Application used to add all debug features.
 * @author sergio pereira
 */
public class DebugApplication extends JumiaApplication {

    /**
     * Enable support multi dex
     */
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        // Enabled multi dex
        MultiDex.install(this);
    }

    /**
     * Enable debug tools
     */
    @Override
    public void onCreate() {
        // #DEBUG Install debug tools pre on create application
        DebugTools.onCreateApplication(this);
        // Call super
        super.onCreate();
        // #DEBUG Install debug tools pos on create application
        DebugTools.onApplicationCreated(this);
    }

}
