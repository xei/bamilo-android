package com.mobile.newFramework.objects.catalog.filters;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rsoares on 9/3/15.
 */
public class CatalogPriceFilterOption implements IJSONSerializable, FilterOptionService {

    public CatalogPriceFilterOption(){}

    public CatalogPriceFilterOption(JSONObject jsonObject) throws JSONException {
        this();
        initialize(jsonObject);
    }

    protected int min;
    protected int max;
    protected int interval;
    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        min = jsonObject.getInt(RestConstants.JSON_MIN_TAG);
        max = jsonObject.getInt(RestConstants.JSON_MAX_TAG);
        interval = jsonObject.getInt(RestConstants.JSON_INTERVAL_TAG);
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
