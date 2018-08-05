package com.mobile.service.objects.checkout;

import com.mobile.service.objects.IJSONSerializable;
import com.mobile.service.objects.RequiredJson;
import com.mobile.service.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

public class DeliveryType implements IJSONSerializable {

    private String dropShipDescription;


    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        try {
            dropShipDescription = jsonObject.getString(RestConstants.DROPSHIP_DESC);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public JSONObject toJSON() {
        return null;
    }

    @Override
    public int getRequiredJson() {
        return RequiredJson.OBJECT_DATA;
    }


    public String getDropShipDescription() {
        return dropShipDescription;
    }

    public void setDropShipDescription(String dropShipDescription) {
        this.dropShipDescription = dropShipDescription;
    }
}
