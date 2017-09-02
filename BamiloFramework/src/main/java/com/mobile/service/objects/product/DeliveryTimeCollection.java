package com.mobile.service.objects.product;

import com.mobile.service.objects.IJSONSerializable;
import com.mobile.service.objects.RequiredJson;
import com.mobile.service.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class DeliveryTimeCollection implements IJSONSerializable {
    private List<DeliveryTime> deliveryTimes;
    private int regionId, cityId;

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        JSONArray deliveryTimesArray = jsonObject.getJSONArray(RestConstants.DATA);
        deliveryTimes = new ArrayList<>();
        regionId = jsonObject.optInt(RestConstants.REGION);
        cityId = jsonObject.optInt(RestConstants.CITY_ID);
        for (int i = 0; i < deliveryTimesArray.length(); i++) {
            DeliveryTime deliveryTime = new DeliveryTime();
            deliveryTime.initialize(deliveryTimesArray.getJSONObject(i));
            deliveryTimes.add(deliveryTime);
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

    public List<DeliveryTime> getDeliveryTimes() {
        return deliveryTimes;
    }

    public void setDeliveryTimes(List<DeliveryTime> deliveryTimes) {
        this.deliveryTimes = deliveryTimes;
    }

    public int getRegionId() {
        return regionId;
    }

    public void setRegionId(int regionId) {
        this.regionId = regionId;
    }

    public int getCityId() {
        return cityId;
    }

    public void setCityId(int cityId) {
        this.cityId = cityId;
    }
}
