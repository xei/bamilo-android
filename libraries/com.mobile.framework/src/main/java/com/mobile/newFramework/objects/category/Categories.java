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
            // Remove first childs level, and put it at the same level of the parent, following it
            if(category.hasChildren()){
                ArrayList<Category> childs = category.getChildren();
                // Mark the parent category has a section
                category.markAsSection();
                category.setChildren(null);
                // Add first level category
                this.add(category);
                // Append all sub categories to the first level
                this.addAll(childs);
            } else {
                category.markAsSection();
                this.add(category);
            }

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
}
