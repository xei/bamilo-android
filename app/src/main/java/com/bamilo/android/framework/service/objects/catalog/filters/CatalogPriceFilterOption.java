package com.bamilo.android.framework.service.objects.catalog.filters;

import android.os.Parcel;
import android.os.Parcelable;

import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.pojo.RestConstants;

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
public class CatalogPriceFilterOption implements IJSONSerializable, SingleFilterOptionInterface, Parcelable {

    private long min;
    private long max;
    private long interval;
    private long rangeMin;
    private long rangeMax;
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
        JSONObject optionsJsonObject = jsonObject.getJSONObject(RestConstants.OPTION);
        min = optionsJsonObject.getLong(RestConstants.MIN);
        max = optionsJsonObject.getLong(RestConstants.MAX);
        interval = optionsJsonObject.getLong(RestConstants.INTERVAL);
        JSONObject selectedJsonObject = jsonObject.optJSONObject(RestConstants.SELECTED);
        if (selectedJsonObject == null) {
            rangeMin = min;
            rangeMax = max;
        } else {
            rangeMin = selectedJsonObject.optLong(RestConstants.LOWER_VALUE);
            rangeMax = selectedJsonObject.optLong(RestConstants.UPPER_VALUE);
        }
        JSONObject specialPrice = jsonObject.getJSONObject(RestConstants.SPECIAL_PRICE);
        checkBoxOption = new PriceFilterCheckBoxOption(specialPrice);
        return true;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.NONE;
    }


    public long getMin() {
        return min;
    }

    public long getMax() {
        return max;
    }

    public long getInterval() {
        return interval;
    }

    public long getRangeMin() {
        return rangeMin;
    }

    public void setRangeMin(long rangeMin) {
        this.rangeMin = rangeMin;
    }

    public long getRangeMax() {
        return rangeMax;
    }

    public void setRangeMax(long rangeMax) {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(this.min);
        dest.writeLong(this.max);
        dest.writeLong(this.interval);
        dest.writeLong(this.rangeMin);
        dest.writeLong(this.rangeMax);
        dest.writeParcelable(this.checkBoxOption, flags);
    }

    protected CatalogPriceFilterOption(Parcel in) {
        this.min = in.readLong();
        this.max = in.readLong();
        this.interval = in.readLong();
        this.rangeMin = in.readLong();
        this.rangeMax = in.readLong();
        this.checkBoxOption = in.readParcelable(PriceFilterCheckBoxOption.class.getClassLoader());
    }

    public static final Creator<CatalogPriceFilterOption> CREATOR = new Creator<CatalogPriceFilterOption>() {
        public CatalogPriceFilterOption createFromParcel(Parcel source) {
            return new CatalogPriceFilterOption(source);
        }

        public CatalogPriceFilterOption[] newArray(int size) {
            return new CatalogPriceFilterOption[size];
        }
    };
}
