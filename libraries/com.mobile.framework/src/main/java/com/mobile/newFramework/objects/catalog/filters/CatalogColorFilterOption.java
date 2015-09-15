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
public class CatalogColorFilterOption extends CatalogCheckFilterOption {

    public CatalogColorFilterOption(){}

    public CatalogColorFilterOption(JSONObject jsonObject) throws JSONException {
        this();
        initialize(jsonObject);
    }

    private String hexValue;

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        hexValue = jsonObject.getString(RestConstants.JSON_HEX_VALUE_TAG);
        return super.initialize(jsonObject);
    }

    public String getHexValue() {
        return hexValue;
    }

    public void setHexValue(String hexValue) {
        this.hexValue = hexValue;
    }
}
