package com.mobile.controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.utils.imageloader.RocketImageLoader;
import com.mobile.view.R;

import de.akquinet.android.androlog.Log;

/**
 * This Class is used to create an adapter for the list of countries.
 * <p/>
 * <br>
 * 
 * Copyright (C) 2012 Rocket Internet - All Rights Reserved
 * <p/>
 * 
 * Unauthorized copying of this file, via any medium is strictly prohibited <br>
 * Proprietary and confidential.
 * 
 * @author Hugo Matilla
 * 
 * @version 1.00
 * 
 *          2013/04/10
 * 
 */

public class CountryAdapter extends ArrayAdapter<String> {

    private String[] values;
    private final String[] flagsList;
    private LayoutInflater mInflater;
    private Context ctx;
    private int sdk = -1;

    public CountryAdapter(Context context, String[] values, String[] flagsList) {
        super(context, R.layout.change_country_row, values);
        this.ctx = context;
        this.values = values;
        this.flagsList = flagsList;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        sdk = android.os.Build.VERSION.SDK_INT;
    }

    public void updateValues(String[] values) {
        this.values = values;
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Log.i("Country List", "Position: " + position);
        View rowView = convertView;
        if (rowView == null) {
            rowView = mInflater.inflate(R.layout.change_country_row, parent, false);
        }
        TextView textView = (TextView) rowView.findViewById(R.id.country_name);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.flag);
        textView.setText(values[position]);
        if (flagsList != null && flagsList.length > 0) RocketImageLoader.instance.loadImage(flagsList[position], imageView, null, R.drawable.no_image_small);
        return rowView;
    }
    
    
    /**
     * set selector for specific top and bottom positions on a list
     * @param rowView
     * @param position
     */
    @SuppressWarnings("unused")
    @Deprecated
    private void setSelector(View rowView,int position){
        
        if(values.length > 0 && position == (values.length - 1) && null != ctx){           
            if(-1 != sdk && sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                rowView.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.selector_item_bottom));
            } else {
                rowView.setBackground(ctx.getResources().getDrawable(R.drawable.selector_item_bottom));
            }          
        } else if(values.length > 0 && position == 0 && null != ctx){
            Log.d("SELECTOR","TOP:"+position);
            if(-1 != sdk && sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                rowView.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.selector_item_top));
            } else {
                rowView.setBackground(ctx.getResources().getDrawable(R.drawable.selector_item_top));
            }     
            
        } else {
            Log.d("SELECTOR","MIDDLE:"+position);
            if(-1 != sdk && sdk < android.os.Build.VERSION_CODES.JELLY_BEAN) {
                rowView.setBackgroundDrawable(ctx.getResources().getDrawable(R.drawable.selector_listitem_rounded_margin_highlight));
            } else {
                rowView.setBackground(ctx.getResources().getDrawable(R.drawable.selector_listitem_rounded_margin_highlight));
            }          
        }
        
    }
}
