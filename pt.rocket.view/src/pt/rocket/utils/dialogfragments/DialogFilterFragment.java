package pt.rocket.utils.dialogfragments;

import java.util.ArrayList;
import java.util.List;

import org.holoeverywhere.widget.TextView;

import pt.rocket.framework.objects.CatalogFilter;
import pt.rocket.framework.objects.CatalogFilterOption;
import pt.rocket.framework.utils.LogTagHelper;
import pt.rocket.view.R;
import pt.rocket.view.fragments.Catalog;
import android.content.ContentValues;
import android.content.Context;
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
import de.akquinet.android.androlog.Log;

/**
 * 
 * @author sergiopereira
 * 
 */
public class DialogFilterFragment extends DialogFragment {

    private final static String TAG = LogTagHelper.create(DialogFilterFragment.class);

    public static final int NO_INITIAL_POSITION = -1;

    private static final int FILTER_CATEGORY = 0;
    private static final int FILTER_BRAND = 1;
    private static final int FILTER_SIZE = 2;
    private static final int FILTER_PRICE = 3;
    private static final int FILTER_COLOR = 4;
    private static final int FILTER_MAIN = 5;

    public final static String FILTER_TAG = "catalog_filters";

    private static ArrayList<CatalogFilter> mFilters;

    private Catalog mParentFrament;

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
    public static DialogFilterFragment newInstance(Bundle bundle, Catalog mParentFrament) {
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
        setStyle(R.style.Theme_Jumia_Dialog_Blue_NoTitle, R.style.Theme_Jumia_Dialog_Blue_NoTitle);
        Bundle bundle = getArguments();
        mFilters = bundle.getParcelableArrayList(FILTER_TAG);
        Log.d(TAG, "FILTERS: " + mFilters.size());
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
        // Get container
        onSwitchChildFragment(FILTER_MAIN, null);
    }

    /**
     * 
     * @param filterType
     * @param bundle 
     */
    private void onSwitchChildFragment(int filterType, Bundle bundle) {
        switch (filterType) {
        case FILTER_MAIN:
            Log.i(TAG, "ON SWITCH FILTER: FILTER_MAIN");
            FilterMainFragment fragmentOne = FilterMainFragment.newInstance(this);
            fragmentChildManagerTransition(R.id.dialog_filter_container, fragmentOne, false, true);
            break;
        case FILTER_CATEGORY:
            Log.i(TAG, "ON SWITCH FILTER: FILTER_CATEGORY");
            FilterCategoryFragment fragmentCategory = FilterCategoryFragment.newInstance(this, bundle);
            fragmentChildManagerTransition(R.id.dialog_filter_container, fragmentCategory, true, true);
            break;
        case FILTER_BRAND:
            Log.i(TAG, "ON SWITCH FILTER: FILTER_BRAND");
            FilterBrandFragment fragmentBrand = FilterBrandFragment.newInstance(this, bundle);
            fragmentChildManagerTransition(R.id.dialog_filter_container, fragmentBrand, true, true);
            break;
        case FILTER_SIZE:
            Log.i(TAG, "ON SWITCH FILTER: FILTER_SIZE");
            FilterSizeFragment fragmentSize = FilterSizeFragment.newInstance(this, bundle);
            fragmentChildManagerTransition(R.id.dialog_filter_container, fragmentSize, true, true);
            break;
        case FILTER_PRICE:
            Log.i(TAG, "ON SWITCH FILTER: FILTER_PRICE");
            FilterPriceFragment fragmentPrice = FilterPriceFragment.newInstance(this, bundle);
            fragmentChildManagerTransition(R.id.dialog_filter_container, fragmentPrice, true, true);
            break;
        case FILTER_COLOR:
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
    public void fragmentChildManagerTransition(int container, Fragment fragment, boolean animated,
            boolean addToBackStack) {
        FragmentTransaction fragmentTransaction = getChildFragmentManager().beginTransaction();
        // Animations
        if (animated)
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                    R.anim.slide_in_left, R.anim.slide_out_right);
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
        dismiss();
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
     * 
     * @author sergiopereira
     * 
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
            if(filterId.contains("category")) ((DialogFilterFragment) getParentFragment()).onSwitchChildFragment(FILTER_CATEGORY, bundle); 
            else if(filterId.contains("brand")) ((DialogFilterFragment) getParentFragment()).onSwitchChildFragment(FILTER_BRAND, bundle);
            else if(filterId.contains("size")) ((DialogFilterFragment) getParentFragment()).onSwitchChildFragment(FILTER_SIZE, bundle);
            else if(filterId.contains("price")) ((DialogFilterFragment) getParentFragment()).onSwitchChildFragment(FILTER_PRICE, bundle);
            else if(filterId.contains("color")) ((DialogFilterFragment) getParentFragment()).onSwitchChildFragment(FILTER_COLOR, bundle);
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
         * Process the click on save button
         * @author sergiopereira
         */
        private void proccessOnClickDone(){
            Log.d(TAG, "CLICKED ON: DONE");
            // Create query
            mContentValues = createContentValues();
            Log.d(TAG, "FILTER QUERY: " + mContentValues.toString());
            // Validate and send to catalog fragment
            mParent.onSubmitFilterValues(mContentValues);
            // Dismiss dialog
            mParent.dismiss();
        }
        
        
        /**
         * Create content values to filter catalog
         * @author sergiopereira
         * @return ContentValues
         */
        private ContentValues createContentValues(){
            // Create query
            ContentValues contentValues = new ContentValues();
            // Save all values
            for (CatalogFilter filter : mFilters) {
                // Generic filter: Get filter id and values
                if(filter.hasOptionSelected()){
                    String filterId = filter.getId();
                    String filterValue = "";
                    for(int i = 0; i < filter.getSelectedOption().size(); i++) 
                        filterValue += filter.getSelectedOption().valueAt(i).getLabel() + "--";
                    contentValues.put(filterId, filterValue);
                }
                // Range filter: Get range value 
                if(filter.hasRangeValues()) {
                    String filterId = filter.getId();
                    int min = filter.getMinRangeValue();
                    int max = filter.getMaxRangeValue();
                    boolean discount = filter.isRangeWithDiscount();
                    contentValues.put(filterId, min + "-" + max);
                }
            }
            return contentValues;
        }
        
        /**
         * Process the click on clean button
         * @author sergiopereira
         */
        private void processOnClickClean(){
            Log.d(TAG, "CLICKED ON: CLEAR");
            // Clean all saved values
            for (CatalogFilter filter : mFilters) {
                // Generic filter: Get filter id and values
                if(filter.hasOptionSelected()) {
                    // Clean and disable old selection
                    filter.cleanSelectedOption();
                }
                // Range filter: Get range value 
                if(filter.hasRangeValues()) {
                    //Log.d(TAG, "FILTER: " + filter.getId() + " VALUE:" + filter.getMinRangeValue() + " " + filter.getMaxRangeValue() + " " + filter.isRangeWithDiscount());
                    filter.cleanRangeValues();
                } 
            }
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