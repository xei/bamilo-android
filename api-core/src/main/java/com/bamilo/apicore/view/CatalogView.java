package com.bamilo.apicore.view;

import com.bamilo.apicore.service.model.data.catalog.Catalog;

/**
 * Created on 12/25/2017.
 */

public interface CatalogView extends BaseView {
    void performCatalog(Catalog catalog);
}
