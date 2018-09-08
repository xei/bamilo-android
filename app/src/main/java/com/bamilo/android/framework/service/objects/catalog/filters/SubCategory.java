package com.bamilo.android.framework.service.objects.catalog.filters;

import android.os.Parcel;
import android.os.Parcelable;
import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;
import org.json.JSONObject;

/**
 * Created by Farshid since 6/2/2018. contact farshidabazari@gmail.com
 */
public class SubCategory implements IJSONSerializable, Parcelable {

    private int id;
    private int total_products;

    private String name;
    private String url_slug;

    private boolean isSelected;

    public SubCategory() {
        super();
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getUrl_slug() {
        return url_slug;
    }

    public int getTotal_products() {
        return total_products;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public SubCategory(JSONObject jsonObject) {
        initialize(jsonObject);
    }

    public SubCategory(Parcel in) {
        id = in.readInt();
        total_products = in.readInt();
        name = in.readString();
        url_slug = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(url_slug);
        dest.writeInt(id);
        dest.writeInt(total_products);
    }

    @Override
    public boolean initialize(JSONObject jsonObject) {
        id = jsonObject.optInt("id");
        total_products = jsonObject.optInt("total_products");
        name = jsonObject.optString("name");
        url_slug = jsonObject.optString("url_slug");
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

    public static final Creator<SubCategory> CREATOR = new Creator<SubCategory>() {
        @Override
        public SubCategory createFromParcel(Parcel in) {
            return new SubCategory(in);
        }

        @Override
        public SubCategory[] newArray(int size) {
            return new SubCategory[size];
        }
    };
}