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

import com.mobile.preferences.CountryPersistentConfigs;
import com.mobile.view.R;

import io.palaima.debugdrawer.base.DebugModule;

/**
 * Created by spereira on 2/23/16.
 */
public class DebugCountryModel implements DebugModule, CompoundButton.OnCheckedChangeListener {

    private final Context mContext;
    private TextView mTextView;

    public DebugCountryModel(@NonNull Activity activity) {
        mContext = activity;
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        // Get view
        View view = inflater.inflate(R.layout.dd_debug_drawer_item_country_configs, parent, false);
        // Get switch
        SwitchCompat switchView = (SwitchCompat) view.findViewById(R.id.dd_debug_item_country_switch);
        switchView.setOnCheckedChangeListener(this);
        // Get text
        mTextView = (TextView) view.findViewById(R.id.dd_debug_item_country_text);
        return view;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            //mTextView.setText(Html.fromHtml(CountryPersistentConfigs.toString(mContext)));
            mTextView.setText(CountryPersistentConfigs.toString(mContext));
            mTextView.setVisibility(View.VISIBLE);
        } else {
            mTextView.setVisibility(View.GONE);
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
