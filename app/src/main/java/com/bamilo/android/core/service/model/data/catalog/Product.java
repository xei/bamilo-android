
package com.bamilo.android.core.service.model.data.catalog;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Product {

    @SerializedName("sku")
    @Expose
    private String sku;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("brand")
    @Expose
    private String brand;
    @SerializedName("max_saving_percentage")
    @Expose
    private Long maxSavingPercentage;
    @SerializedName("price")
    @Expose
    private Long price;
    @SerializedName("special_price")
    @Expose
    private Long specialPrice;
    @SerializedName("price_converted")
    @Expose
    private Double priceConverted;
    @SerializedName("special_price_converted")
    @Expose
    private Double specialPriceConverted;
    @SerializedName("categories")
    @Expose
    private String categories;
    @SerializedName("category_entity")
    @Expose
    private CategoryEntity categoryEntity;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("target")
    @Expose
    private String target;
    @SerializedName("rating_reviews_summary")
    @Expose
    private RatingReviewsSummary ratingReviewsSummary;
    @SerializedName("is_new")
    @Expose
    private Boolean isNew;

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

    public Long getMaxSavingPercentage() {
        return maxSavingPercentage;
    }

    public void setMaxSavingPercentage(Long maxSavingPercentage) {
        this.maxSavingPercentage = maxSavingPercentage;
    }

    public Long getPrice() {
        return price;
    }

    public void setPrice(Long price) {
        this.price = price;
    }

    public Long getSpecialPrice() {
        return specialPrice;
    }

    public void setSpecialPrice(Long specialPrice) {
        this.specialPrice = specialPrice;
    }

    public Double getPriceConverted() {
        return priceConverted;
    }

    public void setPriceConverted(Double priceConverted) {
        this.priceConverted = priceConverted;
    }

    public Double getSpecialPriceConverted() {
        return specialPriceConverted;
    }

    public void setSpecialPriceConverted(Double specialPriceConverted) {
        this.specialPriceConverted = specialPriceConverted;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public CategoryEntity getCategoryEntity() {
        return categoryEntity;
    }

    public void setCategoryEntity(CategoryEntity categoryEntity) {
        this.categoryEntity = categoryEntity;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

    public RatingReviewsSummary getRatingReviewsSummary() {
        return ratingReviewsSummary;
    }

    public void setRatingReviewsSummary(RatingReviewsSummary ratingReviewsSummary) {
        this.ratingReviewsSummary = ratingReviewsSummary;
    }

    public Boolean getIsNew() {
        return isNew;
    }

    public void setIsNew(Boolean isNew) {
        this.isNew = isNew;
    }

}
