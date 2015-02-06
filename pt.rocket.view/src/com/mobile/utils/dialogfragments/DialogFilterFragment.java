package com.mobile.utils.dialogfragments;

import java.util.ArrayList;
import java.util.List;

import com.mobile.components.customfontviews.TextView;
import com.mobile.framework.objects.CatalogFilter;
import com.mobile.framework.objects.CatalogFilterOption;
import com.mobile.framework.objects.CategoryFilterOption;
import com.mobile.framework.utils.LogTagHelper;
import com.mobile.helpers.products.GetProductsHelper;
import com.mobile.utils.TrackerDelegator;
import com.mobile.view.R;
import com.mobile.view.fragments.CatalogFragment;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import de.akquinet.android.androlog.Log;

/**
 * Class used to show filters for catalog
 * @author sergiopereira
 */
public class DialogFilterFragment extends DialogFragment {

    private final static String TAG = LogTagHelper.create(DialogFilterFragment.class);

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

    private static ArrayList<CatalogFilter> mFilters;

    private CatalogFragment mParentFrament;

    /**
     * Empty constructor
     */
    public DialogFilterFragment() { }

    /**
     * 
     * @param bundle
     * @param onClickListener 
     * @return
     */
    public static DialogFilterFragment newInstance(Bundle bundle, CatalogFragment mParentFrament) {
        Log.d(TAG, "NEW INSTANCE");
        DialogFilterFragment dialogListFragment = new DialogFilterFragment();
        dialogListFragment.setArguments(bundle);
        dialogListFragment.mParentFrament = mParentFrament;
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
            Log.i(TAG, "ON SWITCH FILTER: FILTER_MAIN");
            FilterMainFragment fragmentOne = FilterMainFragment.newInstance(this);
            fragmentChildManagerTransition(R.id.dialog_filter_container, fragmentOne, false, true);
            break;
        case FILTER_CATEGORY_TYPE:
            Log.i(TAG, "ON SWITCH FILTER: FILTER_CATEGORY");
            FilterCategoryFragment fragmentCategory = FilterCategoryFragment.newInstance(this, bundle);
            fragmentChildManagerTransition(R.id.dialog_filter_container, fragmentCategory, true, true);
            break;
        case FILTER_BRAND_TYPE:
            Log.i(TAG, "ON SWITCH FILTER: FILTER_BRAND");
            FilterBrandFragment fragmentBrand = FilterBrandFragment.newInstance(this, bundle);
            fragmentChildManagerTransition(R.id.dialog_filter_container, fragmentBrand, true, true);
            break;
        case FILTER_SIZE_TYPE:
            Log.i(TAG, "ON SWITCH FILTER: FILTER_SIZE");
            FilterSizeFragment fragmentSize = FilterSizeFragment.newInstance(this, bundle);
            fragmentChildManagerTransition(R.id.dialog_filter_container, fragmentSize, true, true);
            break;
        case FILTER_PRICE_TYPE:
            Log.i(TAG, "ON SWITCH FILTER: FILTER_PRICE");
            FilterPriceFragment fragmentPrice = FilterPriceFragment.newInstance(this, bundle);
            fragmentChildManagerTransition(R.id.dialog_filter_container, fragmentPrice, true, true);
            break;
        case FILTER_COLOR_TYPE:
            Log.i(TAG, "ON SWITCH FILTER: FILTER_COLOR");
            FilterColorFragment fragmentColor = FilterColorFragment.newInstance(this, bundle);
            fragmentChildManagerTransition(R.id.dialog_filter_container, fragmentColor, true, true);
            break;
        default:
            Log.w(TAG, "ON SWITCH FILTER: UNKNOWN TYPE");
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
        // fragmentTransaction.commit();
        fragmentTransaction.commitAllowingStateLoss();
    }

    /**
     * Process the back
     */
    public void allowBackPressed() {
        Log.d(TAG, "ALLOW BACK PRESSED");
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
        CatalogFragment.isNotShowingDialogFilter = true;
    }
    
    /**
     * Method used to send the content values to parent
     * @param filterValues
     * @author sergiopereira
     */
    public void onSubmitFilterValues(ContentValues filterValues){
        if(mParentFrament != null)
            mParentFrament.onSubmitFilterValues(filterValues);
    }
    
