package com.mobile.view.fragments;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Toast;

import com.mobile.components.customfontviews.CheckBox;
import com.mobile.components.customfontviews.TextView;
import com.mobile.controllers.fragments.FragmentController;
import com.mobile.newFramework.objects.catalog.filters.CatalogCheckFilter;
import com.mobile.newFramework.objects.catalog.filters.CatalogColorFilterOption;
import com.mobile.newFramework.objects.catalog.filters.CatalogFilter;
import com.mobile.newFramework.objects.catalog.filters.CatalogPriceFilter;
import com.mobile.newFramework.objects.catalog.filters.CatalogRatingFilter;
import com.mobile.newFramework.objects.catalog.filters.FilterOptionInterface;
import com.mobile.newFramework.objects.catalog.filters.FilterSelectionController;
import com.mobile.newFramework.pojo.IntConstants;
import com.mobile.newFramework.utils.DeviceInfoHelper;
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
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/09/07
 *
 *
 */
public class FilterMainFragment extends BaseFragment implements View.OnClickListener{

    private static final String TAG = FilterMainFragment.class.getSimpleName();

    private FilterSelectionController filterSelectionController;

    private SwitchCompat mDiscountBox;

    private ArrayList<CatalogFilter> mFilters;

    private ListView filtersKey;

    private int currentFilterPosition;

    private FilterFragment currentFragment;

    private boolean toCancelFilters;

    public final static String FILTER_TAG = "catalog_filters";

    public final static String FILTER_POSITION_TAG = "filters_position";

    public final static String INITIAL_FILTER_VALUES = "initial_filter_values";

    private  TextView mTxFilterTitle;

