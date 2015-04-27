package com.mobile.utils.dialogfragments;

import android.support.v4.app.Fragment;
import android.util.SparseArray;

import com.mobile.framework.objects.CatalogFilter;
import com.mobile.framework.objects.CatalogFilterOption;
import com.mobile.view.R;

/**
 * Created by rsoares on 4/27/15.
 */
public class FilterFragment extends Fragment {
    protected static int mBackButtonId = R.id.dialog_filter_header_title;

    protected static int mClearButtonId = R.id.dialog_filter_header_clear;

    protected static int mCancelButtonId = R.id.dialog_filter_button_cancel;

    protected static int mDoneButtonId = R.id.dialog_filter_button_done;

    protected static int mListId = R.id.dialog_filter_list;

    protected DialogFilterFragment mParent;

    protected CatalogFilter mCatalogFilter;

    protected SparseArray<CatalogFilterOption> mCurrentSelectedOptions = new SparseArray<>();

    protected boolean allowMultiselection;

    /**
     * Clean all old selections
     * @author sergiopereira
     */
    void cleanOldSelections(){
        // Disable old selection
        for(int i = 0; i < mCurrentSelectedOptions.size(); i++) {
            mCurrentSelectedOptions.valueAt(i).setSelected(false);
        }
        // Clean array
        mCurrentSelectedOptions.clear();
    }

    /**
     * Save the selected item
     * @author sergiopereira
     */
    protected void addSelectedItem(CatalogFilterOption option, int position){
        // Add selected
        mCurrentSelectedOptions.put(position, option);
        // Set selected
        option.setSelected(true);
    }

    /**
     * Clean selected item
     * @author sergiopereira
     */
    protected void cleanSelectedItem(CatalogFilterOption option, int position){
        // Disable old selection
        option.setSelected(false);
        // Remove item
        mCurrentSelectedOptions.remove(position);
    }
}
