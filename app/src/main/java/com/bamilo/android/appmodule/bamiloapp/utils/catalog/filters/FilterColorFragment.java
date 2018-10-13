package com.bamilo.android.appmodule.bamiloapp.utils.catalog.filters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;
import com.bamilo.android.appmodule.bamiloapp.controllers.FilterOptionArrayAdapter;
import com.bamilo.android.framework.service.objects.catalog.filters.CatalogCheckFilter;
import com.bamilo.android.framework.service.objects.catalog.filters.CatalogColorFilterOption;
import com.bamilo.android.framework.service.objects.catalog.filters.MultiFilterOptionInterface;
import com.bamilo.android.framework.service.utils.DeviceInfoHelper;
import com.bamilo.android.R;

/**
 * 
 * @author sergiopereira
 *
 */
public class FilterColorFragment extends FilterCheckFragment {
    
    private static final String TAG = FilterColorFragment.class.getSimpleName();

    public static FilterColorFragment newInstance(Bundle bundle) {
        FilterColorFragment frag = new FilterColorFragment();
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    protected FilterOptionArrayAdapter createAdapter() {
        return new FilterColorOptionArrayAdapter(getActivity(), mFilter);
    }

     public static class FilterColorOptionArrayAdapter extends FilterOptionArrayAdapter {
            
        private static int layout = R.layout.list_sub_item_2;

        public FilterColorOptionArrayAdapter(Context context, CatalogCheckFilter catalogCheckFilter) {
            super(context, catalogCheckFilter);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get Filter
            MultiFilterOptionInterface option = getItem(position);
            if(option instanceof CatalogColorFilterOption){
                if (option.isSelected()) {
                    addSelectedItem(option, position);
                }
                // Validate current view
                if (convertView == null) convertView = LayoutInflater.from(getContext()).inflate(layout, null);
                // Set color box
                final GradientDrawable gradient = new GradientDrawable();
                gradient.setShape(GradientDrawable.OVAL);
                gradient.setColor(Color.parseColor(((CatalogColorFilterOption) option).getHexValue()));
                gradient.setStroke(1, convertView.getResources().getColor(R.color.black_700));
                // Set color box
                final View itemColorBox = convertView.findViewById(R.id.dialog_item_color_box);
                if (DeviceInfoHelper.isPosJellyBean()) {
                    itemColorBox.setBackground(gradient);
                } else {
                    //noinspection deprecation
                    itemColorBox.setBackgroundDrawable(gradient);
                }

                convertView.findViewById(R.id.dialog_item_color_box).setVisibility(View.VISIBLE);
                // Set title
                ((TextView) convertView.findViewById(R.id.dialog_item_title)).setText(option.getLabel());

                setProductsCount(convertView.findViewById(R.id.dialog_products_count), option);

                // Set check box
                setCheckboxBehavior(convertView.findViewById(R.id.dialog_item_checkbox), option);
            }
            // Return the filter view
            return convertView;
        }
    }
}