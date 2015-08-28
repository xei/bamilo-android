package com.mobile.newFramework.objects.product;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.product.pojo.ProductMultiple;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by spereira on 8/4/15.
 */
public class WishList extends ArrayList<ProductMultiple> implements IJSONSerializable {

    /**
     * Empty constructor
     */
    public WishList() {
        super();
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        // Products
        JSONArray productsArray = jsonObject.getJSONArray(RestConstants.JSON_PRODUCTS_TAG);
        int size = productsArray.length();
        if (size > 0) {
            for (int i = 0; i < size; i++) {
                JSONObject simpleObject = productsArray.getJSONObject(i);
                ProductMultiple product = new ProductMultiple();
                if (product.initialize(simpleObject)) {
                    add(product);
                }
            }
        }
        return true;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return RequiredJson.METADATA;
    }

}
