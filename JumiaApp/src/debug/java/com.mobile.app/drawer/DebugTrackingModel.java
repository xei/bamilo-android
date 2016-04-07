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

import com.mobile.newFramework.tracking.Ad4PushTracker;
import com.mobile.newFramework.tracking.AdjustTracker;
import com.mobile.newFramework.tracking.AnalyticsGoogle;
import com.mobile.newFramework.tracking.NewRelicTracker;
import com.mobile.newFramework.tracking.gtm.GTMManager;
import com.mobile.view.R;

/**
 * Model used to show trackers ids and enable/disable the respective log.
 * @author spereira
 */
public class DebugTrackingModel extends BaseDebugModel implements CompoundButton.OnCheckedChangeListener {

    private final Context mContext;
    private View mContainer;

    public DebugTrackingModel(@NonNull Activity activity) {
        mContext = activity;
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        // Get view
        View view = inflater.inflate(R.layout.dd_debug_drawer_module_tracking, parent, false);
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
            case R.id.dd_debug_item_tracking_accengage_enable:
                Ad4PushTracker.get().debugMode(mContext, isChecked);
                break;
            case R.id.dd_debug_item_tracking_ga_enable:
                AnalyticsGoogle.get().debugMode(mContext, isChecked);
                break;
            case R.id.dd_debug_item_tracking_gtm_enable:
                GTMManager.get().debugMode(mContext, isChecked);
                break;
            case R.id.dd_debug_item_tracking_adjust_enable:
                AdjustTracker.get().debugMode(mContext, isChecked);
                break;
            case R.id.dd_debug_item_tracking_relic_enable:
                NewRelicTracker.get().debugMode(mContext, isChecked);
                break;
            case R.id.dd_debug_item_tracking_switch:
            default:
                mainSwitch(isChecked);
                break;
        }
    }

    private void mainSwitch(boolean isChecked) {
        if (isChecked) {
            // ACCENGAGE
            ((TextView) mContainer.findViewById(R.id.dd_debug_item_tracking_accengage_id)).setText(Ad4PushTracker.get().getId());
            // GA
            ((TextView) mContainer.findViewById(R.id.dd_debug_item_tracking_ga_id)).setText(AnalyticsGoogle.get().getId());
            // GTM
            ((TextView) mContainer.findViewById(R.id.dd_debug_item_tracking_gtm_id)).setText(GTMManager.get().getId(mContext));
            // ADJUST
            ((TextView) mContainer.findViewById(R.id.dd_debug_item_tracking_adjust_id)).setText(AdjustTracker.get().getId(mContext));
            // NEW RELIC
            ((TextView) mContainer.findViewById(R.id.dd_debug_item_tracking_relic_id)).setText(NewRelicTracker.get().getId(mContext));
            // Show
            mContainer.setVisibility(View.VISIBLE);
        } else {
            mContainer.setVisibility(View.GONE);
        }
    }

}
