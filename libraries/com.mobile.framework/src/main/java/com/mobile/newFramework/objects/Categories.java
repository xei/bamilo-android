package com.mobile.newFramework.objects;

import com.mobile.framework.rest.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by rsoares on 5/26/15.
 */
public class Categories extends ArrayList<Category> implements IJSONSerializable{

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {

        JSONArray categoriesArray = jsonObject.getJSONArray(RestConstants.JSON_DATA_TAG);
        int categoriesArrayLenght = categoriesArray.length();

        // For each child
        for (int i = 0; i < categoriesArrayLenght; ++i) {
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
