package com.bamilo.android.framework.service.objects.catalog.filters;

import android.os.Parcel;
import android.os.Parcelable;

import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by rsoares on 9/16/15.
 */
public class PriceFilterCheckBoxOption implements IJSONSerializable, SingleFilterOptionInterface {

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
        name = jsonObject.optString(RestConstants.NAME);
        type = jsonObject.optString(RestConstants.TYPE);
        selected = jsonObject.optBoolean(RestConstants.SELECTED);
        return true;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.NONE;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.id);
        dest.writeString(this.label);
        dest.writeString(this.name);
        dest.writeString(this.type);
        dest.writeByte(selected ? (byte) 1 : (byte) 0);
    }

    protected PriceFilterCheckBoxOption(Parcel in) {
        this.id = in.readString();
        this.label = in.readString();
        this.name = in.readString();
        this.type = in.readString();
        this.selected = in.readByte() != 0;
    }

    public static final Parcelable.Creator<PriceFilterCheckBoxOption> CREATOR = new Parcelable.Creator<PriceFilterCheckBoxOption>() {
        public PriceFilterCheckBoxOption createFromParcel(Parcel source) {
            return new PriceFilterCheckBoxOption(source);
        }

        public PriceFilterCheckBoxOption[] newArray(int size) {
            return new PriceFilterCheckBoxOption[size];
        }
    };
}
