package com.mobile.newFramework.objects.catalog.filters;

import android.content.ContentValues;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.CollectionUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/09/04
 *
 */
public class CatalogCheckFilter extends CatalogFilter{

    private ArrayList<MultiFilterOptionInterface> filterOptions;

    private SelectedFilterOptions selectedFilterOptions;

    public CatalogCheckFilter(){
        filterOptions = new ArrayList<>();
        selectedFilterOptions = new SelectedFilterOptions();
    }

    public CatalogCheckFilter(JSONObject jsonObject) throws JSONException {
        this();
        initialize(jsonObject);
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        super.initialize(jsonObject);

        JSONArray optionsArray = jsonObject.getJSONArray(RestConstants.JSON_OPTION_TAG);

        for (int i = 0; i < optionsArray.length(); i++) {
            filterOptions.add(getFilterOptionType(optionsArray.getJSONObject(i)));
        }
        return true;
    }

    @Override
    protected void setOptionType(String id) {
        optionType = (!id.equals(COLOR)) ? CatalogCheckFilterOption.class : CatalogColorFilterOption.class;
    }

    @Override
    protected ContentValues getValues() {
        ContentValues values = new ContentValues();
        if(hasAppliedFilters()) {
            values.put(id, multi ? processMulti() : processSingle());
        }
        return values;
    }

    @Override
    public boolean hasAppliedFilters() {
        return CollectionUtils.isNotEmpty(selectedFilterOptions);
    }

    private String processSingle() {
        return CollectionUtils.isNotEmpty(selectedFilterOptions) ? selectedFilterOptions.valueAt(0).getVal() : "";
    }

    private String processMulti(){
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

    public void setFilterOptions(ArrayList<MultiFilterOptionInterface> filterOptions) {
        this.filterOptions = filterOptions;
    }

    protected MultiFilterOptionInterface getFilterOptionType(JSONObject jsonObject) throws JSONException {
        try {
            MultiFilterOptionInterface object = (MultiFilterOptionInterface) optionType.newInstance();
            if(object instanceof IJSONSerializable){
                ((IJSONSerializable) object).initialize(jsonObject);
            }
            return object;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    public SelectedFilterOptions getSelectedFilterOptions() {
        return selectedFilterOptions;
    }

    public void setSelectedFilterOptions(SelectedFilterOptions selectedFilterOptions) {
        this.selectedFilterOptions = selectedFilterOptions;
    }

    public void switchSelectedOptions(SelectedFilterOptions selectedFilterOptions){
        for(MultiFilterOptionInterface filterOption : filterOptions){
            filterOption.setSelected(false);
        }

        for(int j = 0; j < selectedFilterOptions.size(); j++){
            selectedFilterOptions.valueAt(j).setSelected(true);
        }

        this.selectedFilterOptions = selectedFilterOptions;
    }
}
