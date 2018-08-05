package com.mobile.service.objects.checkout;

import com.mobile.service.objects.IJSONSerializable;
import com.mobile.service.objects.RequiredJson;
import com.mobile.service.objects.cart.PurchaseCartItem;
import com.mobile.service.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by mohsen on 3/12/18.
 */

public class CartPackage implements IJSONSerializable {
    private String title;
    private String deliveryTime;
    private List<PurchaseCartItem> products;

    private DeliveryType deliveryType;

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        if (jsonObject != null) {
            title = jsonObject.optString(RestConstants.TITLE);
            deliveryTime = jsonObject.optString(RestConstants.DELIVERY_TIME);

            deliveryType = new DeliveryType();
            deliveryType.initialize(jsonObject.optJSONObject(RestConstants.DELIVERY_TYPE));

            JSONArray productsArray = jsonObject.optJSONArray(RestConstants.PRODUCTS);
            products = new ArrayList<>();
            if (productsArray != null && productsArray.length() > 0) {
                for (int i = 0; i < productsArray.length(); i++) {
                    PurchaseCartItem cartItem = new PurchaseCartItem();
                    cartItem.initialize(productsArray.optJSONObject(i));
                    products.add(cartItem);
                }
            }
            return true;
        }
        return false;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.NONE;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDeliveryTime() {
        return deliveryTime;
    }

    public void setDeliveryTime(String deliveryTime) {
        this.deliveryTime = deliveryTime;
    }

    public List<PurchaseCartItem> getProducts() {
        return products;
    }

    public void setProducts(List<PurchaseCartItem> products) {
        this.products = products;
    }

    public DeliveryType getDeliveryType() {
        return deliveryType;
    }

    public void setDeliveryType(DeliveryType deliveryType) {
        this.deliveryType = deliveryType;
    }

}
