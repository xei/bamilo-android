package com.mobile.service.objects.catalog.filters;

import android.content.ContentValues;
import android.os.Parcel;

import com.mobile.service.objects.IJSONSerializable;
import com.mobile.service.pojo.RestConstants;
import com.mobile.service.utils.CollectionUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 * <p/>
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/09/04
 */
public class CatalogCategoryFilter extends CatalogFilter {

    private ArrayList<MultiFilterOptionInterface> filterOptions;

    private SelectedFilterOptions selectedFilterOptions;

    public CatalogCategoryFilter() {
        filterOptions = new ArrayList<>();
        selectedFilterOptions = new SelectedFilterOptions();
    }

    public CatalogCategoryFilter(JSONObject jsonObject) throws JSONException {
        this();
        initialize(jsonObject);
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        super.initialize(jsonObject);
        JSONArray optionsArray = jsonObject.getJSONArray(RestConstants.OPTION);
        for (int i = 0; i < optionsArray.length(); i++) {
            MultiFilterOptionInterface filterOptionType = getFilterOptionType(optionsArray.getJSONObject(i));
            if(filterOptionType != null) filterOptions.add(filterOptionType);
        }
        return true;
    }

    @Override
    protected void setOptionType(String id) {
        optionType = CatalogCheckFilterOption.class;
    }

    @Override
    protected ContentValues getValues() {
        ContentValues values = new ContentValues();
        if (hasAppliedFilters()) {
            values.put(id, multi ? processMulti() : processSingle());
        }
        return values;
    }

    @Override
    public boolean hasAppliedFilters() {
        fillSelectedFilterOptions();
        return CollectionUtils.isNotEmpty(selectedFilterOptions);
    }

    private void fillSelectedFilterOptions() {
        int counter = 0;
        selectedFilterOptions.clear();
        for (MultiFilterOptionInterface filterOption : filterOptions){
            if (filterOption.isSelected()){
                selectedFilterOptions.put(counter, filterOption);
                counter++;
            }
        }
    }

    private String processSingle() {
        return CollectionUtils.isNotEmpty(selectedFilterOptions) ? selectedFilterOptions.valueAt(0).getVal() : "";
    }

    private String processMulti() {
        String value = processSingle();
        for (int i = 1; i < selectedFilterOptions.size(); i++) {
            value += filterSeparator + selectedFilterOptions.valueAt(i).getVal();
        }
        return value;

    }

    @Override
    public void cleanFilter() {
        for (int i = 0; i < selectedFilterOptions.size(); i++)
            selectedFilterOptions.valueAt(i).setSelected(false);
        this.selectedFilterOptions.clear();
    }

    public ArrayList<MultiFilterOptionInterface> getFilterOptions() {
        return filterOptions;
    }

    protected MultiFilterOptionInterface getFilterOptionType(JSONObject jsonObject) throws JSONException {
        try {
            MultiFilterOptionInterface object = (MultiFilterOptionInterface) optionType.newInstance();
            if (object instanceof IJSONSerializable) {
                ((IJSONSerializable) object).initialize(jsonObject);
            }
            return object;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public SelectedFilterOptions getSelectedFilterOptions() {
        return selectedFilterOptions;
    }

    public void switchSelectedOptions(SelectedFilterOptions selectedFilterOptions) {
        for (MultiFilterOptionInterface filterOption : filterOptions) {
            filterOption.setSelected(false);
        }

        for (int j = 0; j < selectedFilterOptions.size(); j++) {
            selectedFilterOptions.valueAt(j).setSelected(true);
        }

        this.selectedFilterOptions = selectedFilterOptions;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        if (filterOptions == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeList(filterOptions);
        }
        dest.writeValue(selectedFilterOptions);
    }

    protected CatalogCategoryFilter(Parcel in) {
        super(in);
        if (in.readByte() == 0x01) {
            filterOptions = new ArrayList<>();
            in.readList(filterOptions, MultiFilterOptionInterface.class.getClassLoader());
        } else {
            filterOptions = null;
        }
        selectedFilterOptions = (SelectedFilterOptions) in.readValue(SelectedFilterOptions.class.getClassLoader());

    }

    public static final Creator<CatalogCategoryFilter> CREATOR = new Creator<CatalogCategoryFilter>() {
        public CatalogCategoryFilter createFromParcel(Parcel source) {
            return new CatalogCategoryFilter(source);
        }

        public CatalogCategoryFilter[] newArray(int size) {
            return new CatalogCategoryFilter[size];
        }
    };

}
