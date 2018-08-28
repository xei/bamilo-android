package com.bamilo.android.appmodule.bamiloapp.utils.catalog.filters;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.bamilo.android.appmodule.bamiloapp.controllers.FilterOptionArrayAdapter;
import com.bamilo.android.framework.service.objects.catalog.filters.CatalogCheckFilter;
import com.bamilo.android.framework.service.objects.catalog.filters.MultiFilterOptionInterface;
import com.bamilo.android.framework.service.utils.output.Print;
import com.bamilo.android.R;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.FilterMainFragment;

/**
 * 
 * @author sergiopereira
 *
 */
public class FilterCheckFragment extends FilterFragment {

    private static final String TAG = FilterCheckFragment.class.getSimpleName();

    protected FilterOptionArrayAdapter mOptionArray;

    protected CatalogCheckFilter mFilter;

    /**
     * Create new instance
     */
    public static FilterCheckFragment newInstance(Bundle bundle) {
        Print.d(TAG, "NEW INSTANCE: BRAND");
        FilterCheckFragment frag = new FilterCheckFragment();
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
        mCatalogFilter = bundle.getParcelable(FilterMainFragment.FILTER_TAG);
        mFilter = (CatalogCheckFilter)mCatalogFilter;
    }
    
    /*
     * (non-Javadoc)
     * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout._def_check_filter_fragment, container, false);
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
        if(mFilter.getSelectedFilterOptions().size()>0) loadSelectedItems();
        else Print.i(TAG, "PRE SELECTION IS EMPTY");

        // Create adapter
        mOptionArray = createAdapter();
        // Set adapter
        ((ListView) view.findViewById(mListId)).setAdapter(mOptionArray);
        ((ListView) view.findViewById(mListId)).setOnItemClickListener(mOptionArray);
    }

    protected FilterOptionArrayAdapter createAdapter(){
        return new FilterOptionArrayAdapter(getActivity(), mFilter);
    }

    /**
     * Load the pre selected options
     * @author sergiopereira
     */
    protected void loadSelectedItems(){
        Print.d(TAG, "PRE SELECTION SIZE: " + mFilter.getSelectedFilterOptions().size());
        // Copy all selected items
        for (int i = 0; i < mFilter.getSelectedFilterOptions().size(); i++) {
            // Get position
            int position = mFilter.getSelectedFilterOptions().keyAt(i);
            // Get option
            MultiFilterOptionInterface option = mFilter.getSelectedFilterOptions().get(position);
            // Save item
            mCurrentSelectedOptions.put(position, option);
            // Set option as selected
            option.setSelected(true);
        }
    }

    @Override
    public void cleanValues() {
        if(mOptionArray != null) {
            mOptionArray.cleanOldSelections();
            mOptionArray.notifyDataSetChanged();
        }
    }
}