package com.mobile.framework.objects;

import android.text.TextUtils;

import com.mobile.framework.rest.RestConstants;
import com.mobile.framework.utils.CurrencyFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;

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


    @Override
    public boolean initialize(JSONObject jsonObject) {

        try {
            sku = jsonObject.getString(RestConstants.JSON_SKU_TAG);
            name = jsonObject.getString(RestConstants.JSON_PROD_NAME_TAG);
            brand = jsonObject.getString(RestConstants.JSON_BRAND_TAG);
            url = jsonObject.getString(RestConstants.JSON_URL_TAG);

            String priceJSON = jsonObject.getString(RestConstants.JSON_PRICE_TAG);
            if (!CurrencyFormatter.isNumber(priceJSON)) {
                throw new JSONException("Price is not a number!");
            }
            priceDouble = Double.parseDouble(priceJSON);
            price = priceJSON;
            priceConverted = jsonObject.optDouble(RestConstants.JSON_PRICE_CONVERTED_TAG, 0d);

            String specialPriceJSON = jsonObject.optString(RestConstants.JSON_SPECIAL_PRICE_TAG);
            if (!CurrencyFormatter.isNumber(specialPriceJSON)) {
                specialPriceJSON = priceJSON;
            }
            specialPriceDouble = Double.parseDouble(specialPriceJSON);

            specialPrice = specialPriceJSON;
            specialPriceConverted = jsonObject.optDouble(RestConstants.JSON_SPECIAL_PRICE_CONVERTED_TAG, 0d);

            String maxSavingPercentageJSON = jsonObject.optString(RestConstants.JSON_MAX_SAVING_PERCENTAGE_TAG);
            if (CurrencyFormatter.isNumber(maxSavingPercentageJSON)) {
                maxSavingPercentage = Double.parseDouble(maxSavingPercentageJSON);
            } else {
                maxSavingPercentage = 0d;
            }

            isNew = jsonObject.optBoolean(RestConstants.JSON_IS_NEW_TAG, false);

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
                ProductSimple simple = new ProductSimple();
                JSONObject simpleObject = simpleArray.getJSONObject(i);
                simple.initialize(simpleObject);

                if (!TextUtils.isEmpty(variationName)) {
                    String variationValue = "";
                    Iterator it = simple.getAttributes().entrySet().iterator();
                    while (it.hasNext()) {
                        Map.Entry pair = (Map.Entry) it.next();
                        if (pair.getKey().toString().equals(RestConstants.JSON_VARIATION_VALUE_TAG)) {
                            variationValue = pair.getValue().toString();
                        }
                    }
                    if (!TextUtils.isEmpty(variationValue)) {
                        simple.getAttributes().put(variationName, variationValue);
                    }
                }

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

}
