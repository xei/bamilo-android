package com.bamilo.android.core.service.model.data.ordercancellation;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by mohsen on 1/31/18.
 */

public class CancellationRequestBody {
    @Expose
    @SerializedName("orderNumber")
    private String orderNumber;
    @Expose
    @SerializedName("description")
    private String description;
    @Expose
    @SerializedName("items")
    private List<Item> items;

    public String getOrderNumber() {
        return orderNumber;
    }

    public void setOrderNumber(String orderNumber) {
        this.orderNumber = orderNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }

    public static class Item {
        @Expose
        @SerializedName("simpleSku")
        private String simpleSku;
        @Expose
        @SerializedName("itemId")
        private String itemId;
        @Expose
        @SerializedName("quantity")
        private int quantity;
        @Expose
        @SerializedName("reasonId")
        private String reasonId;

        public String getSimpleSku() {
            return simpleSku;
        }

        public void setSimpleSku(String simpleSku) {
            this.simpleSku = simpleSku;
        }

        public int getQuantity() {
            return quantity;
        }

        public void setQuantity(int quantity) {
            this.quantity = quantity;
        }

        public String getReasonId() {
            return reasonId;
        }

        public void setReasonId(String reasonId) {
            this.reasonId = reasonId;
        }

        public String getItemId() {
            return itemId;
        }

        public void setItemId(String itemId) {
            this.itemId = itemId;
        }
    }
}
