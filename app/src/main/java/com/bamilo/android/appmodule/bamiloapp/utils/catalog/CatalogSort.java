package com.bamilo.android.appmodule.bamiloapp.utils.catalog;

import com.bamilo.android.R;

/**
 * Catalog sort.
 * 
 * @author sergiopereira
 *
 */
public enum CatalogSort {
    
    BEST_RATING("rating", R.string.products_sort_bestrated),
    
    POPULARITY("popularity", R.string.products_sort_popularity),
    
    NEW_IN("newest", R.string.products_sort_newin),
    
    PRICE_UP("price/dir/asc", R.string.products_sort_priceup),
    
    PRICE_DOWN("price/dir/desc", R.string.products_sort_pricedown),
    
    NAME("name", R.string.products_sort_name),

    BRAND("brand", R.string.products_sort_brand);

    public String path;
    public int name;

    CatalogSort(String path, int name) {
        this.path = path;
        this.name = name;
    }

}
