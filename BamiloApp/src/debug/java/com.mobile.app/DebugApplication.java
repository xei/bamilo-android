package com.mobile.app;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.mobile.newFramework.rest.AigHttpClient;
import com.mobile.newFramework.rest.AigRestAdapter;
import com.mobile.newFramework.utils.output.Print;
import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

import com.mobile.newFramework.utils.DeviceInfoHelper;

public class DebugApplication extends BamiloApplication {

    public static RefWatcher sRefWatcher;

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);

        //Enabled multi dex
        MultiDex.install(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (this.isDebuggable()) {
            Print.initializeAndroidMode(this);

            //Leakcanary
            sRefWatcher = LeakCanary.install(this);

            //AIG Rest Adapter
            AigRestAdapter.enableDebug();

            //Stetho
            Stetho.initializeWithDefaults(this);
            AigHttpClient.getInstance(this).addDebugNetworkInterceptors(new StethoInterceptor());

            Print.w("DebugApplication", "WARNING: APPLICATION IN DEBUG MODE");
        }
    }

    @Override
    public boolean isDebuggable() {
        return DeviceInfoHelper.isDebuggable(this);
    }
}
