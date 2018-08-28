package com.bamilo.android.framework.service.objects.catalog.filters;

import android.os.Parcel;
import android.os.Parcelable;
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
public class SelectedFilterOptions extends SparseArray<MultiFilterOptionInterface> implements FilterOptionInterface, Parcelable {

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
    }

    protected SelectedFilterOptions(Parcel in) {
    }

    public static final Creator<SelectedFilterOptions> CREATOR = new Creator<SelectedFilterOptions>() {
        public SelectedFilterOptions createFromParcel(Parcel source) {
            return new SelectedFilterOptions(source);
        }

        public SelectedFilterOptions[] newArray(int size) {
            return new SelectedFilterOptions[size];
        }
    };
}
