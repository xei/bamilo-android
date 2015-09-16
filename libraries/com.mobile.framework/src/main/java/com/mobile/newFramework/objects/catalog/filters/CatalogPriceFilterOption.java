package com.mobile.newFramework.objects.catalog.filters;

import com.mobile.newFramework.objects.IJSONSerializable;
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
public class CatalogPriceFilterOption implements IJSONSerializable, SingleFilterOptionInterface {

    private int min;
    private int max;
    private int interval;
    private int rangeMin;
    private int rangeMax;
    private PriceFilterCheckBoxOption checkBoxOption;

    public CatalogPriceFilterOption(){}

    public CatalogPriceFilterOption(JSONObject jsonObject) throws JSONException {
        this();
        initialize(jsonObject);
    }

    CatalogPriceFilterOption(CatalogPriceFilterOption priceFilterOption){
        this.min = priceFilterOption.min;
        this.max = priceFilterOption.max;
        this.interval = priceFilterOption.interval;
        this.rangeMin = priceFilterOption.rangeMin;
        this.rangeMax = priceFilterOption.rangeMax;
        this.checkBoxOption = new PriceFilterCheckBoxOption(priceFilterOption.checkBoxOption);
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
    public SingleFilterOptionInterface clone() {
        return new CatalogPriceFilterOption(this);
    }


    public PriceFilterCheckBoxOption getCheckBoxOption() {
        return checkBoxOption;
    }

    public void setCheckBoxOption(PriceFilterCheckBoxOption checkBoxOption) {
        this.checkBoxOption = checkBoxOption;
    }
}
