package com.mobile.newFramework.objects.catalog.filters;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rsoares on 9/3/15.
 */
public class CatalogPriceFilterOption implements IJSONSerializable, SingleFilterOptionService{

    private int min;
    private int max;
    private int interval;
    private int rangeMin;
    private int rangeMax;

    public CatalogPriceFilterOption(){}

    public CatalogPriceFilterOption(JSONObject jsonObject) throws JSONException {
        this();
        initialize(jsonObject);
    }

    public CatalogPriceFilterOption(CatalogPriceFilterOption priceFilterOption){
        this.min = priceFilterOption.min;
        this.max = priceFilterOption.max;
        this.interval = priceFilterOption.interval;
        this.rangeMin = priceFilterOption.rangeMin;
        this.rangeMax = priceFilterOption.rangeMax;
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        min = jsonObject.getInt(RestConstants.JSON_MIN_TAG);
        rangeMin = min;
        max = jsonObject.getInt(RestConstants.JSON_MAX_TAG);
        rangeMax = max;
        interval = jsonObject.getInt(RestConstants.JSON_INTERVAL_TAG);
        return true;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return null;
    }


    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int getInterval() {
        return interval;
    }

    public int getRangeMin() {
        return rangeMin;
    }

    public void setRangeMin(int rangeMin) {
        this.rangeMin = rangeMin;
    }

    public int getRangeMax() {
        return rangeMax;
    }

    public void setRangeMax(int rangeMax) {
        this.rangeMax = rangeMax;
    }

    @Override
    public SingleFilterOptionService clone() {
        return new CatalogPriceFilterOption(this);
    }
}
