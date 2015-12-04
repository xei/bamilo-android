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
 * @date 2015/09/04
 *
 */
public class CatalogColorFilterOption extends CatalogCheckFilterOption {

    public CatalogColorFilterOption(){}

    public CatalogColorFilterOption(JSONObject jsonObject) throws JSONException {
        this();
        initialize(jsonObject);
    }

    private String hexValue;

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        hexValue = jsonObject.getString(RestConstants.HEX_VALUE);
        return super.initialize(jsonObject);
    }

    public String getHexValue() {
        return hexValue;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeString(this.hexValue);
    }

    protected CatalogColorFilterOption(Parcel in) {
        super(in);
        this.hexValue = in.readString();
    }

    public static final Creator<CatalogColorFilterOption> CREATOR = new Creator<CatalogColorFilterOption>() {
        public CatalogColorFilterOption createFromParcel(Parcel source) {
            return new CatalogColorFilterOption(source);
        }

        public CatalogColorFilterOption[] newArray(int size) {
            return new CatalogColorFilterOption[size];
        }
    };
}
