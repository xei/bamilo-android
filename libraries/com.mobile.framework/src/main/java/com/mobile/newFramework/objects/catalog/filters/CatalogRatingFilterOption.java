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
 * @date 2015/09/04
 *
 */
public class CatalogRatingFilterOption extends CatalogFilterOption implements MultiFilterOptionInterface {

    public CatalogRatingFilterOption(){}

    public CatalogRatingFilterOption(JSONObject jsonObject) throws JSONException {
        this();
        initialize(jsonObject);
    }

    protected String val;
    protected int average;
    protected boolean selected;

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        val = jsonObject.getString(RestConstants.JSON_VAL_TAG);
        average = jsonObject.getInt(RestConstants.JSON_RATINGS_AVERAGE_TAG);
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
        return null;
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
