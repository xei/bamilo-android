package com.mobile.utils.dialogfragments;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.mobile.components.customfontviews.TextView;
import com.mobile.helpers.products.GetCatalogPageHelper;
import com.mobile.interfaces.OnDialogFilterListener;
import com.mobile.newFramework.objects.catalog.filters.CatalogFilter;
import com.mobile.newFramework.objects.catalog.filters.FilterSelectionController;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.view.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Class used to show filters for catalog
 * @author sergiopereira
 */
public class DialogFilterFragment extends DialogFragment {

    private final static String TAG = DialogFilterFragment.class.getSimpleName();

    public static final int NO_INITIAL_POSITION = -1;

    private static final int FILTER_CATEGORY_TYPE = 0;
    
    private static final int FILTER_BRAND_TYPE = 1;
    
    private static final int FILTER_SIZE_TYPE = 2;
    
    private static final int FILTER_PRICE_TYPE = 3;
    
    private static final int FILTER_COLOR_TYPE = 4;

    private static final int FILTER_MAIN_TYPE = 5;
    
    public static final String CATEGORY_ID = "category";
    
    public static final String BRAND_ID = "brand";
    
    public static final String SIZE_ID = "size";
    
    public static final String PRICE_ID = "price";
    
    public static final String COLOR_ID = "color";

    public final static String FILTER_TAG = "catalog_filters";
    
    public static final String URL = GetCatalogPageHelper.URL;
    
    public static final String BRAND = GetCatalogPageHelper.BRAND;

    private static ArrayList<CatalogFilter> mFilters;

    private OnDialogFilterListener mParentFrament;

    private FilterSelectionController filterSelectionController;

    private boolean toCancelFilters;

    /**
     * Empty constructor
     */
    public DialogFilterFragment() { }

