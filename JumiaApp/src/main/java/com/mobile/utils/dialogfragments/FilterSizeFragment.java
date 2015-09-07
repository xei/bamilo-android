package com.mobile.utils.dialogfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.mobile.controllers.FilterOptionArrayAdapter;
import com.mobile.newFramework.objects.catalog.filters.CatalogCheckFilter;
import com.mobile.newFramework.objects.catalog.filters.MultiFilterOptionService;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.view.R;

/**
 * 
 * @author sergiopereira
 *
 */
public class FilterSizeFragment extends FilterFragment implements OnClickListener, OnItemClickListener{
    
    private static final String TAG = FilterSizeFragment.class.getSimpleName();

    private FilterOptionArrayAdapter mOptionArray;

    private CatalogCheckFilter mFilter;

    /**
     * 
     * @param parent
     * @param bundle
     * @return
     */
    public static FilterSizeFragment newInstance(DialogFilterFragment parent, Bundle bundle) {
        Print.d(TAG, "NEW INSTANCE: BRAND");
        FilterSizeFragment frag = new FilterSizeFragment();
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
        mCatalogFilter = bundle.getParcelable(DialogFilterFragment.FILTER_TAG);
        mFilter = (CatalogCheckFilter) mCatalogFilter;
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_filter_fragment_size, container, false);
    }

    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onViewCreated(android.view.View, android.os.Bundle)
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Get multi selection option
        allowMultiSelection = mFilter.isMulti();
        Print.d(TAG, "IS MULTI SELECTION: " + allowMultiSelection);
        
        // Get pre selected option
        if(mFilter.getSelectedFilterOptions().size() > 0) loadSelectedItems();
        else Print.i(TAG, "PRE SELECTION IS EMPTY");
        
        // Back button
        view.findViewById(mBackButtonId).setOnClickListener(this);
        // Clear button
        view.findViewById(mClearButtonId).setOnClickListener(this);
        // Cancel button
        view.findViewById(mCancelButtonId).setOnClickListener(this);
        // Save button
        view.findViewById(mDoneButtonId).setOnClickListener(this);
        // Filter list
        ((ListView) view.findViewById(mListId)).setOnItemClickListener(this);
        mOptionArray = new FilterOptionArrayAdapter(getActivity(), mFilter.getFilterOptions());
        ((ListView) view.findViewById(mListId)).setAdapter(mOptionArray);
    }

    /*
     * (non-Javadoc)
     * @see android.view.View.OnClickListener#onClick(android.view.View)
     */
    @Override
    public void onClick(View view) {
        Print.d(TAG, "ON CLICK: FILTER BACK");
        int id = view.getId();
        
        // Clean current selection
        if(id == mBackButtonId || id == mCancelButtonId) {
            Print.d(TAG, "FILTER BACK: " + mCurrentSelectedOptions.size());
            // Clean the current selection
            cleanOldSelections();
            // Go to back
            mParent.allowBackPressed();
            
        } else if(id == mClearButtonId) {
            Print.d(TAG, "FILTER CLEAN: " + mCurrentSelectedOptions.size());
            // Clean the current selection
            if(mCurrentSelectedOptions.size() > 0){
                //
                cleanOldSelections();
                // 
                mOptionArray.notifyDataSetChanged();
            }
            
        } else if(id == mDoneButtonId) {
            Print.d(TAG, "FILTER SAVE: " + mCurrentSelectedOptions.size());
            // Save the current selection
            mFilter.setSelectedFilterOptions(mCurrentSelectedOptions);
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
        Print.d(TAG, "ON ITEM CLICK: FILTER OPTION " + position);
        // Validate if is multi
        if(allowMultiSelection) processMultiSelection(parent, position);
        else processSingleSelection(parent, position);
        // Update adapter
        ((BaseAdapter) parent.getAdapter()).notifyDataSetChanged();
    }
    
    /**
     * Method used to save multiple options
     * @param parent
     * @param position
     * @author sergiopereira
     */
    private void processMultiSelection(AdapterView<?> parent, int position){
        // Validate if checked or not
        MultiFilterOptionService option = mCurrentSelectedOptions.get(position);
        if( option == null) {
            Print.d(TAG, "FILTER MULTI SELECTION: CHECK " + position);
            // Add item
            addSelectedItem((MultiFilterOptionService) parent.getItemAtPosition(position), position);
        } else {
            // Uncheck
            Print.d(TAG, "FILTER MULTI SELECTION: UNCHECK " + position);
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
            Print.d(TAG, "FILTER SINGLE SELECTION: DISABLE " + position);
            // Clean old selection
            cleanOldSelections();
        } else {
            Print.d(TAG, "FILTER SINGLE SELECTION: CLEAN AND ADD " + position);
            // Clean old selection
            cleanOldSelections();
            // Add item
            addSelectedItem((MultiFilterOptionService) parent.getItemAtPosition(position), position);
        }
    }

    /**
     * Load the pre selected options
     * @author sergiopereira
     */
    private void loadSelectedItems(){
        Print.d(TAG, "PRE SELECTION SIZE: " + mFilter.getSelectedFilterOptions().size());
        // Copy all selected items
        for (int i = 0; i < mFilter.getSelectedFilterOptions().size(); i++) {
            // Get position
            int position = mFilter.getSelectedFilterOptions().keyAt(i);
            // Get option
            MultiFilterOptionService option = mFilter.getSelectedFilterOptions().get(position);
            // Save item
            mCurrentSelectedOptions.put(position, option);
            // Set option as selected
            option.setSelected(true);
        }
    }
    
}