package com.mobile.app;

import android.content.Context;
import android.support.multidex.MultiDex;
import com.bamilo.android.appmodule.bamiloapp.app.BamiloApplication;
import com.bamilo.android.framework.service.rest.AigRestAdapter;
import com.bamilo.android.framework.service.utils.DeviceInfoHelper;


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

            //Leakcanary
            /*if (LeakCanary.isInAnalyzerProcess(this)) {
                // This process is dedicated to LeakCanary for heap analysis.
                // You should not init your app in this process.
                return;
            }
            LeakCanary.install(this);*/

            //AIG Rest Adapter
            AigRestAdapter.enableDebug();
        }
    }

    @Override
    public boolean isDebuggable() {
        return DeviceInfoHelper.isDebuggable(this);
    }
}
