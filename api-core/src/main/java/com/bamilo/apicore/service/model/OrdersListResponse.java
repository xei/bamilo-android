package com.bamilo.apicore.service.model;

import com.bamilo.apicore.service.model.data.orders.OrderListItem;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created on 12/30/2017.
 */

public class OrdersListResponse extends ServerResponse {
    @SerializedName("orders")
    @Expose
    private List<OrderListItem> orderListItems;

    @SerializedName("total_orders")
    @Expose
    private int totalOrders;

    @SerializedName("pagination")
    @Expose
    private Pagination pagination;

    public OrdersListResponse(JsonObject jsonObject, Gson gson) {
        super(jsonObject, gson);
    }

    public OrdersListResponse() {
        super(null, null);
    }

    @Override
    public EventType getEventType() {
        return EventType.GET_MY_ORDERS_LIST_EVENT;
    }

    @Override
    public EventTask getEventTask() {
        return EventTask.NORMAL_TASK;
    }

    @Override
    protected void initializeWithJson(JsonObject jsonObject, Gson gson) {
        if (jsonObject != null && gson != null) {
            setSuccess(jsonObject.get(JsonConstants.RestConstants.SUCCESS).getAsBoolean());
            JsonElement jsonElement = jsonObject.get(JsonConstants.RestConstants.METADATA);
            if (jsonElement != null) {
                OrdersListResponse response = gson.fromJson(jsonElement, OrdersListResponse.class);
                this.orderListItems = response.orderListItems;
                this.pagination = response.pagination;
                this.totalOrders = response.totalOrders;
            }
        }
    }

    public List<OrderListItem> getOrderListItems() {
        return orderListItems;
    }

    public void setOrderListItems(List<OrderListItem> orderListItems) {
        this.orderListItems = orderListItems;
    }

    public Pagination getPagination() {
        return pagination;
    }

    public void setPagination(Pagination pagination) {
        this.pagination = pagination;
    }

    public int getTotalOrders() {
        return totalOrders;
    }

    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    public static class Pagination {
        @SerializedName("per_page")
        @Expose
        private int itemsPerPage;
        @SerializedName("current_page")
        @Expose
        private int currentPage;
        @SerializedName("total_pages")
        @Expose
        private int totalPages;

        public int getItemsPerPage() {
            return itemsPerPage;
        }

        public void setItemsPerPage(int itemsPerPage) {
            this.itemsPerPage = itemsPerPage;
        }

        public int getCurrentPage() {
            return currentPage;
        }

        public void setCurrentPage(int currentPage) {
            this.currentPage = currentPage;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }
    }
}
