//package com.mobile.framework.objects;
//
//import com.mobile.framework.output.Print;
//import com.mobile.newFramework.pojo.JsonConstants;
//import com.mobile.framework.utils.CurrencyFormatter;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.util.ArrayList;
//
///**
// * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
// *
// * Unauthorized copying of this file, via any medium is strictly prohibited
// * Proprietary and confidential.
// *
// * @author ricardo.soares
// * @version 1.0
// * @date 2015/03/04
// */
//public class RelatedProduct extends LastViewed {
//
//    private ArrayList<String> categories;
//
//    public RelatedProduct(){
//        super();
//        categories = new ArrayList<String>();
//    }
//
//    public boolean initialize(JSONObject relatedProductJsonObject){
//        try {
//            this.setSku(relatedProductJsonObject.getString(JsonConstants.JSON_SKU_TAG));
//            this.setImageUrl(relatedProductJsonObject.getString(JsonConstants.JSON_IMAGE_TAG));
//            this.setBrand(relatedProductJsonObject.getString(JsonConstants.JSON_BRAND_TAG));
//            this.setName(relatedProductJsonObject.getString(JsonConstants.JSON_PROD_NAME_TAG));
//            this.setUrl(relatedProductJsonObject.getString(JsonConstants.JSON_PROD_URL_TAG));
//
//            String priceJson = relatedProductJsonObject.getString(JsonConstants.JSON_PRICE_TAG);
//            if (!CurrencyFormatter.isNumber(priceJson)) {
//                throw new JSONException("Price is not a number!");
//            }
//            setPriceAsDouble(Double.parseDouble(priceJson));
//            setPrice(priceJson);
//
//            double specialPrice = relatedProductJsonObject.optDouble(JsonConstants.JSON_SPECIAL_PRICE_TAG, 0d);
//            setSpecialPriceDouble(specialPrice);
//            setSpecialPrice(String.valueOf(specialPrice));
//
//            String priceEuroConvertedJson = relatedProductJsonObject.getString(JsonConstants.JSON_PRICE_CONVERTED_TAG);
//            if (!CurrencyFormatter.isNumber(priceEuroConvertedJson)) {
//                throw new JSONException("Price converted is not a number!");
//            }
//            setPriceConverted(Double.parseDouble(priceEuroConvertedJson));
//
//            double specialPriceEuroConverted = relatedProductJsonObject.optDouble(JsonConstants.JSON_SPECIAL_PRICE_CONVERTED_TAG);
//            setSpecialPriceConverted(specialPriceEuroConverted);
//
//
//            JSONArray categoriesJsonArray = relatedProductJsonObject.getJSONArray(JsonConstants.JSON_CATEGORIES_TAG);
//            if(categoriesJsonArray !=null){
//                for(int i = 0; i< categoriesJsonArray.length();i++){
//                    getCategories().add(categoriesJsonArray.getString(i));
//                }
//            }
//
//        } catch (JSONException e) {
//            Print.e(TAG, "Error initializing the related product", e);
//            return false;
//        }
//        return true;
//    }
//
//    public ArrayList<String> getCategories() {
//        return categories;
//    }
//}
