package com.mobile.utils.catalog.filters;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mobile.components.customfontviews.CheckBox;
import com.mobile.components.customfontviews.TextView;
import com.mobile.controllers.FilterOptionArrayAdapter;
import com.mobile.service.objects.catalog.filters.CatalogCheckFilter;
import com.mobile.service.objects.catalog.filters.CatalogColorFilterOption;
import com.mobile.service.objects.catalog.filters.MultiFilterOptionInterface;
import com.mobile.service.utils.DeviceInfoHelper;
import com.mobile.service.utils.output.Print;
import com.mobile.view.R;

/**
 * 
 * @author sergiopereira
 *
 */
public class FilterColorFragment extends FilterCheckFragment {
    
    private static final String TAG = FilterColorFragment.class.getSimpleName();

    /**
     *
     * @param bundle
     * @return
     */
    public static FilterColorFragment newInstance(Bundle bundle) {
        Print.d(TAG, "NEW INSTANCE: BRAND");
        FilterColorFragment frag = new FilterColorFragment();
        frag.setArguments(bundle);
        return frag;
    }

    @Override
    protected FilterOptionArrayAdapter createAdapter() {
        return new FilterColorOptionArrayAdapter(getActivity(), mFilter);
    }

    /**
     * 
     * @author sergiopereira
     *
     */
     public static class FilterColorOptionArrayAdapter extends FilterOptionArrayAdapter {
            
        private static int layout = R.layout.list_sub_item_2;

        public FilterColorOptionArrayAdapter(Context context, CatalogCheckFilter catalogCheckFilter) {
            super(context, catalogCheckFilter);
        }

        /*
         * (non-Javadoc)
         * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get Filter
            MultiFilterOptionInterface option = getItem(position);
            if(option instanceof CatalogColorFilterOption){
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

                setProductsCount((TextView) convertView.findViewById(R.id.dialog_products_count), option);

                // Set check box
                setCheckboxBehavior(((CheckBox) convertView.findViewById(R.id.dialog_item_checkbox)), option);
            }

            // Return the filter view
            return convertView;
        }


    }
    
    
}