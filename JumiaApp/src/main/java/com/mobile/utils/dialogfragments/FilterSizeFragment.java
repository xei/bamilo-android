package com.mobile.utils.dialogfragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
public class FilterSizeFragment extends FilterFragment{
    
    private static final String TAG = FilterSizeFragment.class.getSimpleName();

    private FilterOptionArrayAdapter mOptionArray;

    private CatalogCheckFilter mFilter;

    /**
     *
     * @param bundle
     * @return
     */
    public static FilterSizeFragment newInstance(Bundle bundle) {
        Print.d(TAG, "NEW INSTANCE: BRAND");
        FilterSizeFragment frag = new FilterSizeFragment();
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

        // Filter list
//        ((ListView) view.findViewById(mListId)).setOnItemClickListener(this);
        mOptionArray = new FilterOptionArrayAdapter(getActivity(), mFilter);
        ((ListView) view.findViewById(mListId)).setAdapter(mOptionArray);
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