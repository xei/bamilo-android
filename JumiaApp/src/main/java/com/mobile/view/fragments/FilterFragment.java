package com.mobile.view.fragments;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.controllers.FilterOptionArrayAdapter;
import com.mobile.newFramework.objects.catalog.filters.CatalogCheckFilter;
import com.mobile.newFramework.objects.catalog.filters.CatalogFilter;
import com.mobile.newFramework.objects.catalog.filters.FilterSelectionController;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Created by rsoares on 9/7/15.
 */
public class FilterFragment extends BaseFragment{

    private FilterSelectionController filterSelectionController;

    private ArrayList<CatalogFilter> mFilters;

    private ListView filtersKey;

    private ListView filterValues;

    public final static String FILTER_TAG = "catalog_filters";

    public FilterFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK),
                NavigationAction.Filters,
                R.layout.filters_main,
                NO_TITLE,
                KeyboardState.NO_ADJUST_CONTENT);
    }

    public static FilterFragment getInstance(Bundle bundle) {
        FilterFragment filterFragment = new FilterFragment();
        filterFragment.setArguments(bundle);
        return filterFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mFilters = bundle.getParcelableArrayList(FILTER_TAG);
        filterSelectionController = new FilterSelectionController(mFilters);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        filtersKey = (ListView)view.findViewById(R.id.filters_key);
        filterValues = (ListView)view.findViewById(R.id.filter_values);

        filtersKey.setAdapter(new FiltersArrayAdapter(this.getActivity(), mFilters));
        filtersKey.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onFiltersKeyItemClick(position);
            }
        });
//        filter_values.setAdapter(adapter);
    }

    private void onFiltersKeyItemClick(int position) {
        final CatalogFilter catalogFilter = mFilters.get(position);
        filterValues.setVisibility(View.VISIBLE);

        if (catalogFilter instanceof CatalogCheckFilter) {
            FilterOptionArrayAdapter filterOptionArrayAdapter = new FilterOptionArrayAdapter(this.getActivity(), ((CatalogCheckFilter) catalogFilter));
            filterValues.setAdapter(filterOptionArrayAdapter);
            filterValues.setOnItemClickListener(filterOptionArrayAdapter);
        }
    }

    public static class FiltersArrayAdapter extends ArrayAdapter<CatalogFilter> {

        private static int layout = R.layout.dialog_list_sub_item_1;

        /**
         * Constructor
         * @param context
         * @param objects
         */
        public FiltersArrayAdapter(Context context, List<CatalogFilter> objects) {
            super(context, layout, objects);
        }

        /*
         * (non-Javadoc)
         * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get Filter
            CatalogFilter filter = getItem(position);
            // Validate current view
            if (convertView == null) convertView = LayoutInflater.from(getContext()).inflate(layout, null);
            // Set title
            ((TextView) convertView.findViewById(R.id.dialog_item_title)).setText(filter.getName());
            // Set sub title
//            if (!filter.hasOptionSelected() && !filter.hasRangeValues() && !filter.isRangeWithDiscount())
//                ((TextView) convertView.findViewById(R.id.dialog_item_subtitle)).setText(R.string.all_label);
//            else if(filter.hasRangeValues() && !filter.isRangeWithDiscount())
//                ((TextView) convertView.findViewById(R.id.dialog_item_subtitle)).setText(filter.getMinRangeValue() + " - " + filter.getMaxRangeValue());
//            else if(filter.hasRangeValues() && filter.isRangeWithDiscount())
//                ((TextView) convertView.findViewById(R.id.dialog_item_subtitle)).setText(filter.getMinRangeValue() + " - " + filter.getMaxRangeValue() + " " + getContext().getString(R.string.string_with_discount_only));
//            else if(!filter.hasRangeValues() && filter.isRangeWithDiscount())
//                ((TextView) convertView.findViewById(R.id.dialog_item_subtitle)).setText(R.string.string_with_discount_only);
//            else if (filter.hasOptionSelected())
//                ((TextView) convertView.findViewById(R.id.dialog_item_subtitle)).setText(getOptionsToString(filter.getSelectedOption()));
//            ((TextView) convertView.findViewById(R.id.dialog_item_subtitle)).setText("O que me apetecer");
            // Return the filter view
            return convertView;
        }

        /**
         * Create a string
         * @param array
         * @return String
         */
//        private String getOptionsToString(SparseArray<CatalogFilterOption> array){
//            String string = "";
//            for (int i = 0; i < array.size(); i++) string += ((i == 0) ? "" : ", ") + array.valueAt(i).getLabel();
//            return string;
//        }
    }

}
