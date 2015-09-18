package com.mobile.newFramework.objects.catalog.filters;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

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

    public CatalogFilters(){}

    public CatalogFilters(JSONObject jsonObject) throws JSONException {
        this();
        initialize(jsonObject);
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        JSONArray filtersArray = jsonObject.getJSONArray(RestConstants.JSON_FILTERS_TAG);

        for(int i = 0; i< filtersArray.length();i++){
            try {
                CatalogFilter catalogFilter = getCatalogType(filtersArray.getJSONObject(i));
                if(catalogFilter != null){
                    add(catalogFilter);
                }

            }catch (JSONException ex){
                ex.printStackTrace();
            }
        }
        return true;
    }

    private CatalogFilter getCatalogType(JSONObject jsonObject) throws JSONException {
        String id = jsonObject.getString(RestConstants.ID);

        /* Temporary: category is being ignored temporarily because it has a different behavior */
        if(id.equals(CatalogFilter.CATEGORY)){
          return null;
        } else if(id.equals(CatalogFilter.RATING)){
            return new CatalogRatingFilter(jsonObject);
        } else if(id.equals(CatalogFilter.PRICE)){
            return new CatalogPriceFilter(jsonObject);
        } else {
            return new CatalogCheckFilter(jsonObject);
        }
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return RequiredJson.METADATA;
    }
}
