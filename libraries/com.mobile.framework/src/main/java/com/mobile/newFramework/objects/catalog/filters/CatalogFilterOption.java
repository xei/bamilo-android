package com.mobile.newFramework.objects.catalog.filters;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rsoares on 9/11/15.
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
