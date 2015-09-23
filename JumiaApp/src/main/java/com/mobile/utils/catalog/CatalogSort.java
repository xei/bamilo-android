package com.mobile.utils.catalog;

import com.mobile.view.R;

/**
 * Catalog sort.
 * 
 * @author sergiopereira
 *
 */
public enum CatalogSort {
    
    BESTRATING("rating", R.string.products_sort_bestrated, null),
    
    POPULARITY("popularity", R.string.products_sort_popularity, null),
    
    NEWIN("newest", R.string.products_sort_newin, null),
    
    PRICE_UP("price", R.string.products_sort_priceup, "asc"),
    
    PRICE_DOWN("price", R.string.products_sort_pricedown, "desc"),
    
    NAME("name", R.string.products_sort_name, null), // not working

    BRAND("brand", R.string.products_sort_brand, null); // not working

    public String id;
    public int name;
    public String direction;

    CatalogSort(String id, int name, String direction) {
        this.id = id;
        this.name = name;
        this.direction = direction;
    }

}
