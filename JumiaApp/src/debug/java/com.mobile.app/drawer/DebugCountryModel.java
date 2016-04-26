package com.mobile.app.drawer;

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

/**
 * Model used to show country info from preferences.
 *
 * @author sergiopereira
 */
public class DebugCountryModel extends BaseDebugModel implements CompoundButton.OnCheckedChangeListener {

    private final Context mContext;
    private TextView mTextView;

    public DebugCountryModel(@NonNull Context context) {
        mContext = context ;
    }

    @NonNull
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.dd_debug_drawer_module_country, parent, false);
        ((SwitchCompat) view.findViewById(R.id.dd_debug_item_country_switch)).setOnCheckedChangeListener(this);
        mTextView = (TextView) view.findViewById(R.id.dd_debug_item_country_text);
        return view;
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            String configs = CountryPersistentConfigs.toString(mContext);
            mTextView.setText(configs);
            mTextView.setVisibility(View.VISIBLE);
        } else {
            mTextView.setVisibility(View.GONE);
        }
    }

}
