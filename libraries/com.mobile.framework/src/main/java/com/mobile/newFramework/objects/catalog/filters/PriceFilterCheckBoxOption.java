package com.mobile.newFramework.objects.catalog.filters;

import com.mobile.newFramework.objects.IJSONSerializable;
import com.mobile.newFramework.objects.RequiredJson;
import com.mobile.newFramework.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rsoares on 9/16/15.
 */
public class PriceFilterCheckBoxOption implements IJSONSerializable, SingleFilterOptionInterface{

    private String id;
    private String label;
    private String name;
    private String type;
    private boolean selected;

    public PriceFilterCheckBoxOption(){}

    public PriceFilterCheckBoxOption(JSONObject jsonObject) throws JSONException {
        this();
        initialize(jsonObject);
    }

    PriceFilterCheckBoxOption(PriceFilterCheckBoxOption checkBoxOption){
        this.id = checkBoxOption.id;
        this.label = checkBoxOption.label;
        this.name = checkBoxOption.name;
        this.type = checkBoxOption.type;
        this.selected = checkBoxOption.selected;
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        id = jsonObject.getString(RestConstants.ID);
        label = jsonObject.getString(RestConstants.LABEL);
        name = jsonObject.getString(RestConstants.JSON_NAME_TAG);
        type = jsonObject.optString(RestConstants.TYPE);
        return true;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public RequiredJson getRequiredJson() {
        return null;
    }

    public String getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public String getName() {
        return name;
    }

    @Override
    public SingleFilterOptionInterface clone() {
        return new PriceFilterCheckBoxOption(this);
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean isSelected) {
        this.selected = isSelected;
    }
}
