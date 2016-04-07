package com.mobile.app.drawer;

import android.support.annotation.NonNull;

import com.mobile.app.DebugActivity;

import io.palaima.debugdrawer.DebugDrawer;
import io.palaima.debugdrawer.commons.DeviceModule;
import io.palaima.debugdrawer.commons.SettingsModule;
import io.palaima.debugdrawer.scalpel.ScalpelModule;

/**
 * DebugDrawerView
 * @author sergiopereira
 */
public class AbcDebugDrawerView {

    public static DebugDrawer onCreate(@NonNull final DebugActivity activity) {
        return new DebugDrawer.Builder(activity)
                .modules(
                        new DebugAppInfoModel(activity),
                        new DebugCountryModel(activity),
                        new DebugTrackingModel(activity),
                        new DebugMobileApiModel(activity),
                        new DeviceModule(activity),
                        new SettingsModule(activity),
                        new ScalpelModule(activity)
                        // TODO Send notification
                        //new NetworkModule(activity),
                        //new OkHttpModule(AigHttpClient.getInstance().getOkHttpClient()),
                        //new BuildModule(activity),
                ).build();
    }

    public static void onStart(DebugDrawer debugDrawer) {
        if (debugDrawer != null) debugDrawer.onStart();
    }

    public static void onResume(DebugDrawer debugDrawer) {
        if (debugDrawer != null) debugDrawer.onResume();
    }

    public static void onPause(DebugDrawer debugDrawer) {
        if (debugDrawer != null) debugDrawer.onPause();
    }

    public static void onStop(DebugDrawer debugDrawer) {
        if (debugDrawer != null) debugDrawer.onStop();
    }

}
