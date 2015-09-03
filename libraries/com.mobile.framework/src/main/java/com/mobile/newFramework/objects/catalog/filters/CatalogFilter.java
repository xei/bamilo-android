package com.mobile.newFramework.objects.catalog.filters;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.objects.catalog.Catalog;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by rsoares on 9/3/15.
 */
public class CatalogFilter implements IJSONSerializable {

    public static final String PRICE = "price";
    public static final String COLOR = "color_family";
    public static final String RATING = "rating";

    public CatalogFilter(){
        filterOptions = new ArrayList<>();
    }

    public CatalogFilter(JSONObject jsonObject) throws JSONException {
        this();
        initialize(jsonObject);
    }

    protected String id;
    protected boolean multi;
    protected String name;
    protected String filterSeparator;
    protected Class type;
    protected ArrayList<FilterOptionService> filterOptions;

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getString(RestConstants.ID);
        name = jsonObject.getString(RestConstants.JSON_NAME_TAG);
        multi = jsonObject.getBoolean(RestConstants.JSON_MULTI);
        filterSeparator = multi ? jsonObject.getString(RestConstants.JSON_FILTER_SEPARATOR) : jsonObject.optString(RestConstants.JSON_FILTER_SEPARATOR);

        setType(id);
        if(type != CatalogPriceFilterOption.class) {
            JSONArray optionsArray = jsonObject.getJSONArray(RestConstants.JSON_OPTION_TAG);

            for (int i = 0; i < optionsArray.length(); i++) {
                filterOptions.add(getFilterOptionType(optionsArray.getJSONObject(i)));
            }
        } else {
            filterOptions.add(getFilterOptionType(jsonObject.getJSONObject(RestConstants.JSON_OPTION_TAG)));
        }
        return true;
    }

    protected void setType(String id) {
        if(id.equals(PRICE)){
            type = CatalogPriceFilterOption.class;
//            return new CatalogPriceFilterOption(jsonObject);
        } else if(id.equals(COLOR)){
            type = CatalogColorFilterOption.class;
//            return new CatalogColorFilterOption(jsonObject);
        } else if(id.equals(RATING)){
            type = CatalogRatingFilterOption.class;
//            return  new CatalogRatingFilterOption(jsonObject);
        } else {
            type = CatalogCheckFilterOption.class;
//            return new CatalogCheckFilterOption(jsonObject);
        }
    }

    protected FilterOptionService getFilterOptionType(JSONObject jsonObject) throws JSONException {
        try {
            FilterOptionService object = (FilterOptionService)type.newInstance();
            if(object instanceof IJSONSerializable){
                ((IJSONSerializable) object).initialize(jsonObject);
            }
            return object;
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return null;
    }
}
