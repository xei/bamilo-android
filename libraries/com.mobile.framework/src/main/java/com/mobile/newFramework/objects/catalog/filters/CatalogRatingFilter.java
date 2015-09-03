package com.mobile.newFramework.objects.catalog.filters;

import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rsoares on 9/3/15.
 */
public class CatalogRatingFilter extends CatalogFilter{

    public CatalogRatingFilter(){}

    public CatalogRatingFilter(JSONObject jsonObject) throws JSONException {
        this();
        initialize(jsonObject);
    }

    protected int min;
    protected int max;

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        JSONObject starsSize = jsonObject.getJSONObject(RestConstants.JSON_RATING_STAR_SIZE_TAG);
        min = starsSize.getInt(RestConstants.JSON_MIN_TAG);
        max = starsSize.getInt(RestConstants.JSON_MAX_TAG);
        return super.initialize(jsonObject);
    }
}