    /*
     * ################# MAIN FRAGMENT ################
     */
    /**
     * Class used to create the main fragment that shows the list of supported filters
     * @author sergiopereira
     */
    private static class FilterMainFragment extends Fragment implements OnClickListener, OnItemClickListener {

        private DialogFilterFragment mParent;
        
        private ListView mFilterListView;
        
        private ContentValues mContentValues;
        
        /**
         * Constructor
         * @param parent
         * @return
         */
        public static FilterMainFragment newInstance(DialogFilterFragment parent) {
            Log.d(TAG, "NEW INSTANCE: MAIN");
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
            Log.d(TAG, "ON ITEM CLICK: " + position);
            // Get selected filter
            CatalogFilter selectedFilter = mFilters.get(position);
            // Get the id
            String filterId = selectedFilter.getId();
            // Create bundle
            Bundle bundle = new Bundle();
            bundle.putParcelable(FILTER_TAG, selectedFilter);
            // Goto for respective fragment
            if(filterId.contains(CATEGORY_ID)) ((DialogFilterFragment) getParentFragment()).onSwitchChildFragment(FILTER_CATEGORY_TYPE, bundle); 
            else if(filterId.contains(BRAND_ID)) ((DialogFilterFragment) getParentFragment()).onSwitchChildFragment(FILTER_BRAND_TYPE, bundle);
            else if(filterId.contains(SIZE_ID)) ((DialogFilterFragment) getParentFragment()).onSwitchChildFragment(FILTER_SIZE_TYPE, bundle);
            else if(filterId.contains(PRICE_ID)) ((DialogFilterFragment) getParentFragment()).onSwitchChildFragment(FILTER_PRICE_TYPE, bundle);
            else if(filterId.contains(COLOR_ID)) ((DialogFilterFragment) getParentFragment()).onSwitchChildFragment(FILTER_COLOR_TYPE, bundle);
            else { Log.w(TAG, "UNKNOWN FILTER ID"); bundle = null; }
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
            if (id == R.id.dialog_filter_header_clear) processOnClickClean();
            // Cancel 
            else if (id == R.id.dialog_filter_button_cancel) mParent.dismiss();
            // Save
            else if (id == R.id.dialog_filter_button_done) proccessOnClickDone();
        }
        
        /**
         * Process the click on save button.
         * Create a content values and send it to parent
         * @author sergiopereira
         */
        private void proccessOnClickDone(){
            Log.d(TAG, "CLICKED ON: DONE");
            // Create query
            mContentValues = createContentValues();
            // Validate and send to catalog fragment
            mParent.onSubmitFilterValues(mContentValues);
            // Dismiss dialog
            mParent.dismiss();
        }
        
        /**
         * Create content values to filter catalog
         * @return ContentValues
         * @author sergiopereira
         */
        private ContentValues createContentValues(){
            // Create query
            ContentValues contentValues = new ContentValues();
            String catalogFilters = "";
            // Save all values
            for (CatalogFilter filter : mFilters) {
                // Get filter id and values
                String filterId = filter.getId();
                // Case category filter
                if(filter.hasOptionSelected() && filterId.equals(CATEGORY_ID)) {
                    addCategoryFilter(filter, contentValues);
                // Case brand filter
                } else if(filter.hasOptionSelected() && filterId.equals(BRAND_ID)) {
                    addBrandFilter(filter, contentValues);
                // Case generic filter
                } else if(filter.hasOptionSelected()){
                    addGenericFilter(filter, contentValues);
                // Case price filter, get range value 
                } else if(filter.hasRangeValues()){
                    addPriceFilter(filter, contentValues);
                }
                
                if(TrackerDelegator.FILTER_COLOR.equalsIgnoreCase(filterId)) filterId = COLOR_ID;
              //TODO
                //catalogFilters = catalogFilters + filterId + ",";
            }
            /*if(!TextUtils.isEmpty(catalogFilters)) catalogFilters = catalogFilters.substring(0, catalogFilters.length()-1);
            contentValues.put(TrackerDelegator.CATALOG_FILTER_KEY, catalogFilters);*/
            return contentValues;
        }
        
