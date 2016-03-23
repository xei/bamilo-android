package com.mobile.app.drawer;

import android.app.Activity;

import com.mobile.utils.Toast;

import java.util.Arrays;

import io.palaima.debugdrawer.DebugDrawer;
import io.palaima.debugdrawer.actions.ButtonAction;
import io.palaima.debugdrawer.actions.SpinnerAction;
import io.palaima.debugdrawer.actions.SwitchAction;
import io.palaima.debugdrawer.commons.BuildModule;
import io.palaima.debugdrawer.commons.DeviceModule;
import io.palaima.debugdrawer.commons.NetworkModule;
import io.palaima.debugdrawer.commons.SettingsModule;
import io.palaima.debugdrawer.scalpel.ScalpelModule;


public class DebugDrawerView {

    public static DebugDrawer onCreate(final Activity activity) {
        return new io.palaima.debugdrawer.DebugDrawer
                .Builder(activity)
                .modules(
                        new DebugCountryModel(activity),
//                        new ActionsModule(createSwitchAction(activity)),
//                        new ActionsModule(createButtonAction(activity)),
//                        new ActionsModule(createSpinnerAction(activity)),
                        new DeviceModule(activity),
                        new BuildModule(activity),
                        new NetworkModule(activity),
                        new ScalpelModule(activity),
                        new SettingsModule(activity)
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

    // TODO : Create these models
    // Show country configs
    // IDs from 3rd party libraries
    // Network Error: Maintenance Page | Kickout Page

    /*
     * ########### MODELS ###########
     */


    public static SwitchAction createSwitchAction(final Activity activity) {
        return new SwitchAction("Test switch", new SwitchAction.Listener() {
            @Override
            public void onCheckedChanged(boolean value) {
                Toast.makeText(activity.getApplicationContext(), "Switch checked", Toast.LENGTH_LONG).show();
            }
        });
    }

    public static ButtonAction createButtonAction(final Activity activity) {
        return new ButtonAction("Test button", new ButtonAction.Listener() {
            @Override
            public void onClick() {
                Toast.makeText(activity.getApplicationContext(), "Button clicked", Toast.LENGTH_LONG).show();
            }
        });
    }

    public static SpinnerAction createSpinnerAction(final Activity activity) {
        return new SpinnerAction<>(
                Arrays.asList("First", "Second", "Third"),
                new SpinnerAction.OnItemSelectedListener<String>() {
                    @Override
                    public void onItemSelected(String value) {
                        Toast.makeText(activity.getApplicationContext(), "Spinner item selected - " + value, Toast.LENGTH_LONG).show();
                    }
                }
        );
    }

}
