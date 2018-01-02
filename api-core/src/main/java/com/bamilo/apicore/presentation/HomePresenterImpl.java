package com.bamilo.apicore.presentation;

import com.bamilo.apicore.interaction.HomeInteractor;
import com.bamilo.apicore.service.model.HomeResponse;
import com.bamilo.apicore.view.HomeView;

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

    Subscription subscription;

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
            view.toggleProgress(true);
        }

        subscription = homeInteractor.loadHome().subscribe(new Action1<HomeResponse>() {
            @Override
            public void call(HomeResponse homeResponse) {
                if (view != null) {
                    view.toggleProgress(false);
                    view.performHomeComponents(homeResponse.getComponents());
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
}
