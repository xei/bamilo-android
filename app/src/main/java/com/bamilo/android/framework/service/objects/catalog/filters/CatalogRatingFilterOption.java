package com.bamilo.android.framework.service.objects.catalog.filters;

import android.os.Parcel;
import android.support.annotation.NonNull;

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
 * @date 2015/09/04
 *
 */
public class CatalogRatingFilterOption extends CatalogFilterOption implements MultiFilterOptionInterface {

    public CatalogRatingFilterOption(){}

    public CatalogRatingFilterOption(JSONObject jsonObject) throws JSONException {
        this();
        initialize(jsonObject);
    }

    protected String val;
    private int average;
    protected boolean selected;

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        val = jsonObject.getString(RestConstants.VAL);
        average = jsonObject.getInt(RestConstants.AVERAGE);
        selected = jsonObject.getBoolean(RestConstants.SELECTED);
        return super.initialize(jsonObject);
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.NONE;
    }

    @Override
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    @Override
    @NonNull
    public String getLabel() {
        return average+"";
    }

    @Override
    public boolean isSelected() {
        return selected;
    }

    @Override
    @NonNull
    public String getVal() {
        return val;
    }

    public int getAverage() {
        return average;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.val);
        dest.writeInt(this.average);
        dest.writeByte(selected ? (byte) 1 : (byte) 0);
    }

    protected CatalogRatingFilterOption(Parcel in) {
        super(in);
        this.val = in.readString();
        this.average = in.readInt();
        this.selected = in.readByte() != 0;
    }

    public static final Creator<CatalogRatingFilterOption> CREATOR = new Creator<CatalogRatingFilterOption>() {
        public CatalogRatingFilterOption createFromParcel(Parcel source) {
            return new CatalogRatingFilterOption(source);
        }

        public CatalogRatingFilterOption[] newArray(int size) {
            return new CatalogRatingFilterOption[size];
        }
    };
}
