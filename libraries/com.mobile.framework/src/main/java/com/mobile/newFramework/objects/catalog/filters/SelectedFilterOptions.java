package com.mobile.newFramework.objects.catalog.filters;

import android.util.SparseArray;

/**
 * Created by rsoares on 9/7/15.
 */
public class SelectedFilterOptions extends SparseArray<MultiFilterOptionService> implements FilterOptionService{

    public SelectedFilterOptions(){}

    public SelectedFilterOptions(SparseArray<MultiFilterOptionService> toClone){
        for (int i = 0; i < toClone.size(); i++) {
            int key = toClone.keyAt(i);
            MultiFilterOptionService catalogFilterOption = toClone.get(key);
            if (catalogFilterOption != null) {
                put(key, catalogFilterOption);
            }
        }
    }
}
