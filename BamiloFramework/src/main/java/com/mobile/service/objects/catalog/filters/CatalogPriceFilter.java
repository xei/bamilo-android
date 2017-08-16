package com.mobile.service.objects.catalog.filters;

import android.content.ContentValues;
import android.os.Parcel;

import com.mobile.service.pojo.IntConstants;
import com.mobile.service.pojo.RestConstants;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Copyright (C) 2015 Africa Internet Group - All Rights Reserved
 *
 * Unauthorized copying of this file, via any medium is strictly prohibited
 * Proprietary and confidential.
 *
 *
 * @author ricardosoares
 * @version 1.0
 * @date 2015/09/04
 *
 */
public class CatalogPriceFilter extends CatalogFilter {

    private CatalogPriceFilterOption option;

    public CatalogPriceFilter(){
        option = new CatalogPriceFilterOption();
    }

    public CatalogPriceFilter(JSONObject jsonObject) throws JSONException {
        this();
        initialize(jsonObject);
    }

    @Override
    public boolean initialize(JSONObject jsonObject) throws JSONException {
        super.initialize(jsonObject);
        option.initialize(jsonObject);
        return true;
    }

    @Override
    protected void parseFields(JSONObject fieldsObject) throws JSONException {
        try{
            option.setCheckBoxOption(new PriceFilterCheckBoxOption(fieldsObject));
        }catch (JSONException ex){

        }
    }

    @Override
    protected void setOptionType(String id) {
        optionType = CatalogPriceFilterOption.class;
    }


    @Override
    public boolean hasAppliedFilters() {
        return hasAppliedPriceValues() || hasAppliedCheckBoxOption();
    }

    protected ContentValues getValues() {
        ContentValues values = new ContentValues();

        if(hasAppliedPriceValues()){
            values.put(id, option.getRangeMin() + filterSeparator + option.getRangeMax());
        }

        if(hasAppliedCheckBoxOption()) {
            values.put(option.getCheckBoxOption().getId(), IntConstants.TRUE);
        }
        return values;
    }

    private boolean hasAppliedPriceValues(){
        return option.getRangeMin() != option.getMin() || option.getRangeMax() != option.getMax();
    }

    private boolean hasAppliedCheckBoxOption(){
        return option.getCheckBoxOption() != null && option.getCheckBoxOption().isSelected();
    }

    @Override
    public void cleanFilter() {
        option.setRangeMin(option.getMin());
        option.setRangeMax(option.getMax());
        if(option.getCheckBoxOption() != null) {
            option.getCheckBoxOption().setSelected(false);
        }
    }

    public CatalogPriceFilterOption getOption() {
        return option;
    }

    public void setOption(CatalogPriceFilterOption option) {
        this.option = option;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeParcelable(this.option, flags);
    }

    protected CatalogPriceFilter(Parcel in) {
        super(in);
        this.option = in.readParcelable(CatalogPriceFilterOption.class.getClassLoader());
    }

    public static final Creator<CatalogPriceFilter> CREATOR = new Creator<CatalogPriceFilter>() {
        public CatalogPriceFilter createFromParcel(Parcel source) {
            return new CatalogPriceFilter(source);
        }

        public CatalogPriceFilter[] newArray(int size) {
            return new CatalogPriceFilter[size];
        }
    };
}
