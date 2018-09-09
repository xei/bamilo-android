package com.bamilo.android.framework.service.objects.home.model;

import com.bamilo.android.framework.service.objects.IJSONSerializable;
import com.bamilo.android.framework.service.objects.RequiredJson;
import com.bamilo.android.framework.service.pojo.RestConstants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created on 10/22/2017.
 */

public class DailyDealComponent extends BaseComponent {
    private String backgroundColor;

    // Header
    private String title;
    private String titleTextColor;

    // More options
    private String moreOptionsTitle;
    private String moreOptionsTitleColor;
    private String moreOptionsTarget;

    // Counter
    private String counterTextColor;
    private long counterRemainingSeconds;
    private long initialTimeSeconds;

    private List<Product> products;

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        super.initialize(jsonObject);
        jsonObject = jsonObject.optJSONObject(RestConstants.DATA);
        backgroundColor = jsonObject.optString(RestConstants.BACKGROUND_COLOR);

        JSONObject headerObject = jsonObject.optJSONObject(RestConstants.HEADER);
        if (headerObject != null) {
            title = headerObject.optString(RestConstants.TITLE);
            titleTextColor = headerObject.optString(RestConstants.TEXT_COLOR);

            JSONObject moreOptionsObject = headerObject.optJSONObject(RestConstants.MORE_OPTION);
            if (moreOptionsObject != null) {
                moreOptionsTitle = moreOptionsObject.optString(RestConstants.TITLE);
                moreOptionsTarget = moreOptionsObject.getString(RestConstants.TARGET);
                moreOptionsTitleColor = moreOptionsObject.optString(RestConstants.TEXT_COLOR);
            }

            JSONObject counterObject = headerObject.optJSONObject(RestConstants.COUNTER);
            if (counterObject != null) {
                counterRemainingSeconds = counterObject.optLong(RestConstants.REMAINING_SECONDS, -1);
                initialTimeSeconds = System.currentTimeMillis() / 1000;
                counterTextColor = counterObject.optString(RestConstants.TEXT_COLOR);
            } else {
                counterRemainingSeconds = -1;
            }
        }

        JSONObject bodyObject = jsonObject.optJSONObject(RestConstants.BODY);
        if (bodyObject != null) {
            products = new ArrayList<>();
            JSONArray productsObject = bodyObject.optJSONArray(RestConstants.PRODUCTS);
            if (productsObject != null) {
                for (int i = 0; i < productsObject.length(); i++) {
                    Product temp = new Product();
                    temp.initialize(productsObject.optJSONObject(i));
                    products.add(temp);
                }
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
        return RequiredJson.OBJECT_DATA;
    }

    public String getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(String backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitleTextColor() {
        return titleTextColor;
    }

    public void setTitleTextColor(String titleTextColor) {
        this.titleTextColor = titleTextColor;
    }

    public String getMoreOptionsTitle() {
        return moreOptionsTitle;
    }

    public void setMoreOptionsTitle(String moreOptionsTitle) {
        this.moreOptionsTitle = moreOptionsTitle;
    }

    public String getMoreOptionsTitleColor() {
        return moreOptionsTitleColor;
    }

    public void setMoreOptionsTitleColor(String moreOptionsTitleColor) {
        this.moreOptionsTitleColor = moreOptionsTitleColor;
    }

    public String getMoreOptionsTarget() {
        return moreOptionsTarget;
    }

    public void setMoreOptionsTarget(String moreOptionsTarget) {
        this.moreOptionsTarget = moreOptionsTarget;
    }

    public String getCounterTextColor() {
        return counterTextColor;
    }

    public void setCounterTextColor(String counterTextColor) {
        this.counterTextColor = counterTextColor;
    }

    public long getRemainingSeconds() {
        return counterRemainingSeconds;
    }

    public void setRemainingSeconds(long remainingSeconds) {
        this.counterRemainingSeconds = remainingSeconds;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public long getInitialTimeSeconds() {
        return initialTimeSeconds;
    }

    public void setInitialTimeSeconds(long initialTimeSeconds) {
        this.initialTimeSeconds = initialTimeSeconds;
    }

    public static class Product implements IJSONSerializable {
        private String sku;
        private String name;
        private String brand;
        private int maxSavingPercentage;
        private long price;
        private long specialPrice;
        private String image;
        private boolean hasStock;

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

        public int getMaxSavingPercentage() {
            return maxSavingPercentage;
        }

        public void setMaxSavingPercentage(int maxSavingPercentage) {
            this.maxSavingPercentage = maxSavingPercentage;
        }

        public long getPrice() {
            return price;
        }

        public void setPrice(long price) {
            this.price = price;
        }

        public long getSpecialPrice() {
            return specialPrice;
        }

        public void setSpecialPrice(long specialPrice) {
            this.specialPrice = specialPrice;
        }

        public String getImage() {
            return image;
        }

        public void setImage(String image) {
            this.image = image;
        }

        @Override
        public boolean initialize(JSONObject jsonObject) throws JSONException {
            sku = jsonObject.optString(RestConstants.SKU);
            name = jsonObject.optString(RestConstants.NAME);
            brand = jsonObject.optString(RestConstants.BRAND);
            maxSavingPercentage = jsonObject.optInt(RestConstants.MAX_SAVING_PERCENTAGE);
            price = jsonObject.optLong(RestConstants.PRICE);
            specialPrice = jsonObject.optLong(RestConstants.SPECIAL_PRICE);
            image = jsonObject.optString(RestConstants.IMAGE);
            hasStock = jsonObject.optBoolean(RestConstants.HAS_STOCK);
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

        public boolean isHasStock() {
            return hasStock;
        }

        public void setHasStock(boolean hasStock) {
            this.hasStock = hasStock;
        }
    }
}