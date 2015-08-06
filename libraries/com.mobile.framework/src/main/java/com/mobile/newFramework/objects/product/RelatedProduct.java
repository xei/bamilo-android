package com.mobile.newFramework.objects.product;

import com.mobile.newFramework.pojo.RestConstants;
import com.mobile.newFramework.utils.output.Print;
import com.mobile.newFramework.utils.shop.CurrencyFormatter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 * @author ricardo.soares
 * @version 1.0
 * @date 2015/03/04
 */
public class RelatedProduct extends LastViewed {

    private String categories;

    public RelatedProduct(){
        super();
    }

    public boolean initialize(JSONObject relatedProductJsonObject){
        try {
            this.setSku(relatedProductJsonObject.getString(RestConstants.JSON_SKU_TAG));
            this.setImageUrl(relatedProductJsonObject.getString(RestConstants.JSON_IMAGE_TAG));
            this.setBrand(relatedProductJsonObject.getString(RestConstants.JSON_BRAND_TAG));
            this.setName(relatedProductJsonObject.getString(RestConstants.JSON_PROD_NAME_TAG));
            this.setUrl(relatedProductJsonObject.getString(RestConstants.JSON_PROD_URL_TAG));

            String priceJson = relatedProductJsonObject.getString(RestConstants.JSON_PRICE_TAG);
            if (!CurrencyFormatter.isNumber(priceJson)) {
                throw new JSONException("Price is not a number!");
            }
            setPriceAsDouble(Double.parseDouble(priceJson));
            setPrice(priceJson);

            double specialPrice = relatedProductJsonObject.optDouble(RestConstants.JSON_SPECIAL_PRICE_TAG, 0d);
            setSpecialPriceDouble(specialPrice);
            setSpecialPrice(String.valueOf(specialPrice));

            String priceEuroConvertedJson = relatedProductJsonObject.getString(RestConstants.JSON_PRICE_CONVERTED_TAG);
            if (!CurrencyFormatter.isNumber(priceEuroConvertedJson)) {
                throw new JSONException("Price converted is not a number!");
            }
            setPriceConverted(Double.parseDouble(priceEuroConvertedJson));

            double specialPriceEuroConverted = relatedProductJsonObject.optDouble(RestConstants.JSON_SPECIAL_PRICE_CONVERTED_TAG);
            setSpecialPriceConverted(specialPriceEuroConverted);


            categories = relatedProductJsonObject.getString(RestConstants.JSON_CATEGORIES_TAG);

        } catch (JSONException e) {
            Print.e(TAG, "Error initializing the related product", e);
            return false;
        }
        return true;
    }

    public String getCategories() {
        return categories;
    }
}

