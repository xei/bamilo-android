package com.bamilo.apicore.presentation;

import com.bamilo.apicore.interaction.ItemTrackingInteractor;
import com.bamilo.apicore.service.model.ItemTrackingResponse;
import com.bamilo.apicore.view.ItemTrackingView;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by mohsen on 1/27/18.
 */

public class ItemTrackingPresenterImpl implements ItemTrackingPresenter {
    private ItemTrackingInteractor interactor;

    private ItemTrackingView view;
    private Subscription subscription;

    public ItemTrackingPresenterImpl(ItemTrackingInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void setView(ItemTrackingView view) {
        this.view = view;

    }

    @Override
    public void loadOrderDetails(String orderNumber, final boolean isConnected) {
        if (view != null) {
            view.toggleProgress(true);
        }
        subscription = interactor.loadOrderDetails(orderNumber)
                .subscribe(new Action1<ItemTrackingResponse>() {
                    @Override
                    public void call(ItemTrackingResponse itemTrackingResponse) {
                        if (view != null) {
                            view.toggleProgress(false);
                            if (itemTrackingResponse.isSuccess()) {
                                view.performOrderDetails(itemTrackingResponse.getCompleteOrder());
                            } else {
                                view.showServerError(itemTrackingResponse);
                            }
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
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
        subscription = null;
        interactor.destroy();

        view = null;
        interactor = null;
    }
}
