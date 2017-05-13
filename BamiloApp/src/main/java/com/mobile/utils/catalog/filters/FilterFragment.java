package com.mobile.utils.catalog.filters;

import android.support.v4.app.Fragment;

import com.mobile.service.objects.catalog.filters.CatalogFilter;
import com.mobile.service.objects.catalog.filters.SelectedFilterOptions;
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
public abstract class FilterFragment extends Fragment {

    protected static int mListId = R.id.dialog_filter_list;

    protected CatalogFilter mCatalogFilter;

    protected SelectedFilterOptions mCurrentSelectedOptions = new SelectedFilterOptions();

    protected boolean allowMultiSelection;

    public abstract void cleanValues();
}
