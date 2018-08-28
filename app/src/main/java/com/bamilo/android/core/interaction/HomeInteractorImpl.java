package com.bamilo.android.core.interaction;

import com.bamilo.android.core.scheduler.SchedulerProvider;
import com.bamilo.android.core.service.BamiloApiService;
import com.bamilo.android.core.service.model.HomeResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;
import rx.subjects.ReplaySubject;

/**
 * Created on 12/20/2017.
 */

public class HomeInteractorImpl implements HomeInteractor {
    private BamiloApiService bamiloApiService;
    private SchedulerProvider schedulerProvider;
    private Gson mGson;

    private ReplaySubject<HomeResponse> homeReplaySubject;
    private Subscription homeSubscription;

    @Inject
    public HomeInteractorImpl(BamiloApiService bamiloApiService, SchedulerProvider schedulerProvider, Gson gson) {
        this.bamiloApiService = bamiloApiService;
        this.schedulerProvider = schedulerProvider;
        mGson = gson;
    }

    @Override
    public void destroy() {
        if (homeSubscription != null && !homeSubscription.isUnsubscribed()) {
            homeSubscription.unsubscribe();
        }
        homeSubscription = null;
        homeReplaySubject = null;
    }

    @Override
    public Observable<HomeResponse> loadHome() {
        if (homeSubscription == null || homeSubscription.isUnsubscribed()) {
            homeReplaySubject = ReplaySubject.create();

            homeSubscription = bamiloApiService.loadHome()
                    .map(new Func1<JsonObject, HomeResponse>() {
                        @Override
                        public HomeResponse call(JsonObject jsonObject) {
                            return new HomeResponse(jsonObject, mGson);
                        }
                    })
                    .subscribeOn(schedulerProvider.getNetworkThreadScheduler())
                    .observeOn(schedulerProvider.getMainThreadScheduler())
                    .subscribe(homeReplaySubject);
        }
        return homeReplaySubject.asObservable();
    }
}
