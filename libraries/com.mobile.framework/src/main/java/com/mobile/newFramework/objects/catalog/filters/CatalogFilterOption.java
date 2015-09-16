package com.mobile.newFramework.objects.catalog.filters;

import com.mobile.newFramework.objects.IJSONSerializable;
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
 * @date 2015/09/11
 *
 */
public abstract class CatalogFilterOption implements IJSONSerializable{

    protected int totalProducts;

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        totalProducts = jsonObject.getInt(RestConstants.JSON_TOTAL_PRODUCTS_TAG);
        return true;
    }

    public int getTotalProducts() {
        return totalProducts;
    }
}
