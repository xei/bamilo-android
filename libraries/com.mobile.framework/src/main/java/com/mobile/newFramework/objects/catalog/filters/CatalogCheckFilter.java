package com.mobile.newFramework.objects.catalog.filters;

import android.util.SparseArray;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by rsoares on 9/4/15.
 */
public class CatalogCheckFilter extends CatalogFilter{

    private ArrayList<MultiFilterOptionService> filterOptions;

    private SparseArray<MultiFilterOptionService> selectedFilterOptions;

    public CatalogCheckFilter(){
        filterOptions = new ArrayList<>();
        selectedFilterOptions = new SparseArray<>();
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

    public ArrayList<MultiFilterOptionService> getFilterOptions() {
        return filterOptions;
    }

    public void setFilterOptions(ArrayList<MultiFilterOptionService> filterOptions) {
        this.filterOptions = filterOptions;
    }

    protected MultiFilterOptionService getFilterOptionType(JSONObject jsonObject) throws JSONException {
        try {
            MultiFilterOptionService object = (MultiFilterOptionService) optionType.newInstance();
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

    public SparseArray<MultiFilterOptionService> getSelectedFilterOptions() {
        return selectedFilterOptions;
    }

    public void setSelectedFilterOptions(SparseArray<MultiFilterOptionService> selectedFilterOptions) {
        this.selectedFilterOptions = selectedFilterOptions;
    }

    public void switchSelectedOptions(SparseArray<MultiFilterOptionService> selectedFilterOptions){
        for(MultiFilterOptionService filterOption : filterOptions){
            filterOption.setSelected(false);
        }

        for(int j = 0; j < selectedFilterOptions.size(); j++){
            selectedFilterOptions.valueAt(j).setSelected(true);
        }

        this.selectedFilterOptions = selectedFilterOptions;
    }
}
