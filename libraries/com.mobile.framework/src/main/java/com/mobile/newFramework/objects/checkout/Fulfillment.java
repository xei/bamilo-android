package com.mobile.newFramework.objects.checkout;

import android.os.Parcel;
import android.os.Parcelable;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.cart.PurchaseCartItem;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/09/24
 *
 */
public class Fulfillment implements IJSONSerializable, Parcelable {

    private List<PurchaseCartItem> products;
    private GlobalSeller globalSeller;

    public Fulfillment(){

        products = new ArrayList<>();
    }

    public Fulfillment(JSONObject jsonObject) throws JSONException {
        this();
        initialize(jsonObject);
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        JSONArray productsArray = jsonObject.getJSONArray(RestConstants.PRODUCTS);
        for(int i = 0; i<productsArray.length();i++){
            products.add(new PurchaseCartItem(productsArray.getJSONObject(i)));
        }

        JSONObject sellerEntityObject = jsonObject.getJSONObject(RestConstants.JSON_SELLER_TAG);
        globalSeller = new GlobalSeller(sellerEntityObject);
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


    public List<PurchaseCartItem> getProducts() {
        return products;
    }

    public GlobalSeller getGlobalSeller() {
        return globalSeller;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeTypedList(products);
        dest.writeParcelable(this.globalSeller, flags);
    }

    protected Fulfillment(Parcel in) {
        this.products = in.createTypedArrayList(PurchaseCartItem.CREATOR);
        this.globalSeller = in.readParcelable(GlobalSeller.class.getClassLoader());
    }

    public static final Creator<Fulfillment> CREATOR = new Creator<Fulfillment>() {
        public Fulfillment createFromParcel(Parcel source) {
            return new Fulfillment(source);
        }

        public Fulfillment[] newArray(int size) {
            return new Fulfillment[size];
        }
    };
}
