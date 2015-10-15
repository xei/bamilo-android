package com.mobile.controllers;

import android.content.Context;
import android.view.View;

import com.mobile.components.customfontviews.TextView;
import com.mobile.view.R;
import com.mobile.view.fragments.MyAccountFragment;

/**
 * This Class is used to create an adapter for the list of sharing the app.
 * <p/>
 * <br>
 * 
 * Copyright (C) 2015 Rocket Internet - All Rights Reserved
 * <p/>
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
 * 
 * @author Ricardo Soares
 * 
 * @version 1.0
 * 
 */
public class AppSharingSettingsAdapter extends MyAccountSettingsAdapter {
    
    public AppSharingSettingsAdapter(Context context, String[] options) {
        super(context, options);
    }
    
    @Override
    protected void setViewByPosition(View view, int position) {
        
        TextView optionsDescription = (TextView) view.findViewById(R.id.option_info);
        // Validate the current position
           switch (position) {
               case MyAccountFragment.POSITION_SHARE_APP:
                   optionsDescription.setText(mContext.getResources().getString(R.string.can_share_app_with_friends));
                   break;
           }
    }
}
