package com.mobile.app;

import android.app.Application;
import android.support.annotation.NonNull;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.mobile.newFramework.rest.AigHttpClient;
import com.mobile.newFramework.rest.AigRestAdapter;
import com.mobile.newFramework.utils.DeviceInfoHelper;
import com.mobile.newFramework.utils.output.Print;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Class used to initialize debug tools for debug version.
 */
public class DebugTools {

    private static final String TAG = DebugTools.class.getSimpleName();

    private static boolean isDebuggable = false;

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
        isDebuggable = DeviceInfoHelper.isDebuggable(application);
        // Validate and initialize
        if (isDebuggable) {
            // Logs
            Print.initializeAndroidMode(application);
            // #LEAK :: https://github.com/square/leakcanary
            sRefWatcher = LeakCanary.install(application);
            // #STETHO :: http://facebook.github.io/stetho/
            Stetho.initializeWithDefaults(application);
            // #RETROFIT
            AigRestAdapter.enableDebug();
            // #OK HTTP
            AigHttpClient.getInstance(application).addDebugNetworkInterceptors(new StethoInterceptor());
            // Warning
            Print.w(TAG, "WARNING: APPLICATION IN DEBUG MODE");
        }
    }

    /**
     * Execute callbacks based on debuggable build type.
     */
    @SuppressWarnings("unused")
    public static void execute(@NonNull IBuildTypeCode callback) {
        if (isDebuggable) {
            callback.onDebugBuildType();
        }
    }

    /**
     * Watch weak reference.
     */
    @SuppressWarnings("unused")
    public static void watch(Object watchedReference) {
        if (isDebuggable) {
            sRefWatcher.watch(watchedReference);
        }
    }

}
