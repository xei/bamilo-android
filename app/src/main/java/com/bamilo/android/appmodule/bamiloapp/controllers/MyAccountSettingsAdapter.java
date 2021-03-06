package com.bamilo.android.appmodule.bamiloapp.controllers;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import android.widget.TextView;
import com.bamilo.android.R;

/**
 * This Class is used to create an adapter for the list of account options. It is called by Home Activity.
 * <p/>
 * <br>
 * <p/>
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * <p/>
 * <p/>
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
 *
 * @author Sergio Pereira
 * @version 1.01
 *          <p/>
 *          2012/06/19
 */
public class MyAccountSettingsAdapter extends BaseAdapter {

    String[] mOptions;
    Context mContext;
    int i = 0;
    private final LayoutInflater mInflater;

    /**
     * The constructor for this adapter
     *
     * @param context The context from where this adapter is called
     * @param options The array containing the options to display
     */
    public MyAccountSettingsAdapter(Context context, String[] options) {
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
        ImageView img;

        if (convertView != null) {
            view = convertView;
        } else {
            view = mInflater.inflate(R.layout.gen_single_line_list, parent, false);
            if (mOptions[i].equals(mContext.getString(R.string.myaccount_userdata))) {
                img = (ImageView) view.findViewById(R.id.country_icon);
                img.setImageDrawable(view.getResources().getDrawable(R.drawable.user_information_icons));
                i++;
            }else if (mOptions[i].equals(mContext.getString(R.string.share_the_app))) {
                img = (ImageView) view.findViewById(R.id.country_icon);
                img.setImageDrawable(view.getResources().getDrawable(R.drawable.share_icons));
                i++;
            } else if (mOptions[i].equals(mContext.getString(R.string.rate_the_app))) {
                img = (ImageView) view.findViewById(R.id.country_icon);
                img.setImageDrawable(view.getResources().getDrawable(R.drawable.rate_icons));
                i++;
            } else if (mOptions[i].equals(mContext.getString(R.string.my_addresses))) {
                img = (ImageView) view.findViewById(R.id.country_icon);
                img.setImageDrawable(view.getResources().getDrawable(R.drawable.my_address_icon));
            }
        }


       /* }*/


        // Get the Category Name
        ((TextView) view.findViewById(R.id.tx_single_line_text)).setText(this.mOptions[position]);
        // Return the Category Item View
        return view;
    }

    /**
     * #FIX: java.lang.IllegalArgumentException: The observer is null.
     *
     * @solution from : https://code.google.com/p/android/issues/detail?id=22946
     */
    @Override
    public void unregisterDataSetObserver(DataSetObserver observer) {
        if (observer != null) {
            super.unregisterDataSetObserver(observer);
        }
    }
}