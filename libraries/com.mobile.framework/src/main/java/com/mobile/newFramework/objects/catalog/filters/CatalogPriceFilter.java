package com.mobile.newFramework.objects.catalog.filters;

import com.mobile.newFramework.pojo.RestConstants;

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

    }

    public CatalogPriceFilter(JSONObject jsonObject) throws JSONException {
        this();
        initialize(jsonObject);
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        super.initialize(jsonObject);
        option = new CatalogPriceFilterOption(jsonObject.getJSONObject(RestConstants.JSON_OPTION_TAG));
        return true;
    }

    @Override
    protected void setOptionType(String id) {
        optionType = CatalogPriceFilterOption.class;
    }

    @Override
    protected String getValues() {
        return (option.getRangeMin() != option.getMin() || option.getRangeMax() != option.getMax()) ? option.getRangeMin() + filterSeparator + option.getRangeMax() : null;
    }

    @Override
    public void cleanFilter() {
        option.setRangeMin(option.getMin());
        option.setRangeMax(option.getMax());
    }

    public CatalogPriceFilterOption getOption() {
        return option;
    }

    public void setOption(CatalogPriceFilterOption option) {
        this.option = option;
    }

}
