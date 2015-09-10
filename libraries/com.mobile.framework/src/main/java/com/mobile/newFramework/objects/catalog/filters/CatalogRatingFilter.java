package com.mobile.newFramework.objects.catalog.filters;

import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rsoares on 9/3/15.
 */
public class CatalogRatingFilter extends CatalogCheckFilter{

    public CatalogRatingFilter(){}

    public CatalogRatingFilter(JSONObject jsonObject) throws JSONException {
        this();
        initialize(jsonObject);
    }

    private int min;
    private int max;

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        JSONObject starsSize = jsonObject.getJSONObject(RestConstants.JSON_RATING_STAR_SIZE_TAG);
        min = starsSize.getInt(RestConstants.JSON_MIN_TAG);
        max = starsSize.getInt(RestConstants.JSON_MAX_TAG);
        return super.initialize(jsonObject);
    }

    @Override
    protected void setOptionType(String id) {
        optionType = CatalogRatingFilterOption.class;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }
}
