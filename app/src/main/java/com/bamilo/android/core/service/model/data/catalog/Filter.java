
package com.bamilo.android.core.service.model.data.catalog;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class Filter {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("name")
    @Expose
    private String name;
    @SerializedName("multi")
    @Expose
    private Boolean multi;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("filter_separator")
    @Expose
    private String filterSeparator;

    private List<Option> option = null;

    private PriceFilterOption priceFilterOption;

    @SerializedName("special_price")
    @Expose
    private SpecialPrice specialPrice;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getMulti() {
        return multi;
    }

    public void setMulti(Boolean multi) {
        this.multi = multi;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFilterSeparator() {
        return filterSeparator;
    }

    public void setFilterSeparator(String filterSeparator) {
        this.filterSeparator = filterSeparator;
    }

    public List<Option> getOption() {
        return option;
    }

    public void setOption(List<Option> option) {
        this.option = option;
    }

    public SpecialPrice getSpecialPrice() {
        return specialPrice;
    }

    public void setSpecialPrice(SpecialPrice specialPrice) {
        this.specialPrice = specialPrice;
    }

    public PriceFilterOption getPriceFilterOption() {
        return priceFilterOption;
    }

    public void setPriceFilterOption(PriceFilterOption priceFilterOption) {
        this.priceFilterOption = priceFilterOption;
    }
}
