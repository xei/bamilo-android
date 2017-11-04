package com.mobile.service.objects.catalog.filters;

import com.mobile.service.objects.IJSONSerializable;
import com.mobile.service.objects.RequiredJson;
import com.mobile.service.pojo.RestConstants;
import com.mobile.service.utils.output.Print;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

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
public class CatalogFilters extends ArrayList<CatalogFilter> implements IJSONSerializable {
    private JSONObject mJsonObject;

    public CatalogFilters(){}

    public CatalogFilters(JSONObject jsonObject) throws JSONException {
        this();
        initialize(jsonObject);
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        this.mJsonObject = jsonObject;
        // Validate json
        if (jsonObject.has(RestConstants.FILTERS)) {
            JSONArray filtersArray = jsonObject.getJSONArray(RestConstants.FILTERS);
            for (int i = 0; i < filtersArray.length(); i++) {
                try {
                    CatalogFilter catalogFilter = getCatalogType(filtersArray.getJSONObject(i));
                    if (catalogFilter != null) {
                        add(catalogFilter);
                    }
                } catch (JSONException ex) {
                    Print.w("WARNING: JSE ON PARSE CATALOG FILTER POSITION: " + i);
                }
            }
        } else {
            Print.w("WARNING: CATALOG FILTER IS EMPTY");
        }
        return true;
    }

    private CatalogFilter getCatalogType(JSONObject jsonObject) throws JSONException {
        String id = jsonObject.getString(RestConstants.ID);
        switch (id) {
            case CatalogFilter.RATING:
                return new CatalogRatingFilter(jsonObject);
            case CatalogFilter.PRICE:
                return new CatalogPriceFilter(jsonObject);
            default:
                return new CatalogCheckFilter(jsonObject);
        }
    }

    @Override
    public JSONObject toJSON() {
        return mJsonObject;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.METADATA;
    }
}
