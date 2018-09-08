package com.bamilo.android.appmodule.bamiloapp.view.fragments;

import android.content.ContentValues;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.SwitchCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.constants.ConstantsIntentExtra;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentController;
import com.bamilo.android.appmodule.bamiloapp.controllers.fragments.FragmentType;
import com.bamilo.android.appmodule.bamiloapp.managers.TrackerManager;
import com.bamilo.android.appmodule.bamiloapp.models.BaseScreenModel;
import com.bamilo.android.appmodule.bamiloapp.utils.MyMenuItem;
import com.bamilo.android.appmodule.bamiloapp.utils.NavigationAction;
import com.bamilo.android.appmodule.bamiloapp.utils.catalog.CatalogSort;
import com.bamilo.android.appmodule.bamiloapp.utils.catalog.UICatalogUtils;
import com.bamilo.android.appmodule.bamiloapp.utils.catalog.filters.FilterCheckFragment;
import com.bamilo.android.appmodule.bamiloapp.utils.catalog.filters.FilterColorFragment;
import com.bamilo.android.appmodule.bamiloapp.utils.catalog.filters.FilterFragment;
import com.bamilo.android.appmodule.bamiloapp.utils.catalog.filters.FilterPriceFragment;
import com.bamilo.android.appmodule.bamiloapp.utils.catalog.filters.FilterRatingFragment;
import com.bamilo.android.framework.service.objects.catalog.filters.CatalogCheckFilter;
import com.bamilo.android.framework.service.objects.catalog.filters.CatalogColorFilterOption;
import com.bamilo.android.framework.service.objects.catalog.filters.CatalogFilter;
import com.bamilo.android.framework.service.objects.catalog.filters.CatalogFilters;
import com.bamilo.android.framework.service.objects.catalog.filters.CatalogPriceFilter;
import com.bamilo.android.framework.service.objects.catalog.filters.CatalogRatingFilter;
import com.bamilo.android.framework.service.objects.catalog.filters.FilterOptionInterface;
import com.bamilo.android.framework.service.objects.catalog.filters.FilterSelectionController;
import com.bamilo.android.framework.service.objects.catalog.filters.SubCategory;
import com.bamilo.android.framework.service.objects.category.Categories;
import com.bamilo.android.framework.service.pojo.IntConstants;
import com.bamilo.android.framework.service.pojo.RestConstants;
import com.bamilo.android.framework.service.tracking.TrackingPage;
import com.bamilo.android.framework.service.utils.DeviceInfoHelper;
import com.bamilo.android.framework.service.utils.output.Print;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved <p> Unauthorized copying of this
 * file, via any medium is strictly prohibited Proprietary and confidential.
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/09/07
 */
public class FilterMainFragment extends BaseFragment implements View.OnClickListener {

    private static final String TAG = FilterMainFragment.class.getSimpleName();

    private FilterSelectionController filterSelectionController;

    private SwitchCompat mDiscountBox;

    private ArrayList<CatalogFilter> mFilters;

    private ListView filtersKey;

    private int currentFilterPosition;

    private FilterFragment currentFragment;

    private boolean toCancelFilters;

    //public final static String FILTER_CATEGORY = "catalog_category";

    public final static String FILTER_TAG = "catalog_filters";

    public final static String FILTER_POSITION_TAG = "filters_position";

    public final static String INITIAL_FILTER_VALUES = "initial_filter_values";

    private TextView mTxFilterTitle;
    private Categories mCategories;

    private ContentValues mQueryValues = new ContentValues();

    private String mTitle;

    private String mKey;

    private CatalogSort mSelectedSort;

    private ContentValues mInitialSelectedFilterValues;
    private FragmentType mTargetType;
    private Bundle argumentsBundle;

