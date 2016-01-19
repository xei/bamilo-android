package com.mobile.newFramework.objects.catalog.filters;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
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
public abstract class CatalogFilter implements IJSONSerializable, Parcelable {

    public static final String PRICE = "price";
    public static final String COLOR = "color_family";
    public static final String RATING = "rating";

    public CatalogFilter(){
    }

    public CatalogFilter(JSONObject jsonObject) throws JSONException {
        this();
        initialize(jsonObject);
    }

    protected String id;
    protected boolean multi;
    protected String name;
    protected String filterSeparator;
    protected Class optionType;

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        id = jsonObject.optString(RestConstants.ID);
        name = jsonObject.getString(RestConstants.NAME);
        multi = jsonObject.getBoolean(RestConstants.MULTI);
        filterSeparator = multi ? jsonObject.getString(RestConstants.FILTER_SEPARATOR) : jsonObject.optString(RestConstants.FILTER_SEPARATOR);

        if(jsonObject.has(RestConstants.FIELDS)) {
            parseFields(jsonObject.optJSONArray(RestConstants.FIELDS));
        } else if(jsonObject.has(RestConstants.SPECIAL_PRICE)){
            parseFields(jsonObject.optJSONObject(RestConstants.SPECIAL_PRICE));
        }

        setOptionType(id);

        return true;
    }

    protected abstract void setOptionType(String id);

    protected abstract ContentValues getValues();

    public abstract boolean hasAppliedFilters();

    protected void parseFields(JSONArray fieldsArray) throws JSONException {
    }

    protected void parseFields(JSONObject fieldsArray) throws JSONException {
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.NONE;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isMulti() {
        return multi;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Class getOptionType() {
        return optionType;
    }

    public abstract void cleanFilter();

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeByte(multi ? (byte) 1 : (byte) 0);
        dest.writeString(this.name);
        dest.writeString(this.filterSeparator);
        dest.writeSerializable(this.optionType);
    }

    protected CatalogFilter(Parcel in) {
        this.id = in.readString();
        this.multi = in.readByte() != 0;
        this.name = in.readString();
        this.filterSeparator = in.readString();
        this.optionType = (Class) in.readSerializable();
    }

}
