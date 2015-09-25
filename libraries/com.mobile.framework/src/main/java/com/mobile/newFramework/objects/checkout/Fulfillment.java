package com.mobile.newFramework.objects.checkout;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.cart.PurchaseCartItem;
import com.mobile.newFramework.objects.product.pojo.ProductSimple;
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
public class Fulfillment implements IJSONSerializable{

    private String sellerName;
    private boolean isGlobal;
    private String deliveryTime;

    private List<PurchaseCartItem> products;

    public Fulfillment(){
        sellerName = "Jumia";
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

        JSONObject sellerEntityObject = jsonObject.getJSONObject(RestConstants.JSON_SELLER_ENTITY);
        sellerName = sellerEntityObject.optString(RestConstants.JSON_NAME_TAG);
        isGlobal = sellerEntityObject.getBoolean(RestConstants.JSON_IS_GLOBAL);
        deliveryTime = sellerEntityObject.getString(RestConstants.JSON_DELIVERY_TIME);
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

    public String getSellerName() {
        return sellerName;
    }

    public boolean isGlobal() {
        return isGlobal;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public List<PurchaseCartItem> getProducts() {
        return products;
    }
}
