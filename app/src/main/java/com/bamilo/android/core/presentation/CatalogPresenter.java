package com.bamilo.android.core.presentation;

import com.bamilo.android.core.view.CatalogView;

/**
 * Created on 12/25/2017.
 */

public interface CatalogPresenter extends BasePresenter<CatalogView> {

    void loadCategoryCatalog(String category, String filters, int page, int maxItems, boolean isConnected);

    void loadHashCatalog(String hash, String filters, int page, int maxItems, boolean isConnected);

    void loadSearchCatalog(String query, String filters, int page, int maxItems, boolean isConnected);
}