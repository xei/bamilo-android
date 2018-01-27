
package com.bamilo.apicore.service.model.data.itemtracking;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PackageItem {

    @SerializedName("sku")
    @Expose
    private String sku;
    @SerializedName("filters")
    @Expose
    private Filters filters;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("brand")
    @Expose
    private String brand;
    @SerializedName("quantity")
    @Expose
    private Long quantity;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("is_live")
    @Expose
    private Boolean isLive;
    @SerializedName("price")
    @Expose
    private Long price;
    @SerializedName("cancellation")
    @Expose
    private Cancellation cancellation;
    @SerializedName("histories")
    @Expose
    private List<History> histories = null;
    @SerializedName("refund")
    @Expose
    private Refund refund;
    @SerializedName("seller")
    @Expose
    private String seller;

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Filters getFilters() {
        return filters;
    }

    public void setFilters(Filters filters) {
        this.filters = filters;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public Long getQuantity() {
        return quantity;
    }

    public void setQuantity(Long quantity) {
        this.quantity = quantity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public Boolean getIsLive() {
        return isLive;
    }

    public void setIsLive(Boolean isLive) {
        this.isLive = isLive;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public List<History> getHistories() {
        return histories;
    }

    public void setHistories(List<History> histories) {
        this.histories = histories;
    }

    public String getSeller() {
        return seller;
    }

    public void setSeller(String seller) {
        this.seller = seller;
    }

    public Cancellation getCancellation() {
        return cancellation;
    }

    public void setCancellation(Cancellation cancellation) {
        this.cancellation = cancellation;
    }

    public Refund getRefund() {
        return refund;
    }

    public void setRefund(Refund refund) {
        this.refund = refund;
    }
}
