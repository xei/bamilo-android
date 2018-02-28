package com.bamilo.apicore.interaction;

import com.bamilo.apicore.scheduler.SchedulerProvider;
import com.bamilo.apicore.service.BamiloApiService;
import com.bamilo.apicore.service.model.UserProfileResponse;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.functions.Func1;
import rx.subjects.ReplaySubject;

/**
 * Created by mohsen on 2/26/18.
 */

public class ProfileInteractorImpl implements ProfileInteractor {
    private BamiloApiService bamiloApiService;
    private SchedulerProvider schedulerProvider;
    private Gson mGson;

    private ReplaySubject<UserProfileResponse> getProfileReplaySubject;
    private Subscription getProfileSubscription;

    private ReplaySubject<Void> submitProfileReplaySubject;
    private Subscription submitProfileSubscription;

    @Inject
    public ProfileInteractorImpl(BamiloApiService bamiloApiService, SchedulerProvider schedulerProvider, Gson mGson) {
        this.bamiloApiService = bamiloApiService;
        this.schedulerProvider = schedulerProvider;
        this.mGson = mGson;
    }


    @Override
    public Observable<UserProfileResponse> loadUserProfile() {
        if (getProfileSubscription == null || getProfileSubscription.isUnsubscribed()) {
            getProfileReplaySubject = ReplaySubject.create();

            getProfileSubscription = bamiloApiService.loadUserProfile()
                    .map(new Func1<JsonObject, UserProfileResponse>() {
                        @Override
                        public UserProfileResponse call(JsonObject jsonObject) {
                            return new UserProfileResponse(jsonObject, mGson);
                        }
                    })
                    .subscribeOn(schedulerProvider.getNetworkThreadScheduler())
                    .observeOn(schedulerProvider.getMainThreadScheduler())
                    .subscribe(getProfileReplaySubject);
        }
        return getProfileReplaySubject.asObservable();
    }

    @Override
    public void destroy() {
        if (getProfileSubscription != null && !getProfileSubscription.isUnsubscribed()) {
            getProfileSubscription.unsubscribe();
        }
        getProfileSubscription = null;
        getProfileReplaySubject = null;

        if (submitProfileSubscription != null && !submitProfileSubscription.isUnsubscribed()) {
            submitProfileSubscription.unsubscribe();
        }
        submitProfileSubscription = null;
        submitProfileReplaySubject = null;
    }
}
