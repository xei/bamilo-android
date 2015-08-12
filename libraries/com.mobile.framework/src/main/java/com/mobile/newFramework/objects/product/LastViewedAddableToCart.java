package com.mobile.newFramework.objects.product;


import android.database.Cursor;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Object that contains one of the last viewed products<br>
 * Refactored when created Screen to list all Last Viewed
 *
 * @author Manuel Silva
 * @modified Paulo Carvalho
 */
public class LastViewedAddableToCart extends AddableToCart implements IJSONSerializable {

    public LastViewedAddableToCart() {
        super();
    }

    public LastViewedAddableToCart(Cursor cursor, int index) {
        mSku = cursor.getString(index++);
        mName = cursor.getString(index++);
        cursor.getString(index++); // price as string
        mPrice = cursor.getDouble(index++);
        mPriceConverted = cursor.getDouble(index++);
        cursor.getString(index++); // url
        mImageUrl = cursor.getString(index++);
        mBrand = cursor.getString(index++);
        cursor.getString(index++); // special price as string
        mSpecialPrice = cursor.getDouble(index++);
        mSpecialPriceConverted = cursor.getDouble(index++);
        mMaxSavingPercentage = cursor.getInt(index++);
        cursor.getInt(index++); // is new TODO
    }


    @Override
    public boolean initialize(JSONObject jsonObject) {

        try {

            mSizeGuideUrl = jsonObject.optString(RestConstants.JSON_SIZE_GUIDE_URL_TAG);

            ArrayList<String> images = new ArrayList<>();
            imageList.clear();
            String image = jsonObject.optString(RestConstants.JSON_IMAGE_URL_TAG);
            images.add(image);
            imageList = images;

            ArrayList<String> variations = new ArrayList<>();
            knownVariations.clear();
            String variationName = jsonObject.optString(RestConstants.JSON_VARIATION_NAME_TAG);
            variations.add(variationName);
            knownVariations = variations;


            simples.clear();
            JSONArray simpleArray = jsonObject.getJSONArray(RestConstants.JSON_SIMPLES_TAG);

            for (int i = 0; i < simpleArray.length(); ++i) {
                NewProductSimple simple = new NewProductSimple();
                JSONObject simpleObject = simpleArray.getJSONObject(i);
                simple.initialize(simpleObject);

//                if (!TextUtils.isEmpty(variationName)) {
//                    String variationValue = "";
//                    Iterator it = simple.getAttributes().entrySet().iterator();
//                    while (it.hasNext()) {
//                        Map.Entry pair = (Map.Entry) it.next();
//                        if (pair.getKey().toString().equals(RestConstants.JSON_VARIATION_VALUE_TAG)) {
//                            variationValue = pair.getValue().toString();
//                        }
//                    }
//                    if (!TextUtils.isEmpty(variationValue)) {
//                        simple.getAttributes().put(variationName, variationValue);
//                    }
//                }

                simples.add(simple);
            }

            JSONObject variationsObject = jsonObject.optJSONObject(RestConstants.JSON_VARIATIONS_TAG);
            if (variationsObject == null)
                return true;

            @SuppressWarnings("rawtypes")
            Iterator iter = variationsObject.keys();
            while (iter.hasNext()) {
                String key = (String) iter.next();
                JSONObject variationObject = variationsObject.getJSONObject(key);
                Variation variation = new Variation();
                variation.initialize(key, variationObject);
                this.variations.add(variation);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

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

}
