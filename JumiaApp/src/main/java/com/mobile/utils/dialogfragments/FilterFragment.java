package com.mobile.utils.dialogfragments;

import android.support.v4.app.Fragment;

import com.mobile.newFramework.objects.catalog.filters.CatalogFilter;
import com.mobile.newFramework.objects.catalog.filters.MultiFilterOptionInterface;
import com.mobile.newFramework.objects.catalog.filters.SelectedFilterOptions;
import com.mobile.view.R;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * @author
 * @version 1.0
 * @date 2015/04/27
 */
public class FilterFragment extends Fragment {
    protected static int mBackButtonId = R.id.dialog_filter_header_title;

    protected static int mClearButtonId = R.id.dialog_filter_header_clear;

    protected static int mCancelButtonId = R.id.dialog_filter_button_cancel;

    protected static int mDoneButtonId = R.id.dialog_filter_button_done;

    protected static int mListId = R.id.dialog_filter_list;

    protected DialogFilterFragment mParent;

    protected CatalogFilter mCatalogFilter;

    protected SelectedFilterOptions mCurrentSelectedOptions = new SelectedFilterOptions();

    protected boolean allowMultiSelection;

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
    protected void addSelectedItem(MultiFilterOptionInterface option, int position){
        // Add selected
        mCurrentSelectedOptions.put(position, option);
        // Set selected
        option.setSelected(true);
    }

    /**
     * Clean selected item
     * @author sergiopereira
     */
    protected void cleanSelectedItem(MultiFilterOptionInterface option, int position){
        // Disable old selection
        option.setSelected(false);
        // Remove item
        mCurrentSelectedOptions.remove(position);
    }
}
