package com.mobile.newFramework.objects.category;

import com.mobile.framework.rest.RestConstants;
import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Categories extends ArrayList<Category> implements IJSONSerializable {

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        JSONArray categoriesArray = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);
        int length = categoriesArray.length();
        // For each child
        for (int i = 0; i < length; ++i) {
            // Get category
            JSONObject categoryObject = categoriesArray.getJSONObject(i);
            Category category = new Category();
            category.initialize(categoryObject);
            this.add(category);
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
