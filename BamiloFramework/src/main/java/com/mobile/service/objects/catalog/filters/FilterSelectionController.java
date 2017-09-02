package com.mobile.service.objects.catalog.filters;

import android.content.ContentValues;
import android.util.SparseArray;

import com.mobile.service.utils.CollectionUtils;

import java.util.ArrayList;

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
public class FilterSelectionController {

    private ArrayList<CatalogFilter> catalogFilters;
    private FilterOptionInterface[] initialValues;

    public FilterSelectionController(ArrayList<CatalogFilter> catalogFilters){
        this.catalogFilters = catalogFilters;
        initialValues = new FilterOptionInterface[catalogFilters.size()];
    }

    public FilterSelectionController(ArrayList<CatalogFilter> catalogFilters, FilterOptionInterface[] initialValues) {
        this.catalogFilters = catalogFilters;
        this.initialValues = initialValues;
    }

    /**
     * Go to initial state of filters.
     *
     */
    public void goToInitialValues(){
        for(int i = 0; i < initialValues.length; i++){

            CatalogFilter catalogFilter = catalogFilters.get(i);

            //Normal filter
            if (catalogFilter instanceof CatalogCheckFilter) {
                if(initialValues[i] instanceof SparseArray){
                    ((CatalogCheckFilter) catalogFilter).switchSelectedOptions((SelectedFilterOptions)initialValues[i]);
                }
            //Price Filter
            } else if(catalogFilter instanceof CatalogPriceFilter){
                if(initialValues[i] instanceof CatalogPriceFilterOption){
                    ((CatalogPriceFilter) catalogFilter).setOption((CatalogPriceFilterOption)initialValues[i]);
                }

            }
        }
    }

    /**
     * Add catalog filter to initial filter values
     *
     * @param position
     */
    public void addToInitialValues(final int position){

        if(initialValues[position] == null) {

            CatalogFilter catalogFilter = catalogFilters.get(position);

            if(catalogFilter instanceof CatalogCheckFilter){
                final SelectedFilterOptions filterOptions = ((CatalogCheckFilter) catalogFilter).getSelectedFilterOptions();
                initialValues[position] = CollectionUtils.isNotEmpty(filterOptions) ? new SelectedFilterOptions(filterOptions): new SelectedFilterOptions();
            } else if (catalogFilter instanceof CatalogPriceFilter){
                CatalogPriceFilterOption priceFilterOption = ((CatalogPriceFilter) catalogFilter).getOption();
                initialValues[position] = priceFilterOption.clone();
            }

        }
    }

    /**
     * Init all filter values which are not initialized yet.
     *
     */
    public void initAllInitialFilterValues(){

        for(int i = 0; i < initialValues.length; i++){
            // If the position is null, means that initial values of the filter is not initialized yet
            if(initialValues[i] == null){
                addToInitialValues(i);
            }
        }
    }

    /**
     * Clean all saved values from filters
     *
     */
    public void cleanAllFilters(){
        for (CatalogFilter filter : catalogFilters) {
            filter.cleanFilter();
        }

    }

    /**
     * Create content values to filter catalog
     * @return ContentValues
     * @author sergiopereira
     */
    public ContentValues getValues(){
        ContentValues contentValues = new ContentValues();
        for (CatalogFilter filter : catalogFilters) {
            contentValues.putAll(filter.getValues());
        }
        return contentValues;
    }

    public ArrayList<CatalogFilter> getCatalogFilters() {
        return catalogFilters;
    }

    public FilterOptionInterface[] getInitialValues() {
        return initialValues;
    }
}
