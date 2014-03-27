package pt.rocket.utils.dialogfragments;

import java.util.List;

import org.holoeverywhere.widget.CheckBox;
import org.holoeverywhere.widget.TextView;

import pt.rocket.framework.components.PinnedSectionListView;
import pt.rocket.framework.components.PinnedSectionListView.PinnedSectionListAdapter;
import pt.rocket.framework.objects.CatalogFilter;
import pt.rocket.framework.objects.CatalogFilterOption;
import pt.rocket.view.R;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import de.akquinet.android.androlog.Log;

/**
 * 
 * @author sergiopereira
 *
 */
public class FilterBrandFragment extends Fragment implements OnClickListener, OnItemClickListener{
    
    private static final String TAG = FilterBrandFragment.class.getSimpleName();

    private static int mBackButtonId = R.id.dialog_filter_header_title;
    
    private static int mClearButtonId = R.id.dialog_filter_header_clear;
    
    private static int mCancelButtonId = R.id.dialog_filter_button_cancel;
    
    private static int mDoneButtonId = R.id.dialog_filter_button_done;
    
    private static int mPinnedListId = R.id.dialog_filter_pinnedlist;

    private DialogFilterFragment mParent;
    
    private CatalogFilter mBrandFilter;

    private FilterOptionArrayAdapter mOptionArray;

    private SparseArray<CatalogFilterOption> mCurrentSelectedOptions = new SparseArray<CatalogFilterOption>();
    
    private boolean allowMultiselection;


    /**
     * 
     * @param parent
     * @param bundle
     * @return
     */
    public static FilterBrandFragment newInstance(DialogFilterFragment parent, Bundle bundle) {
        Log.d(TAG, "NEW INSTANCE: BRAND");
        FilterBrandFragment frag = new FilterBrandFragment();
        frag.mParent = parent;
        frag.setArguments(bundle);
        return frag;
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreate(android.os.Bundle)
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle bundle = getArguments();
        // Get selected filter
        mBrandFilter = bundle.getParcelable(DialogFilterFragment.FILTER_TAG);
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_filter_fragment_brand, container, false);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get multi selection option
        allowMultiselection = mBrandFilter.isMulti();
        Log.d(TAG, "IS MULTI SELECTION: " + allowMultiselection);
        
        // Get pre selected option
        if(mBrandFilter.hasOptionSelected()) loadSelectedItems();
        else Log.i(TAG, "PRE SELECTION IS EMPTY");
        
