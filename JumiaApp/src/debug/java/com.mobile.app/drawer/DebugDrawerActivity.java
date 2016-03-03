package com.mobile.app.drawer;

import android.os.Bundle;

import com.mobile.utils.Toast;
import com.mobile.view.MainFragmentActivity;

import java.util.Arrays;

import io.palaima.debugdrawer.DebugDrawer;
import io.palaima.debugdrawer.actions.ActionsModule;
import io.palaima.debugdrawer.actions.ButtonAction;
import io.palaima.debugdrawer.actions.SpinnerAction;
import io.palaima.debugdrawer.actions.SwitchAction;
import io.palaima.debugdrawer.commons.BuildModule;
import io.palaima.debugdrawer.commons.DeviceModule;
import io.palaima.debugdrawer.commons.NetworkModule;
import io.palaima.debugdrawer.commons.SettingsModule;
import io.palaima.debugdrawer.scalpel.ScalpelModule;


public class DebugDrawerActivity extends MainFragmentActivity {

    private DebugDrawer debugDrawer;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreatedActivity();
    }

    protected void onCreatedActivity() {
        SwitchAction switchAction = new SwitchAction("Test switch", new SwitchAction.Listener() {
            @Override
            public void onCheckedChanged(boolean value) {
                Toast.makeText(getApplicationContext(), "Switch checked", Toast.LENGTH_LONG).show();
            }
        });

        ButtonAction buttonAction = new ButtonAction("Test button", new ButtonAction.Listener() {
            @Override
            public void onClick() {
                Toast.makeText(getApplicationContext(), "Button clicked", Toast.LENGTH_LONG).show();
            }
        });

        SpinnerAction<String> spinnerAction = new SpinnerAction<>(
                Arrays.asList("First", "Second", "Third"),
                new SpinnerAction.OnItemSelectedListener<String>() {
                    @Override
                    public void onItemSelected(String value) {
                        Toast.makeText(getApplicationContext(), "Spinner item selected - " + value, Toast.LENGTH_LONG).show();
                    }
                }
        );

        debugDrawer = new DebugDrawer
                .Builder(this)
                .modules(
                        new MobApiModel(this),
                        new ActionsModule(switchAction, buttonAction, spinnerAction),
                        new DeviceModule(this),
                        new BuildModule(this),
                        new NetworkModule(this),
                        new ScalpelModule(this),
                        new SettingsModule(this)
                ).build();
    }

    @Override
    protected void onStart() {
        super.onStart();
        debugDrawer.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        debugDrawer.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        debugDrawer.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        debugDrawer.onStop();
    }

}
