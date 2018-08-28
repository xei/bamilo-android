package com.bamilo.android.core.presentation;

import com.bamilo.android.core.interaction.HomeInteractor;
import com.bamilo.android.core.service.model.EventType;
import com.bamilo.android.core.service.model.HomeResponse;
import com.bamilo.android.core.view.HomeView;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.Subscriptions;

/**
 * Created on 12/20/2017.
 */

public class HomePresenterImpl implements HomePresenter {

    private HomeInteractor homeInteractor;

    private Subscription subscription;

    private HomeView view;

    @Inject
    public HomePresenterImpl(HomeInteractor homeInteractor) {
        this.homeInteractor = homeInteractor;
        subscription = Subscriptions.empty();
    }

    @Override
    public void setView(HomeView view) {
        this.view = view;
    }

    @Override
    public void destroy() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        subscription = null;
        homeInteractor.destroy();

        view = null;
        homeInteractor = null;
    }

    @Override
    public void loadHome(final boolean isConnected) {
        if (view != null) {
            view.toggleProgress(EventType.GET_HOME_EVENT, true);
        }

        subscription = homeInteractor.loadHome().subscribe(new Action1<HomeResponse>() {
            @Override
            public void call(HomeResponse homeResponse) {
                if (view != null) {
                    view.toggleProgress(EventType.GET_HOME_EVENT, false);
                    view.performHomeComponents(homeResponse.getComponents());
                }
            }
        }, new Action1<Throwable>() {
            @Override
            public void call(Throwable throwable) {
                if (view != null) {
                    view.toggleProgress(EventType.GET_HOME_EVENT, false);
                }

                if (!isConnected) {
                    if (view != null) {
                        view.showOfflineMessage(EventType.GET_HOME_EVENT);
                    }
                } else if (throwable instanceof HttpException) {
                    view.showConnectionError(EventType.GET_HOME_EVENT);
                } else {
                    view.showRetry(EventType.GET_HOME_EVENT);
                }
            }
        });
    }
}
