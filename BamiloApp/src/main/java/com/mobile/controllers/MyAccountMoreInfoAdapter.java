package com.mobile.controllers;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.service.objects.statics.TargetHelper;
import com.mobile.service.utils.DeviceInfoHelper;
import com.mobile.utils.CheckVersion;
import com.mobile.view.R;

import java.util.List;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/10/15
 *
 *  *
 * @edit Shahrooz Jahanshah
 * @date 2016/10/31
 *
 */
public class MyAccountMoreInfoAdapter extends BaseAdapter{

    public final static int APP_VERSION_POSITION = 0;

    private final List<TargetHelper> staticPages;

    private final LayoutInflater mInflater;

    public MyAccountMoreInfoAdapter(@Nullable List<TargetHelper> staticPages, @NonNull Context context) {
        this.staticPages = staticPages;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        // Has always at least one item
        return 1 ;
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
            view = mInflater.inflate( (position == APP_VERSION_POSITION ? R.layout.my_account_app_version : R.layout._def_my_account_simple_item), parent, false);
        }

        // Get the Category Name
        TextView optionsName = (TextView) view.findViewById(R.id.option_name);

        if(position == APP_VERSION_POSITION) {
            optionsName.setText(R.string.app_version);
            TextView appVersionLabel = (TextView) view.findViewById(R.id.app_version_label);
            TextView updateLabel = (TextView) view.findViewById(R.id.update_label);
            ImageView img = (ImageView) view.findViewById(R.id.app_version_icon);
            img.setImageDrawable(view.getResources().getDrawable(R.drawable.app_ver_icons));
            appVersionLabel.setText(DeviceInfoHelper.getVersionName(view.getContext()));

            if(CheckVersion.needsToShowDialog()) {
                updateLabel.setText(R.string.update_now);
                view.setEnabled(true);
            } else {
                updateLabel.setText(R.string.up_to_date);
                view.setEnabled(false);
            }

        } else {
            return view;
        }

        return view;
    }
}