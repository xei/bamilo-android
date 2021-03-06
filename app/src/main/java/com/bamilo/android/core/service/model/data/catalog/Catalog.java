
package com.bamilo.android.core.service.model.data.catalog;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Catalog {

    @SerializedName("sort")
    @Expose
    private String sort;
    @SerializedName("total_products")
    @Expose
    private Long totalProducts;
    @SerializedName("search_term")
    @Expose
    private String searchTerm;
    @SerializedName("title")
    @Expose
    private String title;
    @SerializedName("categories")
    @Expose
    private String categories;
    @SerializedName("vertical")
    @Expose
    private String vertical;
    @SerializedName("results")
    @Expose
    private List<Product> products = null;
    @SerializedName("filters")
    @Expose
    private List<Filter> filters = null;
    @SerializedName("error_message")
    @Expose
    private String errorMessage;

    public String getSort() {
        return sort;
    }

    public void setSort(String sort) {
        this.sort = sort;
    }

    public Long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(Long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategories() {
        return categories;
    }

    public void setCategories(String categories) {
        this.categories = categories;
    }

    public String getVertical() {
        return vertical;
    }

    public void setVertical(String vertical) {
        this.vertical = vertical;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public List<Filter> getFilters() {
        return filters;
    }

    public void setFilters(List<Filter> filters) {
        this.filters = filters;
    }

    public String getSearchTerm() {
        return searchTerm;
    }

    public void setSearchTerm(String searchTerm) {
        this.searchTerm = searchTerm;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
