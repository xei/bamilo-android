package com.mobile.newFramework.objects.catalog.filters;

import android.util.SparseArray;

import com.mobile.newFramework.utils.CollectionUtils;
import com.mobile.newFramework.utils.DeviceInfoHelper;

/**
 * Created by rsoares on 9/4/15.
 */
public class FilterSelectionController {

    private CatalogFilters catalogFilters;
    private Object[] initialValues;

    public FilterSelectionController(CatalogFilters catalogFilters){
        this.catalogFilters = catalogFilters;
        initialValues = new Object[catalogFilters.size()];
    }

    public void goToInitialValues(){
        for(int i = 0; i < initialValues.length; i++){

            CatalogFilter catalogFilter = catalogFilters.get(i);

            //Normal filter
            if (catalogFilter instanceof CatalogCheckFilter) {
                if(initialValues[i] instanceof SparseArray){
                    ((CatalogCheckFilter) catalogFilter).switchSelectedOptions((SparseArray)initialValues[i]);
                }
            //Price Filter
            } else if(catalogFilter instanceof CatalogPriceFilter){
                if(initialValues[i] instanceof CatalogPriceFilterOption){
                    ((CatalogPriceFilter) catalogFilter).setOption((CatalogPriceFilterOption)initialValues[i]);
                }

            }
        }
    }

    public void addToInitialValues(final int position){

        if(initialValues[position] == null) {

            CatalogFilter catalogFilter = catalogFilters.get(position);

            if(catalogFilter instanceof CatalogCheckFilter){
                final SparseArray<MultiFilterOptionService> filterOptions = ((CatalogCheckFilter) catalogFilter).getSelectedFilterOptions();

                DeviceInfoHelper.executeCodeBasedOnIceCreamSandwichVersion(new DeviceInfoHelper.IDeviceVersionBasedCode() {
                    @Override
                    public void highVersionCallback() {
                        initialValues[position] = CollectionUtils.isNotEmpty(filterOptions) ? filterOptions.clone() : new SparseArray<>();
                    }

                    @Override
                    public void lowerVersionCallback() {
                        SparseArray<FilterOptionService> catalogFilterOptionsClone = new SparseArray<>();
                        if (CollectionUtils.isNotEmpty(filterOptions)) {
                            for (int i = 0; i < filterOptions.size(); i++) {
                                int key = filterOptions.keyAt(i);
                                FilterOptionService catalogFilterOption = filterOptions.get(key);
                                if (catalogFilterOption != null) {
                                    catalogFilterOptionsClone.put(key, catalogFilterOption);
                                }
                            }
                        }
                        initialValues[position] = catalogFilterOptionsClone;
                    }
                });
            } else if (catalogFilter instanceof CatalogPriceFilter){
                CatalogPriceFilterOption priceFilterOption = ((CatalogPriceFilter) catalogFilter).getOption();
                initialValues[position] = priceFilterOption.clone();
            }

        }
    }
}
