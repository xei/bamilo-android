package com.mobile.newFramework.objects.catalog.filters;

import android.os.Parcel;
import android.os.Parcelable;

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
public class CatalogPriceFilterOption implements IJSONSerializable, SingleFilterOptionInterface, Parcelable {

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
    public int getRequiredJson() {
        return RequiredJson.NONE;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.min);
        dest.writeInt(this.max);
        dest.writeInt(this.interval);
        dest.writeInt(this.rangeMin);
        dest.writeInt(this.rangeMax);
        dest.writeParcelable(this.checkBoxOption, flags);
    }

    protected CatalogPriceFilterOption(Parcel in) {
        this.min = in.readInt();
        this.max = in.readInt();
        this.interval = in.readInt();
        this.rangeMin = in.readInt();
        this.rangeMax = in.readInt();
        this.checkBoxOption = in.readParcelable(PriceFilterCheckBoxOption.class.getClassLoader());
    }

    public static final Parcelable.Creator<CatalogPriceFilterOption> CREATOR = new Parcelable.Creator<CatalogPriceFilterOption>() {
        public CatalogPriceFilterOption createFromParcel(Parcel source) {
            return new CatalogPriceFilterOption(source);
        }

        public CatalogPriceFilterOption[] newArray(int size) {
            return new CatalogPriceFilterOption[size];
        }
    };
}
