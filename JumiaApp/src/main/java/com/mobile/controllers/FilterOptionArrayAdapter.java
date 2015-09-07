package com.mobile.controllers;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import com.mobile.components.customfontviews.CheckBox;
import com.mobile.components.customfontviews.TextView;
import com.mobile.newFramework.objects.catalog.filters.MultiFilterOptionService;
import com.mobile.view.R;

import java.util.List;

/**
 * Class used to fill the list view with filter options
 * @author sergiopereira
 *
 */
 public class FilterOptionArrayAdapter extends ArrayAdapter<MultiFilterOptionService> {
        
    private static int layout = R.layout.dialog_list_sub_item_2;

    /**
     * Constructor
     * @param context
     * @param objects
     */
    public FilterOptionArrayAdapter(Context context, List<MultiFilterOptionService> objects) {
        super(context, layout, objects);
    }

    /*
     * (non-Javadoc)
     * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get Filter

        MultiFilterOptionService option = getItem(position);

        // Validate current view
        if (convertView == null) convertView = LayoutInflater.from(getContext()).inflate(layout, null);
        // Set title
        ((TextView) convertView.findViewById(R.id.dialog_item_title)).setText(option.getLabel());
        // Set check box
        ((CheckBox) convertView.findViewById(R.id.dialog_item_checkbox)).setChecked(option.isSelected());
        // Return the filter view
        return convertView;
    }
}