        // Title
        ((TextView) view.findViewById(R.id.dialog_filter_header_title)).setText(mBrandFilter.getName());
        // Back button
        view.findViewById(mBackButtonId).setOnClickListener(this);
        // Clear button
        view.findViewById(mClearButtonId).setOnClickListener(this);
        // Cancel button
        view.findViewById(mCancelButtonId).setOnClickListener(this);
        // Save button
        view.findViewById(mDoneButtonId).setOnClickListener(this);
        // Filter list
        ((PinnedSectionListView) view.findViewById(mPinnedListId)).setOnItemClickListener(this);
        // Create adapter
        mOptionArray = new FilterOptionArrayAdapter(getActivity(), mBrandFilter.getFilterOptions());
        // Set adapter
        ((PinnedSectionListView) view.findViewById(mPinnedListId)).setAdapter(mOptionArray);
    }
    
    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        Log.d(TAG, "ON CLICK: FILTER BACK");
        int id = view.getId();
        
        // Clean current selection
        if(id == mBackButtonId || id == mCancelButtonId) {
            Log.d(TAG, "FILTER BACK: " + mCurrentSelectedOptions.size());
            // Clean the current selection
            cleanOldSelections();
            // Go to back
            mParent.allowBackPressed();
            
        } else if(id == mClearButtonId) {
            Log.d(TAG, "FILTER CLEAN: " + mCurrentSelectedOptions.size());
            // Clean the current selection
            if(mCurrentSelectedOptions.size() > 0){
                //
                cleanOldSelections();
                // 
                mOptionArray.notifyDataSetChanged();
            }
            
        } else if(id == mDoneButtonId) {
            Log.d(TAG, "FILTER SAVE: " + mCurrentSelectedOptions.size());
            // Save the current selection
            mBrandFilter.setSelectedOption(mCurrentSelectedOptions);
            // Goto back
            mParent.allowBackPressed();
        }
    }
    
    /*
     * (non-Javadoc)
     * @see android.widget.AdapterView.OnItemClickListener#onItemClick(android.widget.AdapterView, android.view.View, int, long)
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG, "ON ITEM CLICK: FILTER OPTION " + position);
        
        // Get selected option
        CatalogFilterOption selectedOption = (CatalogFilterOption) parent.getItemAtPosition(position);
        // Validate if is a section item
        if(selectedOption.isSectionItem()) return;
        
        // Validate if is multi
        if(allowMultiselection) processMultiSelection(parent, position);
        else processSingleSelection(parent, position);
        // Update adapter
        ((FilterOptionArrayAdapter) parent.getAdapter()).notifyDataSetChanged();
    }
    
    /**
     * Method used to save multiple options
     * @param parent
     * @param position
     * @author sergiopereira
     */
    private void processMultiSelection(AdapterView<?> parent, int position){
        // Validate if checked or not
        CatalogFilterOption option = mCurrentSelectedOptions.get(position);
        if( option == null) {
            Log.d(TAG, "FILTER MULTI SELECTION: CHECK " + position);
            // Add item
            addSelectedItem((CatalogFilterOption) parent.getItemAtPosition(position), position);
        } else {
            // Uncheck
            Log.d(TAG, "FILTER MULTI SELECTION: UNCHECK " + position);
            cleanSelectedItem(option, position);
        }
    }
    
    /**
     * Method used to save only one option
     * @param parent
     * @param position
     * @author sergiopereira
     */
    private void processSingleSelection(AdapterView<?> parent, int position){
        // Option is the last
        if(mCurrentSelectedOptions.get(position) != null) {
            Log.d(TAG, "FILTER SINGLE SELECTION: DISABLE " + position);
            // Clean old selection
            cleanOldSelections();
        } else {
            Log.d(TAG, "FILTER SINGLE SELECTION: CLEAN AND ADD " + position);
            // Clean old selection
            cleanOldSelections();
            // Add item
            addSelectedItem((CatalogFilterOption) parent.getItemAtPosition(position), position);
        }
    }
    
    
    
    /**
     * Clean selected item
     * @author sergiopereira
     */
    private void cleanSelectedItem(CatalogFilterOption option, int position){
        // Disable old selection
        option.setSelected(false);
        // Remove item
        mCurrentSelectedOptions.remove(position);
    }
    
    /**
     * Clean all old selections
     * @author sergiopereira
     */
    private void cleanOldSelections(){
        // Disable old selection
        for(int i = 0; i < mCurrentSelectedOptions.size(); i++) 
            ((CatalogFilterOption) mCurrentSelectedOptions.valueAt(i)).setSelected(false);
        // Clean array
        mCurrentSelectedOptions.clear();
    }

    /**
     * Save the selected item
     * @author sergiopereira
     */
    private void addSelectedItem(CatalogFilterOption option, int position){
        // Add selected
        mCurrentSelectedOptions.put(position, option);
        // Set selected
        option.setSelected(true);
    }
    
    /**
     * Load the pre selected options
     * @author sergiopereira
     */
    private void loadSelectedItems(){
        Log.d(TAG, "PRE SELECTION SIZE: " + mBrandFilter.getSelectedOption().size());
        // Copy all selected items
        for (int i = 0; i < mBrandFilter.getSelectedOption().size(); i++) {
            // Get position
            int position = mBrandFilter.getSelectedOption().keyAt(i);
            // Get option
            CatalogFilterOption option = mBrandFilter.getSelectedOption().get(position);
            // Save item
            mCurrentSelectedOptions.put(position, option);
            // Set option as selected
            option.setSelected(true);
        }
    }
    

    /**
     * Adapter
     * @author sergiopereira
     */
     public static class FilterOptionArrayAdapter extends ArrayAdapter<CatalogFilterOption> implements PinnedSectionListAdapter{
            
        private static int layout = R.layout.dialog_list_sub_item_2;
        
        private static int layoutHeader = R.layout.dialog_list_header_item;
        
        public static final int ITEM = 0;
        
        public static final int SECTION = 1;
        
        public static final int TYPES = 2;
        
        /**
         * Class used to fill the pinned list view
         * Receives an list sorted alphabetic and with section items
         * @param context
         * @param objects is setted on the parse of the CatalogFilter.
         */
        public FilterOptionArrayAdapter(Context context, List<CatalogFilterOption> objects) {
            super(context, layout, objects);
        }
        
        /*
         * (non-Javadoc)
         * @see android.widget.ArrayAdapter#getView(int, android.view.View, android.view.ViewGroup)
         */
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Get Filter
            CatalogFilterOption option = getItem(position);
            // Validate item type
            if(!isItemViewTypePinned(getItemViewType(position))) {
                Log.d(TAG, "FILTER OPTION: IS ITEM");
                // Validate current view
                //if (convertView == null) 
                convertView = LayoutInflater.from(getContext()).inflate(layout, null);
                // Set title
                ((TextView) convertView.findViewById(R.id.dialog_item_title)).setText(option.getLabel());
                // Set check box
                ((CheckBox) convertView.findViewById(R.id.dialog_item_checkbox)).setChecked(option.isSelected());
            } else {
                Log.d(TAG, "FILTER OPTION: IS TITLE");
                // Inflate section layout
                convertView = LayoutInflater.from(getContext()).inflate(layoutHeader, null);
                // Set section
                ((TextView) convertView.findViewById(R.id.dialog_list_section)).setText(option.getLabel());
            }
            // Return the filter view
            return convertView;
        }
    
        /*
         * (non-Javadoc)
         * @see android.widget.BaseAdapter#getViewTypeCount()
         */
        @Override
        public int getViewTypeCount() {
            return TYPES;
        }

        /*
         * (non-Javadoc)
         * @see android.widget.BaseAdapter#getItemViewType(int)
         */
        @Override 
        public int getItemViewType(int position) {
            return (getItem(position).isSectionItem()) ? SECTION : ITEM;
        }

        /*
         * (non-Javadoc)
         * @see com.hb.views.PinnedSectionListView.PinnedSectionListAdapter#isItemViewTypePinned(int)
         */
        @Override
        public boolean isItemViewTypePinned(int viewType) {
            return viewType == SECTION;
        }
    }
   
}