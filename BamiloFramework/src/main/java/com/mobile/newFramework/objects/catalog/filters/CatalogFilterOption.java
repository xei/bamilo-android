package com.mobile.newFramework.objects.catalog.filters;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
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
 * @date 2015/09/11
 *
 */
public abstract class CatalogFilterOption implements IJSONSerializable, Parcelable {

    protected int totalProducts;

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        totalProducts = jsonObject.getInt(RestConstants.TOTAL_PRODUCTS);
        return true;
    }

    public int getTotalProducts() {
        return totalProducts;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.totalProducts);
    }

    public CatalogFilterOption() {
    }

    protected CatalogFilterOption(Parcel in) {
        this.totalProducts = in.readInt();
    }

//    public static final Parcelable.Creator<CatalogFilterOption> CREATOR = new Parcelable.Creator<CatalogFilterOption>() {
//        public CatalogFilterOption createFromParcel(Parcel source) {
//            return new CatalogFilterOption(source);
//        }
//
//        public CatalogFilterOption[] newArray(int size) {
//            return new CatalogFilterOption[size];
//        }
//    };
}
