package com.bamilo.android.framework.service.objects.orders;

import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 10/28/2017.
 */

public class Package implements IJSONSerializable {
    private String title;
    private String calculatedDeliveryTime;
    private List<PackageItem> packageItems;

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        title = jsonObject.optString(RestConstants.TITLE);
        if (!jsonObject.isNull(RestConstants.DELIVERY_TIME)) {
            calculatedDeliveryTime = jsonObject.optString(RestConstants.DELIVERY_TIME);
        }

        JSONArray itemsArray = jsonObject.getJSONArray(RestConstants.PRODUCTS);
        if (itemsArray != null) {
            packageItems = new ArrayList<>();
            for (int i = 0; i < itemsArray.length(); i++) {
                PackageItem tempItem = new PackageItem();
                tempItem.initialize(itemsArray.getJSONObject(i));
                packageItems.add(tempItem);
            }
        }
        return false;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.NONE;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCalculatedDeliveryTime() {
        return calculatedDeliveryTime;
    }

    public void setCalculatedDeliveryTime(String calculatedDeliveryTime) {
        this.calculatedDeliveryTime = calculatedDeliveryTime;
    }

    public List<PackageItem> getPackageItems() {
        return packageItems;
    }

    public void setPackageItems(List<PackageItem> packageItems) {
        this.packageItems = packageItems;
    }
}
