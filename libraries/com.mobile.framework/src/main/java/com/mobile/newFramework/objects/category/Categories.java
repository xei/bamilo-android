package com.mobile.newFramework.objects.category;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Categories extends ArrayList<Category> implements IJSONSerializable {

    private LinkedHashMap<Integer,Integer> mMainCategoryIndexMapping;

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        JSONArray categoriesArray = jsonObject.getJSONArray(RestConstants.DATA);
        int length = categoriesArray.length();
        mMainCategoryIndexMapping = new LinkedHashMap<>();
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
                mMainCategoryIndexMapping.put(i, this.size());
            } else {
                category.markAsSection();
                this.add(category);
                mMainCategoryIndexMapping.put(i, this.size());
            }

        }
        return true;
    }

    public LinkedHashMap<Integer, Integer> getMainCategoryIndexMapping() {
        return mMainCategoryIndexMapping;
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
