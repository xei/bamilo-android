package com.mobile.newFramework.objects.catalog.filters;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rsoares on 9/3/15.
 */
public class CatalogCheckFilterOption implements IJSONSerializable, FilterOptionService {

    public CatalogCheckFilterOption(){}

    public CatalogCheckFilterOption(JSONObject jsonObject) throws JSONException {
        this();
        initialize(jsonObject);
    }

    protected String id;
    protected String label;
    protected String val;
    protected int productsCount;

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        id = jsonObject.optString(RestConstants.ID);
        label = jsonObject.getString(RestConstants.LABEL);
        val = jsonObject.getString(RestConstants.JSON_VAL_TAG);
        productsCount = jsonObject.getInt(RestConstants.JSON_PRODUCTS_COUNT_TAG);
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
}
