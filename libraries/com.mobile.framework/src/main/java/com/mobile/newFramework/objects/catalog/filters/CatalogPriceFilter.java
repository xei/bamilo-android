package com.mobile.newFramework.objects.catalog.filters;

import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rsoares on 9/4/15.
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
        option.setRangeMin(option.getRangeMin());
        option.setRangeMax(option.getRangeMax());
    }

    public CatalogPriceFilterOption getOption() {
        return option;
    }

    public void setOption(CatalogPriceFilterOption option) {
        this.option = option;
    }

}
