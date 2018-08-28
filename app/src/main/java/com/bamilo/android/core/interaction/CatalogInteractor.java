package com.bamilo.android.core.interaction;

import com.bamilo.android.core.service.model.CatalogResponse;

import rx.Observable;

/**
 * Created on 12/25/2017.
 */

public interface CatalogInteractor extends BaseInteractor {

    Observable<CatalogResponse> loadCategoryCatalog(String category, String filters, int page, int maxItems);

    Observable<CatalogResponse> loadHashCatalog(String hash, String filters, int page, int maxItems);

    Observable<CatalogResponse> loadSearchCatalog(String query, String filters, int page, int maxItems);
}
