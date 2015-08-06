package com.mobile.newFramework.objects.product;

import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by spereira on 8/4/15.
 */
public class NewProductAddableToCart extends NewBaseProduct {

    private String mVariationName;
    private ArrayList<NewProductSimple> mSimples;

    public NewProductAddableToCart() {
        //...
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        // Base product
        super.initialize(jsonObject);
        // Variation name
        //mVariationName = jsonObject.getString(RestConstants.JSON_VARIATION_NAME_TAG);
        // Simples
        JSONArray simpleArray = jsonObject.getJSONArray(RestConstants.JSON_SIMPLES_TAG);
        int size = simpleArray.length();
        if (size > 0) {
            mSimples = new ArrayList<>();
            for (int i = 0; i < size; i++) {
                JSONObject simpleObject = simpleArray.getJSONObject(i);
                NewProductSimple simple = new NewProductSimple();
                if (simple.initialize(simpleObject)) {
                    mSimples.add(simple);
                }
            }
        }
        return true;
    }

//    public String getVariationName() {
//        return mVariationName;
//    }

    public ArrayList<NewProductSimple> getSimples() {
        return mSimples;
    }
}
