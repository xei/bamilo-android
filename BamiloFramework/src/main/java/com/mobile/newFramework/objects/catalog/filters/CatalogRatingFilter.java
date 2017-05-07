package com.mobile.newFramework.objects.catalog.filters;

import android.os.Parcel;

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
        JSONObject starsSize = jsonObject.getJSONObject(RestConstants.STARS_SIZE);
        min = starsSize.getInt(RestConstants.MIN);
        max = starsSize.getInt(RestConstants.MAX);
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(this.min);
        dest.writeInt(this.max);
    }

    protected CatalogRatingFilter(Parcel in) {
        super(in);
        this.min = in.readInt();
        this.max = in.readInt();
    }

    public static final Creator<CatalogRatingFilter> CREATOR = new Creator<CatalogRatingFilter>() {
        public CatalogRatingFilter createFromParcel(Parcel source) {
            return new CatalogRatingFilter(source);
        }

        public CatalogRatingFilter[] newArray(int size) {
            return new CatalogRatingFilter[size];
        }
    };
}
