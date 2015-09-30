package com.mobile.newFramework.objects.category;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

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
            // Add first level category
            add(category);
            // Case has sub categories put the same level of the parent
            if (category.hasChildren()) {
                // Append all sub categories to the first level
                addAll(category.getChildren());
                // Mark the parent category has a section
                category.markAsSection();
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
