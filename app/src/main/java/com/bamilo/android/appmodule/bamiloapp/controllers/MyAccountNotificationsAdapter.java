package com.bamilo.android.appmodule.bamiloapp.controllers;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import com.bamilo.android.R;

import com.bamilo.android.framework.components.customfontviews.CheckBox;
import android.widget.TextView;

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
    private static final int SHOW_NOTIFICATION_CHECKBOX = 1;
//    private static final String TAG = MyAccountNotificationsAdapter.class.getName();

    private final String[] mOptions;
    private final int[] mCheckBoxes;
    Context mContext;
    int i =0;
    private final LayoutInflater mInflater;

    /**
     * The constructor for this adapter
     *
     * @param context
     *            The context from where this adapter is called
     * @param options
     *            The array containing the options to display
     * @param checkBoxes int array used to show or hide checkbox options
     */
    public MyAccountNotificationsAdapter(Context context, String[] options, int[] checkBoxes) {
        this.mOptions = options;
        this.mCheckBoxes = checkBoxes;
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
    public View getView(final int position, View convertView, ViewGroup parent) {
        // Recycle the convert view
        View view;
        ImageView img;
        if (convertView != null) {
            view = convertView;
        } else {
            view = mInflater.inflate(R.layout._def_single_line_with_checkbox_component, parent, false);
            //((SingleLineComponent) view).removeSelector();
            if (mOptions[i].equals(mContext.getString(R.string.notifications))) {
                img = (ImageView) view.findViewById(R.id.country_icon);
                img.setImageDrawable(view.getResources().getDrawable(R.drawable.announcements_icon));
                i++;
            } else {
                img = (ImageView) view.findViewById(R.id.country_icon);
                img.setImageDrawable(view.getResources().getDrawable(R.drawable.newsletter_icons));
            }
        }

        // Get the Notification item name
        TextView optionsName = (TextView) view.findViewById(R.id.tx_single_line_text);
        optionsName.setText(this.mOptions[position]);

        // Get the Notification checkbox
        final CheckBox optionsCheckbox = (CheckBox) view.findViewById(R.id.checkbox);
        if(this.mCheckBoxes[position] == SHOW_NOTIFICATION_CHECKBOX) {
            optionsCheckbox.setTag(NOTIFICATION_CHECKBOX_TAG);
            optionsCheckbox.setVisibility(View.VISIBLE);
            /*
            TODO: improve this approach
            WORKAROUND to set the correct checkbox status when user navigates
            to MyAccountFragment by clicking the back button
             */
            /*
            optionsCheckbox.post(new Runnable() {
                @Override
                public void run() {
                    optionsCheckbox.setChecked(Ad4PushTracker.getActiveAd4Push(mContext));
                }
            });

            optionsCheckbox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    Ad4PushTracker.setActiveAd4Push(mContext, isChecked);
                }
            });*/
        } else {
            optionsCheckbox.setVisibility(View.GONE);
        }

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