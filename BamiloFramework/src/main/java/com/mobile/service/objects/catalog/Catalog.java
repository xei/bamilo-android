package com.mobile.service.objects.catalog;

import com.mobile.service.objects.IJSONSerializable;
import com.mobile.service.objects.RequiredJson;
import com.mobile.service.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Class used to represent a catalog response.
 */
public class Catalog implements IJSONSerializable{

    private CatalogPage catalogPage;
    private FeaturedBox featuredBox;


    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        if (jsonObject.has(RestConstants.RESULTS)) {
            catalogPage = new CatalogPage(jsonObject);
        } else {
            featuredBox = new FeaturedBox(jsonObject);
        }
        return true;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.METADATA;
    }

    public CatalogPage getCatalogPage() {
        return catalogPage;
    }

    public FeaturedBox getFeaturedBox() {
        return featuredBox;
    }
}
