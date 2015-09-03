package com.mobile.newFramework.objects.catalog.filters;

import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rsoares on 9/3/15.
 */
public class CatalogColorFilterOption extends CatalogCheckFilterOption {

    public CatalogColorFilterOption(){}

    public CatalogColorFilterOption(JSONObject jsonObject) throws JSONException {
        this();
        initialize(jsonObject);
    }

    protected String hexValue;

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        hexValue = jsonObject.getString(RestConstants.JSON_HEX_VALUE_TAG);
        return super.initialize(jsonObject);
    }
}
