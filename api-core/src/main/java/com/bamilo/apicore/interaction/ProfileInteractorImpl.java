package com.bamilo.apicore.interaction;

import com.bamilo.apicore.scheduler.SchedulerProvider;
import com.bamilo.apicore.service.BamiloApiService;
import com.bamilo.apicore.service.model.ServerResponse;
import com.bamilo.apicore.service.model.UserProfileResponse;
import com.bamilo.apicore.service.model.data.profile.UserProfile;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.subjects.ReplaySubject;

/**
 * Created by mohsen on 2/26/18.
 */

public class ProfileInteractorImpl implements ProfileInteractor {
    public static final String FIELD_CUSTOMER = "customer", FIELD_PHONE_PREFIX = "phone_prefix", FIELD_PASSWORD = "password",
            FIELD_NATIONAL_ID = "national_id", FIELD_BIRTHDAY = "birthday", FIELD_EMAIL = "email", FIELD_FIRST_NAME = "first_name",
            FIELD_LAST_NAME = "last_name", FIELD_PHONE = "phone", FIELD_GENDER = "gender", FIELD_CARD_NUMBER = "card_number";

    private BamiloApiService bamiloApiService;
    private SchedulerProvider schedulerProvider;
    private Gson mGson;

    private ReplaySubject<UserProfileResponse> getProfileReplaySubject;
    private Subscription getProfileSubscription;

    private ReplaySubject<UserProfileResponse> submitProfileReplaySubject;
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
    public Observable<UserProfileResponse> submitProfile(UserProfile userProfile) {
        if (submitProfileSubscription == null || submitProfileSubscription.isUnsubscribed()) {
            submitProfileReplaySubject = ReplaySubject.create();

            submitProfileSubscription =
                    bamiloApiService
                            .submitUserProfile(createRequestMap(userProfile))
                            .map(new Func1<JsonObject, UserProfileResponse>() {
                                @Override
                                public UserProfileResponse call(JsonObject jsonObject) {
                                    return new UserProfileResponse(jsonObject, mGson);
                                }
                            })
                            .subscribeOn(schedulerProvider.getNetworkThreadScheduler())
                            .observeOn(schedulerProvider.getMainThreadScheduler())
                            .subscribe(submitProfileReplaySubject);
        }
        return submitProfileReplaySubject.asObservable();
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

    private Map<String, String> createRequestMap(UserProfile userProfile) {
        Map<String, String> itemMap = new HashMap<>();
        String fieldsPattern = "%s[%s]";

        itemMap.put(String.format(fieldsPattern, FIELD_CUSTOMER, FIELD_PHONE_PREFIX), String.valueOf(100)); /*I don't know why :))*/
        itemMap.put(String.format(fieldsPattern, FIELD_CUSTOMER, FIELD_PASSWORD), userProfile.getPassword());

        String nationalId = userProfile.getNationalId();
        itemMap.put(String.format(fieldsPattern, FIELD_CUSTOMER, FIELD_NATIONAL_ID), nationalId == null ? "" : nationalId);

        String birthday = userProfile.getBirthday();
        itemMap.put(String.format(fieldsPattern, FIELD_CUSTOMER, FIELD_BIRTHDAY), birthday == null ? "" : birthday);

        itemMap.put(String.format(fieldsPattern, FIELD_CUSTOMER, FIELD_EMAIL), userProfile.getEmail());

        String firstName = userProfile.getFirstName();
        itemMap.put(String.format(fieldsPattern, FIELD_CUSTOMER, FIELD_FIRST_NAME), firstName == null ? "" : firstName);

        String lastName = userProfile.getLastName();
        itemMap.put(String.format(fieldsPattern, FIELD_CUSTOMER, FIELD_LAST_NAME), lastName == null ? "" : lastName);

        itemMap.put(String.format(fieldsPattern, FIELD_CUSTOMER, FIELD_PHONE), userProfile.getPhone());

        String gender = userProfile.getGender();
        itemMap.put(String.format(fieldsPattern, FIELD_CUSTOMER, FIELD_GENDER), gender == null ? "" : gender);

        String cardNumber = userProfile.getCardNumber();
        itemMap.put(String.format(fieldsPattern, FIELD_CUSTOMER, FIELD_CARD_NUMBER), cardNumber == null ? "" : cardNumber);

        return itemMap;
    }
}
