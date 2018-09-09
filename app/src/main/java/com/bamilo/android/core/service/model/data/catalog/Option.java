
package com.bamilo.android.core.service.model.data.catalog;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Option {

    @SerializedName("label")
    @Expose
    private String label;
    @SerializedName("val")
    @Expose
    private String val;
    @SerializedName("total_products")
    @Expose
    private Long totalProducts;
    @SerializedName("selected")
    @Expose
    private Boolean selected;
    @SerializedName("hex_value")
    @Expose
    private String hexValue;

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getVal() {
        return val;
    }

    public void setVal(String val) {
        this.val = val;
    }

    public Long getTotalProducts() {
        return totalProducts;
    }

    public void setTotalProducts(Long totalProducts) {
        this.totalProducts = totalProducts;
    }

    public Boolean getSelected() {
        return selected;
    }

    public void setSelected(Boolean selected) {
        this.selected = selected;
    }

    public String getHexValue() {
        return hexValue;
    }

    public void setHexValue(String hexValue) {
        this.hexValue = hexValue;
    }

}
