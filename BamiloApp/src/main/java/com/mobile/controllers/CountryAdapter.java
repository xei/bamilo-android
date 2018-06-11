package com.mobile.controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mobile.utils.SingleLineComponent;
import com.mobile.utils.imageloader.ImageManager;
import com.mobile.view.R;

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
    private final LayoutInflater mInflater;

    public CountryAdapter(Context context, String[] values, String[] flagsList) {
        super(context, R.layout.gen_single_line_with_icon, values);
        this.values = values;
        this.flagsList = flagsList;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public void updateValues(String[] values) {
        this.values = values;
        this.notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SingleLineComponent rowView = (SingleLineComponent) convertView;
        if (rowView == null) {
            rowView = (SingleLineComponent) mInflater.inflate(R.layout.gen_single_line_with_icon, parent, false);
        }
        TextView textView = rowView.getTextView();
        ImageView imageView = rowView.getStartImageView();
        rowView.showImageStartViewVisible();
        textView.setText(values[position]);
        if (flagsList != null && flagsList.length > 0) {
            ImageManager.getInstance().loadImage(flagsList[position], imageView, null, R.drawable.no_image_large, false);
        }
        return rowView;
    }

}
