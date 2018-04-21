package com.mobile.app;

import android.content.Context;
import android.support.multidex.MultiDex;

import com.facebook.stetho.Stetho;
import com.facebook.stetho.okhttp.StethoInterceptor;
import com.mobile.service.rest.AigHttpClient;
import com.mobile.service.rest.AigRestAdapter;
import com.mobile.service.utils.output.Print;
//import com.squareup.leakcanary.LeakCanary;

import com.mobile.service.utils.DeviceInfoHelper;

public class DebugApplication extends BamiloApplication {

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
            /*if (LeakCanary.isInAnalyzerProcess(this)) {
                // This process is dedicated to LeakCanary for heap analysis.
                // You should not init your app in this process.
                return;
            }
            LeakCanary.install(this);*/

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
