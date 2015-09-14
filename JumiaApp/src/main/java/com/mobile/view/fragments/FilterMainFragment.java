package com.mobile.view.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.interfaces.OnDialogFilterListener;
import com.mobile.newFramework.objects.catalog.filters.CatalogCheckFilter;
import com.mobile.newFramework.objects.catalog.filters.CatalogColorFilterOption;
import com.mobile.newFramework.objects.catalog.filters.CatalogFilter;
import com.mobile.newFramework.objects.catalog.filters.CatalogPriceFilter;
import com.mobile.newFramework.objects.catalog.filters.CatalogRatingFilter;
import com.mobile.newFramework.objects.catalog.filters.FilterSelectionController;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.utils.MyMenuItem;
import com.mobile.utils.NavigationAction;
import com.mobile.utils.catalog.filters.FilterCheckFragment;
import com.mobile.utils.catalog.filters.FilterColorFragment;
import com.mobile.utils.catalog.filters.FilterFragment;
import com.mobile.utils.catalog.filters.FilterPriceFragment;
import com.mobile.utils.catalog.filters.FilterRatingFragment;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

/**
 * Created by rsoares on 9/7/15.
 */
public class FilterMainFragment extends BaseFragment implements View.OnClickListener{

    private FilterSelectionController filterSelectionController;

    private ArrayList<CatalogFilter> mFilters;

    private ListView filtersKey;

    private int currentFilterPosition;

    private OnDialogFilterListener filterListener;

    private FilterFragment currentFragment;

    private boolean toCancelFilters;

    public final static String FILTER_TAG = "catalog_filters";

    public final static String FILTER_LISTENER = "catalog_filter_listener";

    public FilterMainFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK),
                NavigationAction.Filters,
                R.layout.filters_main,
                NO_TITLE,
                KeyboardState.NO_ADJUST_CONTENT);
    }

    public static FilterMainFragment getInstance(Bundle bundle) {
        FilterMainFragment filterFragment = new FilterMainFragment();
        filterFragment.setArguments(bundle);
        return filterFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        mFilters = bundle.getParcelableArrayList(FILTER_TAG);
        filterListener = (OnDialogFilterListener)bundle.getSerializable(FILTER_LISTENER);
        filterSelectionController = new FilterSelectionController(mFilters);
        currentFilterPosition = -1;
        toCancelFilters = true;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        filtersKey = (ListView)view.findViewById(R.id.filters_key);

        filtersKey.setAdapter(new FiltersArrayAdapter(this.getActivity(), mFilters));
        filtersKey.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onFiltersKeyItemClick(position);
            }
        });

        view.findViewById(R.id.dialog_filter_button_cancel).setOnClickListener(this);
        view.findViewById(R.id.dialog_filter_button_done).setOnClickListener(this);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(toCancelFilters){
            filterSelectionController.goToInitialValues();
        }
    }

    private void onFiltersKeyItemClick(int position) {

        if(currentFilterPosition != position) {
            final CatalogFilter catalogFilter = mFilters.get(position);
            Bundle bundle = new Bundle();
            bundle.putParcelable(FILTER_TAG, catalogFilter);

            currentFragment = null;

            if (catalogFilter instanceof CatalogRatingFilter) {
                currentFragment = FilterRatingFragment.newInstance(bundle);
            } else if (catalogFilter instanceof CatalogCheckFilter) {

                if (catalogFilter.getOptionType() == CatalogColorFilterOption.class) {
                    currentFragment = FilterColorFragment.newInstance(bundle);
                } else {
                    currentFragment = FilterCheckFragment.newInstance(bundle);
                }

            } else if (catalogFilter instanceof CatalogPriceFilter) {
                currentFragment = FilterPriceFragment.newInstance(bundle);
            }

            if (currentFragment != null) {
                currentFilterPosition = position;
                filterSelectionController.addToInitialValues(position);
                ((BaseAdapter) filtersKey.getAdapter()).notifyDataSetChanged();
                fragmentChildManagerTransition(R.id.dialog_filter_container, currentFragment, true, true);
            }
        }
    }

    public void fragmentChildManagerTransition(int container, Fragment fragment, boolean animated, boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        // Animations
        if (animated)
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left, R.anim.slide_in_left, R.anim.slide_out_right);
        // Replace
        fragmentTransaction.replace(container, fragment);
        // Back stack
        if (addToBackStack)
            fragmentTransaction.addToBackStack(null);
        // Commit
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onClick(View view) {
        // Get view id
        int id = view.getId();

        if (id == R.id.dialog_filter_button_cancel){
            processOnClickClean();
        }
        // Save
        else if (id == R.id.dialog_filter_button_done){
            processOnClickDone();
        }
    }

    /**
     * Process the click on clean button
     * @author sergiopereira
     */
    private void processOnClickClean(){
        Print.d(TAG, "CLICKED ON: CLEAR");

        filterSelectionController.initAllInitialFilterValues();

        // Clean all saved values
        filterSelectionController.cleanAllFilters();
        // Update adapter
        ((BaseAdapter) filtersKey.getAdapter()).notifyDataSetChanged();
        if(currentFragment != null) {
            currentFragment.cleanValues();
        }
    }

    /**
     * Process the click on save button.
     * Create a content values and send it to parent
     * @author sergiopereira
     */
    private void processOnClickDone() {
        Print.d(TAG, "CLICKED ON: DONE");
        toCancelFilters = false;

        // Validate and send to catalog fragment
        if(filterListener != null) {
            filterListener.onSubmitFilterValues(filterSelectionController.getValues());
        }

        getBaseActivity().onBackPressed();

    }

    private class FiltersArrayAdapter extends ArrayAdapter<CatalogFilter> {

        private static final int layout = R.layout.list_sub_item_1;

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

            TextView filterTitleTextView = ((TextView) convertView.findViewById(R.id.dialog_item_title));
            TextView filtersNumberTextView = ((TextView) convertView.findViewById(R.id.dialog_item_count));

            if(filter.hasAppliedFilters()) {
                filterTitleTextView.setTypeface(null, Typeface.BOLD);
                if(!(filter instanceof CatalogPriceFilter)){
                    filtersNumberTextView.setText(convertView.getResources().getString(R.string.filter_placeholder, ((CatalogCheckFilter)filter).getSelectedFilterOptions().size()));
                    filtersNumberTextView.setVisibility(View.VISIBLE);
                }
            } else {
                filtersNumberTextView.setVisibility(View.GONE);
                filterTitleTextView.setTypeface(null, Typeface.NORMAL);
            }
            // Set title
            filterTitleTextView.setText(filter.getName());

            if(position == FilterMainFragment.this.currentFilterPosition) {
                convertView.setBackgroundColor(Color.WHITE);
            } else {
                convertView.setBackgroundColor(convertView.getResources().getColor(R.color.black_300));
            }

            return convertView;
        }
    }

}
