package com.mobile.controllers;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.mobile.components.customfontviews.CheckBox;
import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.tracking.Ad4PushTracker;
import com.mobile.view.R;

/**
 * This Class is used to create an adapter for the list of account options. It is called by Home Activity.
 * <p/>
 * <br>
 * 
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * <p/>
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
 * 
 * @author Sergio Pereira
 * 
 * @version 1.01
 * 
 *          2012/06/19
 * 
 */
public class MyAccountNotificationsAdapter extends BaseAdapter {
    public static final String NOTIFICATION_CHECKBOX_TAG = "checkbox_notification_tag";
    String[] mOptions;
    Context mContext;
    private LayoutInflater mInflater;

    /**
     * The constructor for this adapter
     *
     * @param context
     *            The context from where this adapter is called
     * @param options
     *            The array containing the options to display
     */
    public MyAccountNotificationsAdapter(Context context, String[] options) {
        this.mOptions = options;
        this.mContext = context;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return this.mOptions.length;
    }

    @Override
    public Object getItem(int position) {
        return mOptions[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Recycle the convert view
        View view;
        if (convertView != null) {
            view = convertView;
        } else {
            view = mInflater.inflate(R.layout.my_account_notification_item, parent, false);
        }

        // Get the Notification item name
        TextView optionsName = (TextView) view.findViewById(R.id.option_name);
        optionsName.setText(this.mOptions[position]);

        // Get the Notification checkbox
        CheckBox optionsCheckbox = (CheckBox) view.findViewById(R.id.notification_checkbox);
        optionsCheckbox.setTag(NOTIFICATION_CHECKBOX_TAG);
        optionsCheckbox.setChecked(Ad4PushTracker.getActiveAd4Push(mContext));

        return view;
    }

    /**
     * #FIX: java.lang.IllegalArgumentException: The observer is null.
     * @solution from : https://code.google.com/p/android/issues/detail?id=22946 
     */
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if(observer !=null){
            super.unregisterDataSetObserver(observer);    
        }
    }
}
