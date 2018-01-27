package com.bamilo.apicore.presentation;

import com.bamilo.apicore.interaction.ItemTrackingInteractor;
import com.bamilo.apicore.service.model.EventType;
import com.bamilo.apicore.service.model.ItemTrackingResponse;
import com.bamilo.apicore.view.ItemTrackingView;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.Subscriptions;

/**
 * Created by mohsen on 1/27/18.
 */

public class ItemTrackingPresenterImpl implements ItemTrackingPresenter {
    private ItemTrackingInteractor interactor;

    private ItemTrackingView view;
    private Subscription subscription;

    @Inject
    public ItemTrackingPresenterImpl(ItemTrackingInteractor interactor) {
        this.interactor = interactor;
        subscription = Subscriptions.empty();
    }

    @Override
    public void setView(ItemTrackingView view) {
        this.view = view;

    }

    @Override
    public void loadOrderDetails(String orderNumber, final boolean isConnected) {
        if (view != null) {
            view.toggleProgress(EventType.TRACK_ORDER_EVENT, true);
        }
        subscription = interactor.loadOrderDetails(orderNumber)
                .subscribe(new Action1<ItemTrackingResponse>() {
                    @Override
                    public void call(ItemTrackingResponse itemTrackingResponse) {
                        if (view != null) {
                            view.toggleProgress(EventType.TRACK_ORDER_EVENT, false);
                            if (itemTrackingResponse.isSuccess()) {
                                view.performOrderDetails(itemTrackingResponse.getCompleteOrder());
                            } else {
                                view.showServerError(EventType.TRACK_ORDER_EVENT, itemTrackingResponse);
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (view != null) {
                            view.toggleProgress(EventType.TRACK_ORDER_EVENT, false);
                        }

                        if (!isConnected) {
                            if (view != null) {
                                view.showOfflineMessage(EventType.TRACK_ORDER_EVENT);
                            }
                        } else if (throwable instanceof HttpException) {
                            view.showConnectionError(EventType.TRACK_ORDER_EVENT);
                        } else {
                            view.showRetry(EventType.TRACK_ORDER_EVENT);
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