    /**
     * Create new instance
     */
    public static DialogFilterFragment newInstance(Bundle bundle, OnDialogFilterListener mParentFragment) {
        Print.d(TAG, "NEW INSTANCE");
        DialogFilterFragment dialogListFragment = new DialogFilterFragment();
        dialogListFragment.setArguments(bundle);
        dialogListFragment.mParentFrament = mParentFragment;
        return dialogListFragment;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.DialogFragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(R.style.Theme_Jumia_Dialog_NoTitle, R.style.Theme_Jumia_Dialog_NoTitle);
        Bundle bundle = getArguments();
        mFilters = bundle.getParcelableArrayList(FILTER_TAG);
        filterSelectionController = new FilterSelectionController(mFilters);
        toCancelFilters = true;
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_filter_main, container, false);
    }

    /*
     * (non-Javadoc)
     * 
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Show the main fragment, list of supported filters
        onSwitchChildFragment(FILTER_MAIN_TYPE, null);
    }

    /**
     * Method used to switch between the filter fragments
     * @param filterType
     * @param bundle 
     * @author sergiopereira
     */
    private void onSwitchChildFragment(int filterType, Bundle bundle) {
        switch (filterType) {
        case FILTER_MAIN_TYPE:
            Print.i(TAG, "ON SWITCH FILTER: FILTER_MAIN");
            FilterMainFragment fragmentOne = FilterMainFragment.newInstance(this);
            fragmentChildManagerTransition(R.id.dialog_filter_container, fragmentOne, false, true);
            break;
        case FILTER_CATEGORY_TYPE:
            Print.i(TAG, "ON SWITCH FILTER: FILTER_CATEGORY");
            FilterCategoryFragment fragmentCategory = FilterCategoryFragment.newInstance(this, bundle);
            fragmentChildManagerTransition(R.id.dialog_filter_container, fragmentCategory, true, true);
            break;
        case FILTER_BRAND_TYPE:
            Print.i(TAG, "ON SWITCH FILTER: FILTER_BRAND");
            FilterBrandFragment fragmentBrand = FilterBrandFragment.newInstance(this, bundle);
            fragmentChildManagerTransition(R.id.dialog_filter_container, fragmentBrand, true, true);
            break;
        case FILTER_SIZE_TYPE:
            Print.i(TAG, "ON SWITCH FILTER: FILTER_SIZE");
            FilterSizeFragment fragmentSize = FilterSizeFragment.newInstance(this, bundle);
            fragmentChildManagerTransition(R.id.dialog_filter_container, fragmentSize, true, true);
            break;
        case FILTER_PRICE_TYPE:
            Print.i(TAG, "ON SWITCH FILTER: FILTER_PRICE");
            FilterPriceFragment fragmentPrice = FilterPriceFragment.newInstance(this, bundle);
            fragmentChildManagerTransition(R.id.dialog_filter_container, fragmentPrice, true, true);
            break;
        case FILTER_COLOR_TYPE:
            Print.i(TAG, "ON SWITCH FILTER: FILTER_COLOR");
            FilterColorFragment fragmentColor = FilterColorFragment.newInstance(this, bundle);
            fragmentChildManagerTransition(R.id.dialog_filter_container, fragmentColor, true, true);
            break;
        default:
            Print.w(TAG, "ON SWITCH FILTER: UNKNOWN TYPE");
            break;
        }
    }

    /**
     * Method used to associate the container and fragment
     * 
     * @param container
     * @param fragment
     * @param animated
     */
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

    /**
     * Process the back
     */
    public void allowBackPressed() {
        Print.d(TAG, "ALLOW BACK PRESSED");
        getChildFragmentManager().popBackStack();
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onPause()
     */
    @Override
    public void onPause() {
        super.onPause();
        dismissAllowingStateLoss();
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.DialogFragment#onDismiss(android.content.DialogInterface)
     */
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);
        if(toCancelFilters){
            filterSelectionController.goToInitialValues();
        }
    }
    
    /**
     * Method used to send the content values to parent
     * @param filterValues
     * @author sergiopereira
     */
    public void onSubmitFilterValues(ContentValues filterValues){
        if(mParentFrament != null) mParentFrament.onSubmitFilterValues(filterValues);
    }

    /*
     * ################# MAIN FRAGMENT ################
     */
    /**
     * Class used to create the main fragment that shows the list of supported filters
     * @author sergiopereira
     */
    public static class FilterMainFragment extends Fragment implements OnClickListener, OnItemClickListener {

        private DialogFilterFragment mParent;
        
        private ListView mFilterListView;

        private ContentValues mContentValues;

        /**
         * Constructor
         * @param parent
         * @return
         */
        public static FilterMainFragment newInstance(DialogFilterFragment parent) {
            Print.d(TAG, "NEW INSTANCE: MAIN");
            FilterMainFragment frag = new FilterMainFragment();
            frag.mParent = parent;
            return frag;
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
         * android.view.ViewGroup, android.os.Bundle)
         */
        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            return inflater.inflate(R.layout.dialog_filter_fragment_main, container, false);
        }

        /*
         * (non-Javadoc)
         * 
         * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
         */
        @Override
        public void onViewCreated(View view, Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            // Clean button
            view.findViewById(R.id.dialog_filter_header_clear).setOnClickListener(this);
            // Filter list
            mFilterListView = (ListView) view.findViewById(R.id.dialog_filter_list);
            mFilterListView.setOnItemClickListener(this);
            mFilterListView.setAdapter(new FiltersArrayAdapter(getActivity(), mFilters));
            // Button cancel
            view.findViewById(R.id.dialog_filter_button_cancel).setOnClickListener(this);
            // Button done
            view.findViewById(R.id.dialog_filter_button_done).setOnClickListener(this);
        }

        /*
         * (non-Javadoc)
         * 
         * @see
         * android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView,
         * android.view.View, int, long)
         */
        @Override
        public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
            Print.d(TAG, "ON ITEM CLICK: " + position);
            // Get selected filter
            CatalogFilter selectedFilter = mFilters.get(position);
            mParent.filterSelectionController.addToInitialValues(position);
            // Get the id
            String filterId = selectedFilter.getId();
            // Create bundle
            Bundle bundle = new Bundle();
            bundle.putParcelable(FILTER_TAG, selectedFilter);
            // Goto for respective fragment
            if(filterId.contains(CATEGORY_ID)){
                ((DialogFilterFragment) getParentFragment()).onSwitchChildFragment(FILTER_CATEGORY_TYPE, bundle);
            }
            else if(filterId.contains(BRAND_ID)){
                ((DialogFilterFragment) getParentFragment()).onSwitchChildFragment(FILTER_BRAND_TYPE, bundle);
            }
            else if(filterId.contains(SIZE_ID)){
                ((DialogFilterFragment) getParentFragment()).onSwitchChildFragment(FILTER_SIZE_TYPE, bundle);
            }
            else if(filterId.contains(PRICE_ID)){
                ((DialogFilterFragment) getParentFragment()).onSwitchChildFragment(FILTER_PRICE_TYPE, bundle);
            }
            else if(filterId.contains(COLOR_ID)){
                ((DialogFilterFragment) getParentFragment()).onSwitchChildFragment(FILTER_COLOR_TYPE, bundle);
            }
            else { Print.w(TAG, "UNKNOWN FILTER ID");}
        }


        /*
         * (non-Javadoc)
         *
         * @see android.view.View.OnClickListener#onClick(android.view.View)
         */
        @Override
        public void onClick(View view) {
            // Get view id
            int id = view.getId();
            // Clean
            if (id == R.id.dialog_filter_header_clear) {
                processOnClickClean();
            }
            // Cancel
            else if (id == R.id.dialog_filter_button_cancel){
                processOnClickCancel();
            }
            // Save
            else if (id == R.id.dialog_filter_button_done){
                processOnClickDone();
            }
        }

        private void processOnClickCancel() {
            mParent.toCancelFilters = true;
            mParent.dismiss();
        }

        /**
         * Process the click on save button.
         * Create a content values and send it to parent
         * @author sergiopereira
         */
        private void processOnClickDone() {
            Print.d(TAG, "CLICKED ON: DONE");
            // Validate and send to catalog fragment
            mParent.onSubmitFilterValues(mParent.filterSelectionController.getValues());
            mParent.toCancelFilters = false;
            // Dismiss dialog
            mParent.dismiss();
        }
        
        /**
         * Add to content values the selected category into the PRODUCT_URL tag
         * @param filter
         * @param contentValues
         * @author sergiopereira
         */
