package com.bamilo.android.framework.service.objects.catalog;

import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.pojo.RestConstants;

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
