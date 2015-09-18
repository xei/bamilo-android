package com.mobile.newFramework.objects.catalog.filters;

import android.util.SparseArray;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/09/07
 *
 */
public class SelectedFilterOptions extends SparseArray<MultiFilterOptionInterface> implements FilterOptionInterface {

    public SelectedFilterOptions(){}

    public SelectedFilterOptions(SparseArray<MultiFilterOptionInterface> toClone){
        for (int i = 0; i < toClone.size(); i++) {
            int key = toClone.keyAt(i);
            MultiFilterOptionInterface catalogFilterOption = toClone.get(key);
            if (catalogFilterOption != null) {
                put(key, catalogFilterOption);
            }
        }
    }
}
