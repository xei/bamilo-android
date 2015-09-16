package com.mobile.newFramework.objects.catalog.filters;

import android.content.ContentValues;

import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
public class CatalogPriceFilter extends CatalogFilter{

    private CatalogPriceFilterOption option;

    public CatalogPriceFilter(){
        option = new CatalogPriceFilterOption();
    }

    public CatalogPriceFilter(JSONObject jsonObject) throws JSONException {
        this();
        initialize(jsonObject);
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        super.initialize(jsonObject);
        option.initialize(jsonObject.getJSONObject(RestConstants.JSON_OPTION_TAG));
        return true;
    }

    @Override
    protected void parseFields(JSONArray fieldsArray) throws JSONException {
        if(fieldsArray.length() != 0){
            option.setCheckBoxOption(new PriceFilterCheckBoxOption(fieldsArray.getJSONObject(0)));
        }
    }

    @Override
    protected void setOptionType(String id) {
        optionType = CatalogPriceFilterOption.class;
    }

    @Override
    protected ContentValues getValues() {
        ContentValues values = new ContentValues();
        if(option.getRangeMin() != option.getMin() || option.getRangeMax() != option.getMax()){
            values.put(id, option.getRangeMin() + filterSeparator + option.getRangeMax());
        }

        values.put(option.getCheckBoxOption().getId(), option.getCheckBoxOption().isSelected());
        return values;
    }

    @Override
    public void cleanFilter() {
        option.setRangeMin(option.getMin());
        option.setRangeMax(option.getMax());
        option.getCheckBoxOption().setSelected(false);
    }

    public CatalogPriceFilterOption getOption() {
        return option;
    }

    public void setOption(CatalogPriceFilterOption option) {
        this.option = option;
    }

}
