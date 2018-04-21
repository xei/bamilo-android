package com.bamilo.apicore.interaction;

import com.bamilo.apicore.scheduler.SchedulerProvider;
import com.bamilo.apicore.service.BamiloApiService;
import com.bamilo.apicore.service.model.CatalogResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;
import rx.subjects.ReplaySubject;

/**
 * Created on 12/25/2017.
 */

public class CatalogInteractorImpl implements CatalogInteractor {
    private BamiloApiService bamiloApiService;
    private SchedulerProvider schedulerProvider;
    private Gson mGson;

    private ReplaySubject<CatalogResponse> categoryReplaySubject;
    private Subscription categorySubscription;

    private ReplaySubject<CatalogResponse> hashReplaySubject;
    private Subscription hashSubscription;

    private ReplaySubject<CatalogResponse> searchReplaySubject;
    private Subscription searchSubscription;


    @Inject
    public CatalogInteractorImpl(BamiloApiService apiService, SchedulerProvider schedulerProvider, Gson gson) {
        this.bamiloApiService = apiService;
        this.schedulerProvider = schedulerProvider;
        this.mGson = gson;
    }

    @Override
    public Observable<CatalogResponse> loadCategoryCatalog(String category, String filters, int page, int maxItems) {
        if (categorySubscription == null || categorySubscription.isUnsubscribed()) {
            categoryReplaySubject = ReplaySubject.create();

            categorySubscription = bamiloApiService.loadCategoryCatalog(category, filters, page, maxItems)
                    .map(new Func1<JsonObject, CatalogResponse>() {
                        @Override
                        public CatalogResponse call(JsonObject jsonObject) {
                            return new CatalogResponse(jsonObject, mGson);
                        }
                    })
                    .subscribeOn(schedulerProvider.getNetworkThreadScheduler())
                    .observeOn(schedulerProvider.getMainThreadScheduler())
                    .subscribe(categoryReplaySubject);
        }
        return categoryReplaySubject.asObservable();
    }

    @Override
    public Observable<CatalogResponse> loadHashCatalog(String hash, String filters, int page, int maxItems) {
        if (hashSubscription == null || hashSubscription.isUnsubscribed()) {
            hashReplaySubject = ReplaySubject.create();

            hashSubscription = bamiloApiService.loadHashCatalog(hash, filters, page, maxItems)
                    .map(new Func1<JsonObject, CatalogResponse>() {
                        @Override
                        public CatalogResponse call(JsonObject jsonObject) {
                            return new CatalogResponse(jsonObject, mGson);
                        }
                    })
                    .subscribeOn(schedulerProvider.getNetworkThreadScheduler())
                    .observeOn(schedulerProvider.getMainThreadScheduler())
                    .subscribe(hashReplaySubject);
        }
        return hashReplaySubject.asObservable();
    }

    @Override
    public Observable<CatalogResponse> loadSearchCatalog(String query, String filters, int page, int maxItems) {
        if (searchSubscription == null || searchSubscription.isUnsubscribed()) {
            searchReplaySubject = ReplaySubject.create();

            searchSubscription = bamiloApiService.loadSearchCatalog(query, filters, page, maxItems)
                    .map(new Func1<JsonObject, CatalogResponse>() {
                        @Override
                        public CatalogResponse call(JsonObject jsonObject) {
                            return new CatalogResponse(jsonObject, mGson);
                        }
                    })
                    .subscribeOn(schedulerProvider.getNetworkThreadScheduler())
                    .observeOn(schedulerProvider.getMainThreadScheduler())
                    .subscribe(searchReplaySubject);
        }
        return searchReplaySubject.asObservable();
    }

    @Override
    public void destroy() {
        if (categorySubscription != null && !categorySubscription.isUnsubscribed()) {
            categorySubscription.unsubscribe();
        }
        categorySubscription = null;
        categoryReplaySubject = null;

        if (hashSubscription != null && !hashSubscription.isUnsubscribed()) {
            hashSubscription.unsubscribe();
        }
        hashSubscription = null;
        hashReplaySubject = null;

        if (searchSubscription != null && !searchSubscription.isUnsubscribed()) {
            searchSubscription.unsubscribe();
        }
        searchSubscription = null;
        searchReplaySubject = null;
    }
}
