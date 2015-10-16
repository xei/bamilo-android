package com.mobile.controllers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.statics.TargetHelper;
import com.mobile.newFramework.utils.DeviceInfoHelper;
import com.mobile.view.R;

import java.util.List;

/**
 * Created by rsoares on 10/15/15.
 */
public class MyAccountMoreInfoAdapter extends BaseAdapter{

    public final static int APP_VERSION_POSITION = 0;

    private List<TargetHelper> staticPages;

    private LayoutInflater mInflater;

    public MyAccountMoreInfoAdapter(@Nullable List<TargetHelper> staticPages, @NonNull Context context) {
        this.staticPages = staticPages;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {

        return 1 + (staticPages != null ? staticPages.size(): 0);
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = null;
        if (convertView != null) {
            view = convertView;
        } else {
            view = mInflater.inflate( (position == APP_VERSION_POSITION ? R.layout._def_my_account_app_version : R.layout.my_account_list_item), parent, false);
        }

        // Get the Category Name
        TextView optionsName = (TextView) view.findViewById(R.id.option_name);

        if(position == APP_VERSION_POSITION) {
            optionsName.setText(R.string.app_version);
            TextView appVersionLabel = (TextView) view.findViewById(R.id.app_version_label);
            TextView updateLabel = (TextView) view.findViewById(R.id.update_label);

            appVersionLabel.setText(DeviceInfoHelper.getVersionName(view.getContext()));
            updateLabel.setText(R.string.update_now);
        } else {
            TargetHelper targetHelper = staticPages.get(position - 1);
            optionsName.setText(targetHelper.getTargetTitle());
        }

        return view;
    }
}
