package com.mobile.utils.dialogfragments;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.mobile.components.customfontviews.CheckBox;
import com.mobile.components.customfontviews.TextView;
import com.mobile.controllers.FilterOptionArrayAdapter;
import com.mobile.newFramework.objects.catalog.filters.CatalogCheckFilter;
import com.mobile.newFramework.objects.catalog.filters.CatalogColorFilterOption;
import com.mobile.newFramework.objects.catalog.filters.MultiFilterOptionService;
import com.mobile.newFramework.utils.output.Print;
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
            
        private static int layout = R.layout.dialog_list_sub_item_2;

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
            MultiFilterOptionService option = getItem(position);
            if(option instanceof CatalogColorFilterOption){
                // Validate current view
                if (convertView == null) convertView = LayoutInflater.from(getContext()).inflate(layout, null);
                // Set color box
                convertView.findViewById(R.id.dialog_item_color_box).setBackgroundColor(Color.parseColor(((CatalogColorFilterOption) option).getHexValue()));
                convertView.findViewById(R.id.dialog_item_color_box).setVisibility(View.VISIBLE);
                // Set title
                ((TextView) convertView.findViewById(R.id.dialog_item_title)).setText(option.getLabel());
                // Set check box
                ((CheckBox) convertView.findViewById(R.id.dialog_item_checkbox)).setChecked(option.isSelected());
            }

            // Return the filter view
            return convertView;
        }
    }
    
    
}