        /**
         * Clean all saved values from filters
         * 
         */
        private void cleanAllFilters(){
            for (CatalogFilter filter : mFilters) {
                cleanFilter(filter);
               
              //Log.d(TAG, "FILTER: " + filter.getId() + " VALUE:" + filter.getMinRangeValue() + " " + filter.getMaxRangeValue() + " " + filter.isRangeWithDiscount());
            }
        }
        
        /**
         * Clean values of specified filter
         * @param filter
         * 
         */
        private void cleanFilter(CatalogFilter filter){
            // Generic filter: Get filter id and values, clean and disable old selection
            if(filter.hasOptionSelected())
                filter.cleanSelectedOption();
            // Price filter: Get range value 
            if(filter.hasRangeValues())
                filter.cleanRangeValues();
        }
        
        /**
         * Add to content values the selected category into the PRODUCT_URL tag
         * @param filter
         * @param contentValues
         * @author sergiopereira
         */
        private void addCategoryFilter(CatalogFilter filter, ContentValues contentValues){
            CatalogFilterOption selectedOption = filter.getSelectedOption().valueAt(0);
            if(selectedOption != null && selectedOption instanceof CategoryFilterOption){
                String url = ((CategoryFilterOption) selectedOption).getUrl();
                Log.d(TAG, "SELECTED A NEW CATEGORY: " + url );
                if(url != null)
                    contentValues.put(GetProductsHelper.PRODUCT_URL, url);
            } else {
                cleanFilter(filter);
                Toast.makeText(getActivity(), getResources().getString(R.string.category_filter_error), Toast.LENGTH_SHORT).show();
            }
        }
        
        /**
         * Add to content values all selected brands into the SEARCH_QUERY tag
         * @param filter
         * @param contentValues
         * @author sergiopereira
         */
        private void addBrandFilter(CatalogFilter filter, ContentValues contentValues){
            String query = "";
            int size = filter.getSelectedOption().size();
            for(int i = 0; i < size; i++)
                query += filter.getSelectedOption().valueAt(i).getLabel() + ((i + 1 < size) ? "--" : "");
            contentValues.put(GetProductsHelper.SEARCH_QUERY, query);
        }
        
        /**
         * Add to the content values selected filter
         * @param filter
         * @param contentValues
         * @author sergiopereira
         */
        private void addGenericFilter(CatalogFilter filter, ContentValues contentValues){
            // Get filter id and values
            String filterId = filter.getId();
            String filterValue = "";
            // Get 
            int size = filter.getSelectedOption().size();
            for(int i = 0; i < size; i++)
                filterValue += filter.getSelectedOption().valueAt(i).getLabel() + ((i + 1 < size) ? "--" : "");
            contentValues.put(filterId, filterValue);
        }
        
        /**
         * Add to the content values selected price filter
         * @param filter
         * @param contentValues
         * @author sergiopereira
         */
        private void addPriceFilter(CatalogFilter filter, ContentValues contentValues){
            // Get filter id and values
            String filterId = filter.getId();
            // String filterId = filter.getId();
            int min = filter.getMinRangeValue();
            int max = filter.getMaxRangeValue();
            //boolean discount = filter.isRangeWithDiscount();
            contentValues.put(filterId, min + "-" + max);
        }
        
        /**
         * Process the click on clean button
         * @author sergiopereira
         */
        private void processOnClickClean(){
            Log.d(TAG, "CLICKED ON: CLEAR");
            // Clean all saved values
            cleanAllFilters();
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
            if (!filter.hasOptionSelected() && !filter.hasRangeValues())
                ((TextView) convertView.findViewById(R.id.dialog_item_subtitle)).setText(R.string.all_label);
            else if(filter.hasRangeValues())
                ((TextView) convertView.findViewById(R.id.dialog_item_subtitle)).setText(filter.getMinRangeValue() + " - " + filter.getMaxRangeValue());
            else if (filter.hasOptionSelected())
                ((TextView) convertView.findViewById(R.id.dialog_item_subtitle)).setText(getOptionsToString(filter.getSelectedOption()));
                
            // Return the filter view
            return convertView;
        }
        
        /**
         * Create a string
         * @param array
         * @return String
         */
        private String getOptionsToString(SparseArray<CatalogFilterOption> array){
            String string = "";
            for (int i = 0; i < array.size(); i++) string += ((i == 0) ? "" : ", ") + array.valueAt(i).getLabel();
            return string;
        }
    }
    
}