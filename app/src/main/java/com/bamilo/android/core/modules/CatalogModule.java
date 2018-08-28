package com.bamilo.android.core.modules;

import com.bamilo.android.core.interaction.CatalogInteractor;
import com.bamilo.android.core.interaction.CatalogInteractorImpl;
import com.bamilo.android.core.presentation.CatalogPresenter;
import com.bamilo.android.core.presentation.CatalogPresenterImpl;
import com.bamilo.android.core.view.CatalogView;

import dagger.Module;
import dagger.Provides;

/**
 * Created on 12/25/2017.
 */

@Module
public class CatalogModule {
    private CatalogView catalogView;

    public CatalogModule(CatalogView catalogView) {
        this.catalogView = catalogView;
    }

    @Provides
    public CatalogView provideCatalogView() {
        return this.catalogView;
    }

    @Provides
    public CatalogPresenter provideCatalogPresenter(CatalogPresenterImpl catalogPresenter) {
        catalogPresenter.setView(catalogView);
        return catalogPresenter;
    }

    @Provides
    public CatalogInteractor provideCatalogInteractor(CatalogInteractorImpl catalogInteractor) {
        return catalogInteractor;
    }
}
