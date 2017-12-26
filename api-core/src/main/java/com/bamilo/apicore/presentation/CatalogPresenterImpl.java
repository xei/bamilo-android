package com.bamilo.apicore.presentation;

import com.bamilo.apicore.interaction.CatalogInteractor;
import com.bamilo.apicore.service.model.CatalogResponse;
import com.bamilo.apicore.view.CatalogView;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created on 12/25/2017.
 */

public class CatalogPresenterImpl implements CatalogPresenter {
    private CatalogInteractor interactor;
    private CatalogView view;

    private Subscription categorySubscription,
            hashSubscription, searchSubscription;

    @Inject
    public CatalogPresenterImpl(CatalogInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void setView(CatalogView view) {
        this.view = view;
    }

    @Override
    public void loadCategoryCatalog(String category, String filters, int page, int maxItems, final boolean isConnected) {
        if (view != null) {
            view.toggleProgress(true);
        }
        categorySubscription = interactor
                .loadCategoryCatalog(category, filters, page, maxItems)
                .subscribe(new Action1<CatalogResponse>() {
                    @Override
                    public void call(CatalogResponse catalogResponse) {
                        if (view != null) {
                            view.toggleProgress(false);
                            view.performCatalog(catalogResponse.getCatalog());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (view != null) {
                            view.toggleProgress(false);
                        }

                        if (!isConnected) {
                            if (view != null) {
                                view.showOfflineMessage();
                            }
                        } else if (throwable instanceof HttpException) {
                            view.showConnectionError();
                        } else {
                            view.showRetry();
                        }
                    }
                });
    }

    @Override
    public void loadHashCatalog(String hash, String filters, int page, int maxItems, final boolean isConnected) {
        if (view != null) {
            view.toggleProgress(true);
        }
        categorySubscription = interactor
                .loadHashCatalog(hash, filters, page, maxItems)
                .subscribe(new Action1<CatalogResponse>() {
                    @Override
                    public void call(CatalogResponse catalogResponse) {
                        if (view != null) {
                            view.toggleProgress(false);
                            view.performCatalog(catalogResponse.getCatalog());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (view != null) {
                            view.toggleProgress(false);
                        }

                        if (!isConnected) {
                            if (view != null) {
                                view.showOfflineMessage();
                            }
                        } else if (throwable instanceof HttpException) {
                            view.showConnectionError();
                        } else {
                            view.showRetry();
                        }
                    }
                });
    }

    @Override
    public void loadSearchCatalog(String query, String filters, int page, int maxItems, final boolean isConnected) {
        if (view != null) {
            view.toggleProgress(true);
        }
        categorySubscription = interactor
                .loadSearchCatalog(query, filters, page, maxItems)
                .subscribe(new Action1<CatalogResponse>() {
                    @Override
                    public void call(CatalogResponse catalogResponse) {
                        if (view != null) {
                            view.toggleProgress(false);
                            view.performCatalog(catalogResponse.getCatalog());
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (view != null) {
                            view.toggleProgress(false);
                        }

                        if (!isConnected) {
                            if (view != null) {
                                view.showOfflineMessage();
                            }
                        } else if (throwable instanceof HttpException) {
                            view.showConnectionError();
                        } else {
                            view.showRetry();
                        }
                    }
                });
    }

    @Override
    public void destroy() {
        if (categorySubscription != null && !categorySubscription.isUnsubscribed()) {
            categorySubscription.unsubscribe();
        }
        categorySubscription = null;


        if (hashSubscription != null && !hashSubscription.isUnsubscribed()) {
            hashSubscription.unsubscribe();
        }
        hashSubscription = null;


        if (searchSubscription != null && !searchSubscription.isUnsubscribed()) {
            searchSubscription.unsubscribe();
        }
        searchSubscription = null;

        interactor.destroy();
        interactor = null;

        view = null;
    }
}
