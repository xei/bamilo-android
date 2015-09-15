package com.mobile.newFramework.objects.catalog.filters;

import com.mobile.newFramework.objects.RequiredJson;
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
 * @date 2015/09/03
 *
 */
public class CatalogCheckFilterOption extends CatalogFilterOption implements MultiFilterOptionInterface {

    public CatalogCheckFilterOption(){}

    public CatalogCheckFilterOption(JSONObject jsonObject) throws JSONException {
        this();
        initialize(jsonObject);
    }

    protected String id;
    protected String label;
    protected String val;

    protected boolean selected;

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        id = jsonObject.optString(RestConstants.ID);
        label = jsonObject.getString(RestConstants.LABEL);
        val = jsonObject.getString(RestConstants.JSON_VAL_TAG);
        return super.initialize(jsonObject);
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
        return label;
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    public String getVal() {
        return val;
    }
}
