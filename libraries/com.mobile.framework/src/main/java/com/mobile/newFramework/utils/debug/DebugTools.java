package com.mobile.newFramework.utils.debug;

import android.app.Application;
import android.support.annotation.NonNull;

import com.facebook.stetho.Stetho;
import com.mobile.newFramework.utils.DeviceInfoHelper;
import com.mobile.newFramework.utils.output.Print;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Class used to initialize debug tools for debug version.
 */
public class DebugTools {

    public static boolean IS_DEBUGGABLE = false;

    private static final String TAG = DebugTools.class.getSimpleName();

    private static RefWatcher sRefWatcher;

    /**
     * Interface used to execute debuggable code.
     */
    public interface IBuildTypeCode {
        void onDebugBuildType();
    }

    /**
     * Install and initialize debug tools only for debug version
     */
    public static void initialize(Application application) {
        // Get flag from application
        IS_DEBUGGABLE = DeviceInfoHelper.isDebuggable(application);
        // Validate and initialize
        if (IS_DEBUGGABLE) {
            // Logs
            Print.initializeAndroidMode(application);
            // #LEAK :: https://github.com/square/leakcanary
            sRefWatcher = LeakCanary.install(application);
            // #STETHO :: http://facebook.github.io/stetho/
            Stetho.initializeWithDefaults(application);
            // Warning
            Print.w(TAG, "WARNING: APPLICATION IN DEBUG MODE");
        }
    }

    /**
     * Execute callbacks based on debuggable build type.
     */
    public static void execute(@NonNull IBuildTypeCode callback) {
        if (IS_DEBUGGABLE) {
            callback.onDebugBuildType();
        }
    }

    /**
     * Watch weak reference.
     */
    public static void watch(Object watchedReference) {
        if (IS_DEBUGGABLE) {
            sRefWatcher.watch(watchedReference);
        }
    }

}