//        private void addCategoryFilter(CatalogFilter filter, ContentValues contentValues){
//            CatalogFilterOption selectedOption = filter.getSelectedOption().valueAt(0);
//            if(selectedOption instanceof CategoryFilterOption){
//                String url = ((CategoryFilterOption) selectedOption).getUrl();
//                Print.d(TAG, "SELECTED A NEW CATEGORY: " + url);
//                if(url != null)
//                    contentValues.put(URL, url);
//            } else {
//                cleanFilter(filter);
//                Toast.makeText(getActivity(), getResources().getString(R.string.category_filter_error), Toast.LENGTH_SHORT).show();
//            }
//        }

        /**
         * Add to content values all selected brands into the SEARCH_QUERY tag
         * @param filter
         * @param contentValues
         * @author sergiopereira
         */
//        private void addBrandFilter(CatalogFilter filter, ContentValues contentValues){
//            String query = "";
//            int size = filter.getSelectedOption().size();
//            for(int i = 0; i < size; i++)
//                query += filter.getSelectedOption().valueAt(i).getLabel() + ((i + 1 < size) ? "--" : "");
//            contentValues.put(BRAND, query);
//        }

        /**
         * Add to the content values selected filter
         * @param filter
         * @param contentValues
         * @author sergiopereira
         */
//        private void addGenericFilter(CatalogFilter filter, ContentValues contentValues){
//            // Get filter id and values
//            String filterId = filter.getId();
//            String filterValue = "";
//            // Get
//            int size = filter.getSelectedOption().size();
//            for(int i = 0; i < size; i++)
//                filterValue += filter.getSelectedOption().valueAt(i).getLabel() + ((i + 1 < size) ? "--" : "");
//            contentValues.put(filterId, filterValue);
//        }

        /**
         * Add to the content values selected price filter
         * @param filter
         * @param contentValues
         * @author sergiopereira
         */
//        private void addPriceFilter(CatalogFilter filter, ContentValues contentValues){
//            // Get filter id and values
//            String filterId = filter.getId();
//            // String filterId = filter.getId();
//            if(filter.hasRangeValues()){
//                int min = filter.getMinRangeValue();
//                int max = filter.getMaxRangeValue();
//                contentValues.put(filterId, min + "-" + max);
//            }
//            // String special_price = 1
//            if(filter.isRangeWithDiscount()){
//                contentValues.put("special_price", "1");
//            }
//        }

        /**
         * Process the click on clean button
         * @author sergiopereira
         */
        private void processOnClickClean(){
            Print.d(TAG, "CLICKED ON: CLEAR");

            mParent.filterSelectionController.initAllInitialFilterValues();

            // Clean all saved values
            mParent.filterSelectionController.cleanAllFilters();
            // Update adapter
            ((FiltersArrayAdapter) mFilterListView.getAdapter()).notifyDataSetChanged();
        }
    }

    /*
     * ################# ADAPTER ################
     */
    
    /**
     * Adapter used to show the filters
     * @author sergiopereira
     *
     */
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