    /**
     * Empty constructor
     */
    public FilterMainFragment() {
        super(EnumSet.of(MyMenuItem.UP_BUTTON_BACK),
                NavigationAction.FILTERS,
                R.layout.filters_main,
                R.string.filters_label,
                NO_ADJUST_CONTENT);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Print.i(TAG, "ON CREATE");
        Bundle bundle = getArguments();
        // Saved state
        if (savedInstanceState != null) {
            mFilters = savedInstanceState.getParcelableArrayList(FILTER_TAG);
            currentFilterPosition = savedInstanceState.getInt(FILTER_POSITION_TAG);
            Parcelable[] filterOptions = savedInstanceState.getParcelableArray(INITIAL_FILTER_VALUES);
            filterSelectionController = filterOptions instanceof FilterOptionInterface[] ? new FilterSelectionController(mFilters, (FilterOptionInterface[]) filterOptions) : new FilterSelectionController(mFilters);
        }
        // Received arguments
        else {
            mFilters = bundle.getParcelableArrayList(FILTER_TAG);
            currentFilterPosition = IntConstants.DEFAULT_POSITION;
            filterSelectionController = new FilterSelectionController(mFilters);
        }
        //
        toCancelFilters = true;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        filtersKey = (ListView)view.findViewById(R.id.filters_key);
        mTxFilterTitle = (TextView) view.findViewById(R.id.filter_title);
        mDiscountBox = (SwitchCompat) view.findViewById(R.id.dialog_filter_check_discount);
        filtersKey.setAdapter(new FiltersArrayAdapter(this.getActivity(), mFilters));
        filtersKey.setSelection(currentFilterPosition);
        loadFilterFragment(currentFilterPosition);
        filtersKey.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onFiltersKeyItemClick(position);
            }
        });
        int y=0;
        for (CatalogFilter x :mFilters)
        {
            if (x instanceof  CatalogPriceFilter)
            {
                break;
            }
            y++;
        }


      if(((CatalogPriceFilter)mFilters.get(y)).getOption().getCheckBoxOption() != null){
            mDiscountBox.setVisibility(View.VISIBLE);
           /* mDiscountBox.setText(((CatalogPriceFilter)mFilters.get(y)).getOption().getCheckBoxOption().getLabel());*/
            mDiscountBox.setChecked(((CatalogPriceFilter)mFilters.get(y)).getOption().getCheckBoxOption().isSelected());
          final int finalY = y;
          mDiscountBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    ((CatalogPriceFilter)mFilters.get(finalY)).getOption().getCheckBoxOption().setSelected(isChecked);
                }
            });
        }

        view.findViewById(R.id.dialog_filter_button_cancel).setOnClickListener(this);
        view.findViewById(R.id.dialog_filter_button_done).setOnClickListener(this);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        Print.i(TAG, "ON SAVE INSTANCE");
        outState.putParcelableArray(INITIAL_FILTER_VALUES, filterSelectionController.getInitialValues());
        outState.putParcelableArrayList(FILTER_TAG, filterSelectionController.getCatalogFilters());
        outState.putInt(FILTER_POSITION_TAG, currentFilterPosition);
        super.onSaveInstanceState(outState);
    }

    private void onFiltersKeyItemClick(int position) {
        if(currentFilterPosition != position) {
            loadFilterFragment(position);
        }
    }

    private void loadFilterFragment(int position) {
        final CatalogFilter catalogFilter = mFilters.get(position);
        Bundle bundle = new Bundle();
        bundle.putParcelable(FILTER_TAG, catalogFilter);

        currentFragment = null;
        // Case rating
        if (catalogFilter instanceof CatalogRatingFilter) {
            currentFragment = FilterRatingFragment.newInstance(bundle);
        }
        // Case generic check box filter
        else if (catalogFilter instanceof CatalogCheckFilter) {
            if (catalogFilter.getOptionType() == CatalogColorFilterOption.class) {
                currentFragment = FilterColorFragment.newInstance(bundle);
            } else {
                currentFragment = FilterCheckFragment.newInstance(bundle);
            }
        }
        // Case price filter
        else if (catalogFilter instanceof CatalogPriceFilter) {
            currentFragment = FilterPriceFragment.newInstance(bundle);
        }

        if (currentFragment != null) {
            currentFilterPosition = position;
            filterSelectionController.addToInitialValues(position);
            mTxFilterTitle.setText(catalogFilter.getName());
            ((BaseAdapter) filtersKey.getAdapter()).notifyDataSetChanged();
            fragmentChildManagerTransition(R.id.dialog_filter_container, currentFragment, true, true);
        }
    }

    public void fragmentChildManagerTransition(int container, Fragment fragment, final boolean animated, boolean addToBackStack) {
        final FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();

        /**
         * FIXME: Excluded piece of code due to crash on API = 18.
         * Temporary fix - https://code.google.com/p/android/issues/detail?id=185457
         */
        DeviceInfoHelper.executeCodeExcludingJellyBeanMr2Version(new Runnable() {
            @Override
            public void run() {
                // Animations
                if (animated)
                    fragmentTransaction.setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left, R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

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
        // Cancel
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
        // Get parent unique tag
        String parentCatalogBackStackTag = FragmentController.getParentBackStackTag(this);
        // Communicate with parent
        toCancelFilters = false;
        Bundle bundle = new Bundle();
        bundle.putParcelable(FILTER_TAG, filterSelectionController.getValues());
        getBaseActivity().communicateBetweenFragments(parentCatalogBackStackTag, bundle);
        getBaseActivity().onBackPressed();
    }

    private class FiltersArrayAdapter extends ArrayAdapter<CatalogFilter> {

        /**
         * Constructor
         */
        public FiltersArrayAdapter(Context context, List<CatalogFilter> objects) {
            super(context, R.layout.list_sub_item_1, objects);
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
            if (convertView == null) convertView = LayoutInflater.from(getContext()).inflate(R.layout.list_sub_item_1, null);

            TextView filterTitleTextView = ((TextView) convertView.findViewById(R.id.dialog_item_title));
            TextView filtersNumberTextView = ((TextView) convertView.findViewById(R.id.dialog_item_count));

            if(filter.hasAppliedFilters()) {
                //filterTitleTextView.setTypeface(null, Typeface.BOLD);
                if(!(filter instanceof CatalogPriceFilter)){
                    filtersNumberTextView.setText(convertView.getResources().getString(R.string.parenthesis_placeholder, ((CatalogCheckFilter)filter).getSelectedFilterOptions().size()));
                    filtersNumberTextView.setVisibility(View.VISIBLE);
                }
            } else {
                filtersNumberTextView.setVisibility(View.GONE);
               // filterTitleTextView.setTypeface(null, Typeface.NORMAL);
            }
            // Set title
            filterTitleTextView.setText(filter.getName());

            if(position == FilterMainFragment.this.currentFilterPosition) {
                convertView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.black_400));

            } else {
                convertView.setBackgroundColor(Color.WHITE);
            }

            return convertView;
        }
    }

    @Override
    public boolean allowBackPressed() {
        if(toCancelFilters){
            filterSelectionController.goToInitialValues();
        }
        return super.allowBackPressed();
    }
}
