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
        // #DEBUG Install debug tools only for debug version
        DebugTools.initialize(this);
        // Call super
        super.onCreate();
    }

}
