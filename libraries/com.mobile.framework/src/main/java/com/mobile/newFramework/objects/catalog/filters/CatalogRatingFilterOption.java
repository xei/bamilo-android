package com.mobile.newFramework.objects.catalog.filters;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rsoares on 9/3/15.
 */
public class CatalogRatingFilterOption implements IJSONSerializable, MultiFilterOptionService {

    public CatalogRatingFilterOption(){}

    public CatalogRatingFilterOption(JSONObject jsonObject) throws JSONException {
        this();
        initialize(jsonObject);
    }

    protected int totalProducts;
    protected String val;
    private int average;
    protected boolean selected;

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        totalProducts = jsonObject.getInt(RestConstants.JSON_TOTAL_PRODUCTS_TAG);
        val = jsonObject.getString(RestConstants.JSON_VAL_TAG);
        average = jsonObject.getInt(RestConstants.JSON_RATINGS_AVERAGE_TAG);
        return true;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return null;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    public String getLabel() {
        return average+"";
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public String getVal() {
        return val;
    }

    public int getAverage() {
        return average;
    }
}
