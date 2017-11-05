package com.mobile.service.objects.orders;

import com.mobile.service.objects.IJSONSerializable;
import com.mobile.service.objects.RequiredJson;
import com.mobile.service.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 10/28/2017.
 */

public class PackageItem implements IJSONSerializable {
    private String sku;
    private String name;
    private String brand;
    private String seller;
    private int quantity;
    private String image;
    private long price;
    private String calculatedDeliveryTime;
    private String color; // optional property
    private String size; // optional property
    private List<History> histories;

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        if (!jsonObject.isNull(RestConstants.SKU)) {
            sku = jsonObject.optString(RestConstants.SKU);
        }
        name = jsonObject.optString(RestConstants.NAME);
        brand = jsonObject.optString(RestConstants.BRAND);
        seller = jsonObject.optString(RestConstants.SELLER);
        quantity = jsonObject.optInt(RestConstants.QUANTITY);
        image = jsonObject.optString(RestConstants.IMAGE);
        price = jsonObject.optLong(RestConstants.PRICE);
        calculatedDeliveryTime = jsonObject.optString(RestConstants.CALCULATED_DELIVERY_TIME);

        JSONObject filters = jsonObject.optJSONObject(RestConstants.FILTERS);
        if (filters != null) {
            color = filters.optString(RestConstants.COLOR);
            size = filters.optString(RestConstants.SIZE);
        }

        JSONArray historyArray = jsonObject.optJSONArray(RestConstants.HISTORIES);
        if (historyArray != null) {
            histories = new ArrayList<>();
            for (int i = 0; i < historyArray.length(); i++) {
                History tempHistory = new History();
                tempHistory.initialize(historyArray.getJSONObject(i));
                histories.add(tempHistory);
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

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public long getPrice() {
        return price;
    }

    public void setPrice(long price) {
        this.price = price;
    }

    public String getCalculatedDeliveryTime() {
        return calculatedDeliveryTime;
    }

    public void setCalculatedDeliveryTime(String calculatedDeliveryTime) {
        this.calculatedDeliveryTime = calculatedDeliveryTime;
    }

    public List<History> getHistories() {
        return histories;
    }

    public void setHistories(List<History> histories) {
        this.histories = histories;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public static class History implements IJSONSerializable {
        private String key;
        private String name;
        private String status;
        private int progress;
        private int multiplier;
        private String date;

        @Override
        public boolean initialize(JSONObject jsonObject) throws JSONException {
            key = jsonObject.optString(RestConstants.KEY);
            name = jsonObject.optString(RestConstants.NAME);
            status = jsonObject.optString(RestConstants.STATUS);
            progress = jsonObject.getInt(RestConstants.PROGRESS);
            multiplier = jsonObject.optInt(RestConstants.MULTIPLIER);
            date = jsonObject.optString(RestConstants.DATE);
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

        public String getKey() {
            return key;
        }

        public void setKey(String key) {
            this.key = key;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getStatus() {
            return status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public int getProgress() {
            return progress;
        }

        public void setProgress(int progress) {
            this.progress = progress;
        }

        public int getMultiplier() {
            return multiplier;
        }

        public void setMultiplier(int multiplier) {
            this.multiplier = multiplier;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }
    }
}
