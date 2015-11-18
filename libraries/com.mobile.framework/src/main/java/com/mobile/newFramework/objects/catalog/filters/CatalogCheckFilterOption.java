package com.mobile.newFramework.objects.catalog.filters;

import android.os.Parcel;
import android.support.annotation.NonNull;

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
public class CatalogCheckFilterOption extends CatalogFilterOption implements MultiFilterOptionInterface {

    public CatalogCheckFilterOption(){}

    public CatalogCheckFilterOption(JSONObject jsonObject) throws JSONException {
        this();
        initialize(jsonObject);
    }

    protected String id;
    protected String label;
    protected String val;

    protected boolean selected;

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        id = jsonObject.optString(RestConstants.ID);
        label = jsonObject.getString(RestConstants.LABEL);
        val = jsonObject.getString(RestConstants.JSON_VAL_TAG);
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
        return label;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.id);
        dest.writeString(this.label);
        dest.writeString(this.val);
        dest.writeByte(selected ? (byte) 1 : (byte) 0);
    }

    protected CatalogCheckFilterOption(Parcel in) {
        super(in);
        this.id = in.readString();
        this.label = in.readString();
        this.val = in.readString();
        this.selected = in.readByte() != 0;
    }

    public static final Creator<CatalogCheckFilterOption> CREATOR = new Creator<CatalogCheckFilterOption>() {
        public CatalogCheckFilterOption createFromParcel(Parcel source) {
            return new CatalogCheckFilterOption(source);
        }

        public CatalogCheckFilterOption[] newArray(int size) {
            return new CatalogCheckFilterOption[size];
        }
    };
}
