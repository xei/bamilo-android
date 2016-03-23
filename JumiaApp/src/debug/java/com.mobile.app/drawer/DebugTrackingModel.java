package com.mobile.app.drawer;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import com.mobile.newFramework.tracking.AdjustTracker;
import com.mobile.newFramework.tracking.AnalyticsGoogle;
import com.mobile.newFramework.tracking.gtm.GTMManager;
import com.mobile.view.R;

import io.palaima.debugdrawer.base.DebugModule;

/**
 * Created by spereira on 2/23/16.
 */
public class DebugTrackingModel implements DebugModule, CompoundButton.OnCheckedChangeListener {

    private final Context mContext;
    private View mContainer;

    public DebugTrackingModel(@NonNull Activity activity) {
        mContext = activity;
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        // Get view
        View view = inflater.inflate(R.layout.dd_debug_drawer_item_tracking, parent, false);
        // Get switch
        SwitchCompat switchView = (SwitchCompat) view.findViewById(R.id.dd_debug_item_tracking_switch);
        switchView.setOnCheckedChangeListener(this);
        // Get text
        mContainer = view.findViewById(R.id.dd_debug_item_tracking_container);
        return view;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        int id = buttonView.getId();
        switch (id) {
            case R.id.dd_debug_item_tracking_ga_enable:
                break;
            case R.id.dd_debug_item_tracking_switch:
            default:
                mainSwitch(isChecked);
                break;
        }
    }

    private void mainSwitch(boolean isChecked) {
        if (isChecked) {
            // GA
            ((TextView) mContainer.findViewById(R.id.dd_debug_item_tracking_ga_id)).setText(AnalyticsGoogle.get().getId());
            // GTM
            ((TextView) mContainer.findViewById(R.id.dd_debug_item_tracking_gtm_id)).setText(GTMManager.get().getId());
            // ADJUST
            ((TextView) mContainer.findViewById(R.id.dd_debug_item_tracking_adjust_id)).setText(AdjustTracker.get().getId());
            // NEW RELIC
            ((TextView) mContainer.findViewById(R.id.dd_debug_item_tracking_relic_id)).setText("WWWWWWW");
            // ACCENGAGE
            ((TextView) mContainer.findViewById(R.id.dd_debug_item_tracking_accengage_id)).setText("ZZZZZZ");
            // HOCKEY APP
            ((TextView) mContainer.findViewById(R.id.dd_debug_item_tracking_hockey_id)).setText("TTTTTTT");
            // Show
            mContainer.setVisibility(View.VISIBLE);
        } else {
            mContainer.setVisibility(View.GONE);
        }
    }

    @Override
    public void onOpened() {
        // ...
    }

    @Override
    public void onClosed() {
        // ...
    }

    @Override
    public void onResume() {
        // ...
    }

    @Override
    public void onPause() {
        // ...
    }

    @Override
    public void onStart() {
        // ...
    }

    @Override
    public void onStop() {
        // ...
    }

}
