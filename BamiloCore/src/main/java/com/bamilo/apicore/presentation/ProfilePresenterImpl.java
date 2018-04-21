package com.bamilo.apicore.presentation;

import com.bamilo.apicore.interaction.ProfileInteractor;
import com.bamilo.apicore.service.model.EventType;
import com.bamilo.apicore.service.model.ServerResponse;
import com.bamilo.apicore.service.model.UserProfileResponse;
import com.bamilo.apicore.service.model.data.profile.UserProfile;
import com.bamilo.apicore.view.ProfileView;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by mohsen on 2/27/18.
 */

public class ProfilePresenterImpl implements ProfilePresenter {

    private ProfileInteractor profileInteractor;
    private ProfileView view;
    private Subscription subscription;

    @Inject
    public ProfilePresenterImpl(ProfileInteractor profileInteractor) {
        this.profileInteractor = profileInteractor;
    }

    @Override
    public void setView(ProfileView view) {
        this.view = view;
    }

    @Override
    public void destroy() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        subscription = null;
        view = null;
        profileInteractor.destroy();
        profileInteractor = null;
    }

    @Override
    public void loadUserProfile(final boolean isConnected) {
        if (view != null) {
            view.toggleProgress(EventType.GET_CUSTOMER, true);
        }
        subscription = profileInteractor.loadUserProfile()
                .subscribe(new Action1<UserProfileResponse>() {
                    @Override
                    public void call(UserProfileResponse userProfileResponse) {
                        if (view != null) {
                            view.toggleProgress(EventType.GET_CUSTOMER, false);
                            if (userProfileResponse != null) {
                                if (!userProfileResponse.isSuccess()) {
                                    view.showServerError(EventType.GET_CUSTOMER, userProfileResponse);
                                } else {
                                    view.performUserProfile(userProfileResponse);
                                }
                            } else {
                                view.showRetry(EventType.GET_CUSTOMER);
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (view != null) {
                            view.toggleProgress(EventType.GET_CUSTOMER, false);
                        }

                        if (view != null) {
                            if (!isConnected) {
                                view.showOfflineMessage(EventType.GET_CUSTOMER);
                            } else if (throwable instanceof HttpException) {
                                view.showConnectionError(EventType.GET_CUSTOMER);
                            } else {
                                view.showRetry(EventType.GET_CUSTOMER);
                            }
                        }
                    }
                });
    }

    @Override
    public void submitProfile(final boolean isConnected, UserProfile userProfile) {
        if (view != null) {
            view.toggleProgress(EventType.EDIT_USER_DATA_EVENT, true);
        }
        subscription = profileInteractor.submitProfile(userProfile)
                .subscribe(new Action1<UserProfileResponse>() {
                    @Override
                    public void call(UserProfileResponse response) {
                        if (view != null) {
                            view.toggleProgress(EventType.EDIT_USER_DATA_EVENT, false);
                            if (response == null) {
                                view.showRetry(EventType.EDIT_USER_DATA_EVENT);
                            } else {
                                if (!response.isSuccess()) {
                                    view.showServerError(EventType.EDIT_USER_DATA_EVENT, response);
                                } else {
                                    view.onProfileSubmitted(response);
                                }
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (view != null) {
                            view.toggleProgress(EventType.EDIT_USER_DATA_EVENT, false);
                        }

                        if (view != null) {
                            if (!isConnected) {
                                view.showOfflineMessage(EventType.EDIT_USER_DATA_EVENT);
                            } else if (throwable instanceof HttpException) {
                                view.showConnectionError(EventType.EDIT_USER_DATA_EVENT);
                            } else {
                                view.showRetry(EventType.EDIT_USER_DATA_EVENT);
                            }
                        }
                    }
                });
    }
}
