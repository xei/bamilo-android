package com.bamilo.android.core.service.model.data.home;

import com.bamilo.android.core.service.model.JsonConstants;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created on 12/20/2017.
 */

public class DealComponent extends BaseComponent {

    @Expose
    @SerializedName(JsonConstants.RestConstants.DATA)
    private Deal deal;

    public Deal getDeal() {
        return deal;
    }

    public void setDeal(Deal deal) {
        this.deal = deal;
    }

    public static class Deal {
        @Expose
        @SerializedName(JsonConstants.RestConstants.BACKGROUND_COLOR)
        private String backgroundColor;

        @Expose
        @SerializedName(JsonConstants.RestConstants.HEADER)
        private DealHeader dealHeader;

        @Expose
        @SerializedName(JsonConstants.RestConstants.BODY)
        private DealBody dealBody;

        public String getBackgroundColor() {
            return backgroundColor;
        }

        public void setBackgroundColor(String backgroundColor) {
            this.backgroundColor = backgroundColor;
        }

        public DealHeader getDealHeader() {
            return dealHeader;
        }

        public void setDealHeader(DealHeader dealHeader) {
            this.dealHeader = dealHeader;
        }

        public DealBody getDealBody() {
            return dealBody;
        }

        public void setDealBody(DealBody dealBody) {
            this.dealBody = dealBody;
        }
    }

    public static class DealHeader {
        @Expose
        @SerializedName(JsonConstants.RestConstants.TITLE)
        private String title;
        @Expose
        @SerializedName(JsonConstants.RestConstants.TEXT_COLOR)
        private String titleTextColor;

        @Expose
        @SerializedName(JsonConstants.RestConstants.MORE_OPTION)
        private DealMoreOptions moreOptions;

        @Expose
        @SerializedName(JsonConstants.RestConstants.COUNTER)
        private DealCountDown dealCountDown;

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

        public DealMoreOptions getMoreOptions() {
            return moreOptions;
        }

        public void setMoreOptions(DealMoreOptions moreOptions) {
            this.moreOptions = moreOptions;
        }

        public DealCountDown getDealCountDown() {
            return dealCountDown;
        }

        public void setDealCountDown(DealCountDown dealCountDown) {
            this.dealCountDown = dealCountDown;
        }
    }

    public static class DealCountDown {
        @Expose
        @SerializedName(JsonConstants.RestConstants.TEXT_COLOR)
        private String counterTextColor;
        @Expose
        @SerializedName(JsonConstants.RestConstants.REMAINING_SECONDS)
        private Long counterRemainingSeconds;

        private long initialTimeSeconds;

        public DealCountDown() {
            initialTimeSeconds = System.currentTimeMillis() / 1000;
        }

        public String getCounterTextColor() {
            return counterTextColor;
        }

        public void setCounterTextColor(String counterTextColor) {
            this.counterTextColor = counterTextColor;
        }

        public long getCounterRemainingSeconds() {
            if (counterRemainingSeconds == null) {
                return -1;
            }
            return counterRemainingSeconds;
        }

        public void setCounterRemainingSeconds(Long counterRemainingSeconds) {
            this.counterRemainingSeconds = counterRemainingSeconds;
        }

        public long getInitialTimeSeconds() {
            return initialTimeSeconds;
        }

        public void setInitialTimeSeconds(long initialTimeSeconds) {
            this.initialTimeSeconds = initialTimeSeconds;
        }
    }

    public static class DealMoreOptions {
        @Expose
        @SerializedName(JsonConstants.RestConstants.TITLE)
        private String moreOptionsTitle;
        @Expose
        @SerializedName(JsonConstants.RestConstants.TEXT_COLOR)
        private String moreOptionsTitleColor;
        @Expose
        @SerializedName(JsonConstants.RestConstants.TARGET)
        private String moreOptionsTarget;

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
    }

    public static class DealBody {
        @Expose
        @SerializedName(JsonConstants.RestConstants.PRODUCTS)
        private List<Product> products;

        public List<Product> getProducts() {
            return products;
        }

        public void setProducts(List<Product> products) {
            this.products = products;
        }
    }

    public static class Product {
        @Expose
        @SerializedName(JsonConstants.RestConstants.SKU)
        private String sku;
        @Expose
        @SerializedName(JsonConstants.RestConstants.NAME)
        private String name;
        @Expose
        @SerializedName(JsonConstants.RestConstants.BRAND)
        private String brand;
        @Expose
        @SerializedName(JsonConstants.RestConstants.MAX_SAVING_PERCENTAGE)
        private int maxSavingPercentage;
        @Expose
        @SerializedName(JsonConstants.RestConstants.PRICE)
        private long price;
        @Expose
        @SerializedName(JsonConstants.RestConstants.SPECIAL_PRICE)
        private long specialPrice;
        @Expose
        @SerializedName(JsonConstants.RestConstants.IMAGE)
        private String image;
        @Expose
        @SerializedName(JsonConstants.RestConstants.HAS_STOCK)
        private Boolean hasStock;

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

        public Boolean hasStock() {
            return hasStock;
        }

        public void setHasStock(Boolean hasStock) {
            this.hasStock = hasStock;
        }
    }

}
