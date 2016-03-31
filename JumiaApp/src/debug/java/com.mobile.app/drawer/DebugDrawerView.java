package com.mobile.app.drawer;

import android.app.Activity;

import io.palaima.debugdrawer.DebugDrawer;
import io.palaima.debugdrawer.commons.DeviceModule;
import io.palaima.debugdrawer.commons.NetworkModule;
import io.palaima.debugdrawer.commons.SettingsModule;
import io.palaima.debugdrawer.scalpel.ScalpelModule;


public class DebugDrawerView {

    public static DebugDrawer onCreate(final Activity activity) {
        return new io.palaima.debugdrawer.DebugDrawer
                .Builder(activity)
                .modules(
                        new DebugAppInfoModel(activity),
                        new DebugCountryModel(activity),
                        new DebugTrackingModel(activity),
                        new NetworkModule(activity),
                        new DeviceModule(activity),
                        new ScalpelModule(activity),
                        new SettingsModule(activity)
                        // TODO Send notification
                        // TODO Network Error: Maintenance Page | Kickout Page
                        //new BuildModule(activity),
                        //new ActionsModule(createSwitchAction(activity)),
                        //new ActionsModule(createButtonAction(activity)),
                        //new ActionsModule(createSpinnerAction(activity)),
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

//    /*
//     * ########### MODELS ###########
//     */
//
//
//    public static SwitchAction createSwitchAction(final Activity activity) {
//        return new SwitchAction("Test switch", new SwitchAction.Listener() {
//            @Override
//            public void onCheckedChanged(boolean value) {
//                Toast.makeText(activity.getApplicationContext(), "Switch checked", Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//
//    public static ButtonAction createButtonAction(final Activity activity) {
//        return new ButtonAction("Test button", new ButtonAction.Listener() {
//            @Override
//            public void onClick() {
//                Toast.makeText(activity.getApplicationContext(), "Button clicked", Toast.LENGTH_LONG).show();
//            }
//        });
//    }
//
//    public static SpinnerAction createSpinnerAction(final Activity activity) {
//        return new SpinnerAction<>(
//                Arrays.asList("First", "Second", "Third"),
//                new SpinnerAction.OnItemSelectedListener<String>() {
//                    @Override
//                    public void onItemSelected(String value) {
//                        Toast.makeText(activity.getApplicationContext(), "Spinner item selected - " + value, Toast.LENGTH_LONG).show();
//                    }
//                }
//        );
//    }

}
