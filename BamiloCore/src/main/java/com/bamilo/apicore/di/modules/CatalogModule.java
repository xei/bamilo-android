package com.bamilo.apicore.di.modules;

import com.bamilo.apicore.interaction.CatalogInteractor;
import com.bamilo.apicore.interaction.CatalogInteractorImpl;
import com.bamilo.apicore.presentation.CatalogPresenter;
import com.bamilo.apicore.presentation.CatalogPresenterImpl;
import com.bamilo.apicore.view.CatalogView;

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