    private ArrayList<SubCategory> mSubCategories;


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
        argumentsBundle = getArguments();
        // Saved state
        if (savedInstanceState != null) {
            mFilters = savedInstanceState.getParcelableArrayList(FILTER_TAG);
            currentFilterPosition = savedInstanceState.getInt(FILTER_POSITION_TAG);
            Parcelable[] filterOptions = savedInstanceState
                    .getParcelableArray(INITIAL_FILTER_VALUES);
            filterSelectionController = filterOptions instanceof FilterOptionInterface[]
                    ? new FilterSelectionController(mFilters,
                    (FilterOptionInterface[]) filterOptions)
                    : new FilterSelectionController(mFilters);
            mInitialSelectedFilterValues = filterSelectionController.getValues();
            mTargetType = (FragmentType) savedInstanceState
                    .getSerializable(ConstantsIntentExtra.TARGET_TYPE);
            mSubCategories = savedInstanceState
                    .getParcelableArrayList(RestConstants.SUB_CATEGORIES);
        }
        // Received arguments
        else {
            mTargetType = (FragmentType) argumentsBundle
                    .getSerializable(ConstantsIntentExtra.TARGET_TYPE);
            try {
                mFilters = new CatalogFilters(
                        new JSONObject(argumentsBundle.getString(FILTER_TAG)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            currentFilterPosition = IntConstants.DEFAULT_POSITION;
            filterSelectionController = new FilterSelectionController(mFilters);
            mInitialSelectedFilterValues = filterSelectionController.getValues();
            mKey = argumentsBundle.getString(ConstantsIntentExtra.CONTENT_ID);
            // Get title
            mTitle = argumentsBundle.getString(ConstantsIntentExtra.CONTENT_TITLE);

            UICatalogUtils.saveSearchQuery(argumentsBundle, mQueryValues);
            // Verify if catalog page was open via navigation drawer
            String categoryTree = argumentsBundle
                    .getString(ConstantsIntentExtra.CATEGORY_TREE_NAME);
            //Get category content/main category
            String mainCategory = argumentsBundle.getString(RestConstants.MAIN_CATEGORY);
            mSelectedSort = CatalogSort.values()[argumentsBundle
                    .getInt(ConstantsIntentExtra.CATALOG_SORT)];
            mSubCategories = argumentsBundle.getParcelableArrayList(RestConstants.SUB_CATEGORIES);
        }
        //
        toCancelFilters = true;
        // Track screen
        BaseScreenModel screenModel = new BaseScreenModel(
                getString(TrackingPage.FILTER_VIEW.getName()), getString(R.string.gaScreen), "",
                getLoadTime());
        TrackerManager.trackScreen(getContext(), screenModel, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Print.i(TAG, "ON VIEW CREATED");
        filtersKey = view.findViewById(R.id.filters_key);
        mTxFilterTitle = view.findViewById(R.id.filter_title);
        mDiscountBox = view.findViewById(R.id.dialog_filter_check_discount);
        filtersKey.setAdapter(new FiltersArrayAdapter(this.getActivity(), mFilters));
        filtersKey.setSelection(currentFilterPosition);
        loadFilterFragment(currentFilterPosition);
        filtersKey.setOnItemClickListener(
                (parent, view1, position, id) -> onFiltersKeyItemClick(position));

        int y = 0;
        for (CatalogFilter x : mFilters) {
            if (x instanceof CatalogPriceFilter) {
                break;
            }
            y++;
        }

        if (((CatalogPriceFilter) mFilters.get(y)).getOption().getCheckBoxOption() != null) {
            mDiscountBox.setVisibility(View.VISIBLE);
            /* mDiscountBox.setText(((CatalogPriceFilter)mFilters.get(y)).getOption().getCheckBoxOption().getLabel());*/
            mDiscountBox.setChecked(
                    ((CatalogPriceFilter) mFilters.get(y)).getOption().getCheckBoxOption()
                            .isSelected());
            final int finalY = y;
            mDiscountBox.setOnCheckedChangeListener(
                    (buttonView, isChecked) -> ((CatalogPriceFilter) mFilters.get(finalY))
                            .getOption().getCheckBoxOption()
                            .setSelected(isChecked));
        }

        view.findViewById(R.id.dialog_filter_button_cancel).setOnClickListener(this);
        view.findViewById(R.id.dialog_filter_button_done).setOnClickListener(this);

        initSubCatFilter(view);
    }

    private void initSubCatFilter(View view) {
        View subCategoryLayout = view.findViewById(R.id.subcateories_layout);
        view.findViewById(R.id.subcateories_text).setOnClickListener(this);

        if (mSubCategories != null && mSubCategories.size() > 0) {
            subCategoryLayout.setVisibility(View.VISIBLE);
            for (SubCategory subCategory : mSubCategories) {
                subCategory.setSelected(false);
            }
        } else {
            subCategoryLayout.setVisibility(View.GONE);
        }
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        Print.i(TAG, "ON SAVE INSTANCE");
        outState.putParcelableArray(INITIAL_FILTER_VALUES,
                filterSelectionController.getInitialValues());
        outState.putParcelableArrayList(FILTER_TAG, filterSelectionController.getCatalogFilters());
        outState.putSerializable(ConstantsIntentExtra.TARGET_TYPE, mTargetType);
        outState.putInt(FILTER_POSITION_TAG, currentFilterPosition);
        super.onSaveInstanceState(outState);
    }

    private void onFiltersKeyItemClick(int position) {
        if (currentFilterPosition != position) {
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
            fragmentChildManagerTransition(R.id.dialog_filter_container, currentFragment, true,
                    true);
        }
    }

    public void fragmentChildManagerTransition(int container, Fragment fragment,
            final boolean animated, boolean addToBackStack) {
        final FragmentTransaction fragmentTransaction = getChildFragmentManager()
                .beginTransaction();

        /*
          FIXME: Excluded piece of code due to crash on API = 18.
          Temporary fix - https://code.google.com/p/android/issues/detail?id=185457
         */
        DeviceInfoHelper.executeCodeExcludingJellyBeanMr2Version(() -> {
            // Animations
            if (animated) {
                fragmentTransaction
                        .setCustomAnimations(R.anim.slide_from_right, R.anim.slide_to_left,
                                R.anim.slide_from_left, R.anim.slide_to_right);
            }
        });

        // Replace
        fragmentTransaction.replace(container, fragment);
        // Back stack
        if (addToBackStack) {
            fragmentTransaction.addToBackStack(null);
        }
        // Commit
        fragmentTransaction.commitAllowingStateLoss();
    }

    @Override
    public void onClick(View view) {
        // Get view id
        int id = view.getId();
        // Cancel
        if (id == R.id.dialog_filter_button_cancel) {
            processOnClickClean();
        }
        // Save
        else if (id == R.id.dialog_filter_button_done) {
            processOnClickDone();
        } else if (id == R.id.subcateories_text) {
            processOnSubCategoryClick();
        }
    }

    private void processOnSubCategoryClick() {
        Bundle bundle = new Bundle();
        bundle.putParcelableArrayList(RestConstants.SUB_CATEGORIES, mSubCategories);
        getBaseActivity().onSwitchFragment(FragmentType.Sub_CATEGORY_FILTERS, bundle,
                false);
    }

    /**
     * Process the click on clean button
     *
     * @author sergiopereira
     */
    private void processOnClickClean() {
        Print.d(TAG, "CLICKED ON: CLEAR");
        if (currentFragment instanceof FilterPriceFragment) {
            ((FilterPriceFragment) currentFragment).clearFilterValues();
        }
        filterSelectionController.initAllInitialFilterValues();
        // Clean all saved values
        filterSelectionController.cleanAllFilters();
        // Reset discount box
        if (mDiscountBox.isChecked()) {
            mDiscountBox.setChecked(false);
        }
        // Update adapter
        ((BaseAdapter) filtersKey.getAdapter()).notifyDataSetChanged();
        if (currentFragment != null) {
            currentFragment.cleanValues();
        }
    }

    /**
     * Process the click on save button. Create a content values and send it to parent
     *
     * @author sergiopereira
     */
    private void processOnClickDone() {
        Print.d(TAG, "CLICKED ON: DONE");
        // Get parent unique tag
        String parentCatalogBackStackTag = FragmentController.getParentBackStackTag(this);

        // in case selected filters isn't changed
        if (mInitialSelectedFilterValues != null && mInitialSelectedFilterValues
                .equals(filterSelectionController.getValues())) {
            getBaseActivity().onBackPressed();
            return;
        }

        // Communicate with parent
        toCancelFilters = false;
        // argumentsBundle.putString(FILTER_CATEGORY, "women_tops_tshirts");
        if (argumentsBundle == null) {
            argumentsBundle = new Bundle();
        }
        argumentsBundle.putString(ConstantsIntentExtra.CONTENT_TITLE, mTitle);
        argumentsBundle.putString(ConstantsIntentExtra.CONTENT_ID, mKey);
        argumentsBundle.putSerializable(ConstantsIntentExtra.TRACKING_ORIGIN_TYPE, mGroupType);
        argumentsBundle.putInt(ConstantsIntentExtra.CATALOG_SORT,
                mSelectedSort != null ? mSelectedSort.ordinal() : CatalogSort.POPULARITY.ordinal());

        argumentsBundle.putParcelable(FILTER_TAG, filterSelectionController.getValues());
        getBaseActivity().onSwitchFragment(mTargetType, argumentsBundle,
                FragmentController.ADD_TO_BACK_STACK);
    }


    private class FiltersArrayAdapter extends ArrayAdapter<CatalogFilter> {

        FiltersArrayAdapter(Context context, List<CatalogFilter> objects) {
            super(context, R.layout.list_sub_item_1, objects);
        }

        @NonNull
        @Override
        public View getView(int position, View convertView, @NonNull ViewGroup parent) {
            // Get Filter
            CatalogFilter filter = getItem(position);
            // Validate current view
            if (convertView == null) {
                convertView = LayoutInflater.from(getContext())
                        .inflate(R.layout.list_sub_item_1, null);
            }

            TextView filterTitleTextView = convertView
                    .findViewById(R.id.dialog_item_title);
            TextView filtersNumberTextView = convertView
                    .findViewById(R.id.dialog_item_count);

            if (filter.hasAppliedFilters()) {
                //filterTitleTextView.setTypeface(null, Typeface.BOLD);
                if (!(filter instanceof CatalogPriceFilter)) {
                    filtersNumberTextView.setText(convertView.getResources()
                            .getString(R.string.parenthesis_placeholder,
                                    ((CatalogCheckFilter) filter).getSelectedFilterOptions()
                                            .size()));
                    filtersNumberTextView.setVisibility(View.VISIBLE);
                }
            } else {
                filtersNumberTextView.setVisibility(View.GONE);
                // filterTitleTextView.setTypeface(null, Typeface.NORMAL);
            }
            // Set title
            filterTitleTextView.setText(filter.getName());

            if (position == FilterMainFragment.this.currentFilterPosition) {
                convertView.setBackgroundColor(
                        ContextCompat.getColor(getContext(), R.color.black_400));

            } else {
                convertView.setBackgroundColor(Color.WHITE);
            }

            return convertView;
        }
    }

    @Override
    public boolean allowBackPressed() {
        if (toCancelFilters) {
            filterSelectionController.goToInitialValues();
        }
        return super.allowBackPressed();
    }
}