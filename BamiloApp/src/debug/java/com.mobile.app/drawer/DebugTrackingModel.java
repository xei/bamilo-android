package com.mobile.app.drawer;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;
import com.mobile.view.R;

import com.mobile.newFramework.tracking.AdjustTracker;
import com.mobile.newFramework.tracking.AnalyticsGoogle;
import com.mobile.newFramework.tracking.gtm.GTMManager;

/**
 * Model used to show trackers ids and enable/disable the respective log.
 * @author sergiopereira
 */
public class DebugTrackingModel extends BaseDebugModel implements CompoundButton.OnCheckedChangeListener {

    private final Context mContext;
    private View mContainer;

    public DebugTrackingModel(@NonNull Context context) {
        mContext = context;
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
            case R.id.dd_debug_item_tracking_ga_enable:
                AnalyticsGoogle.get().debugMode(mContext, isChecked);
                break;
            case R.id.dd_debug_item_tracking_gtm_enable:
                GTMManager.get().debugMode(mContext, isChecked);
                break;
            case R.id.dd_debug_item_tracking_adjust_enable:
                AdjustTracker.get().debugMode(mContext, isChecked);
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
            ((TextView) mContainer.findViewById(R.id.dd_debug_item_tracking_gtm_id)).setText(GTMManager.get().getId(mContext));
            // ADJUST
            ((TextView) mContainer.findViewById(R.id.dd_debug_item_tracking_adjust_id)).setText(AdjustTracker.get().getId(mContext));
            // Show
            mContainer.setVisibility(View.VISIBLE);
        } else {
            mContainer.setVisibility(View.GONE);
        }
    }

}
