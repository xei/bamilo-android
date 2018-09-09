package com.bamilo.android.core.presentation;

import com.bamilo.android.core.interaction.OrderCancellationInteractor;
import com.bamilo.android.core.service.model.EventType;
import com.bamilo.android.core.service.model.OrderCancellationResponse;
import com.bamilo.android.core.service.model.data.ordercancellation.CancellationRequestBody;
import com.bamilo.android.core.view.OrderCancellationView;

import javax.inject.Inject;

import retrofit2.adapter.rxjava.HttpException;
import rx.Subscription;
import rx.functions.Action1;

/**
 * Created by mohsen on 1/31/18.
 */

public class OrderCancellationPresenterImpl implements OrderCancellationPresenter {
    private OrderCancellationInteractor interactor;

    private Subscription subscription;
    private OrderCancellationView view;

    @Inject
    public OrderCancellationPresenterImpl(OrderCancellationInteractor interactor) {
        this.interactor = interactor;
    }

    @Override
    public void submitCancellationRequest(final boolean isConnected, CancellationRequestBody requestBody) {
        if (view != null) {
            view.toggleProgress(EventType.SUBMIT_ORDER_CANCELLATION, true);
        }
        subscription = interactor.submitCancellationRequest(requestBody)
                .subscribe(new Action1<OrderCancellationResponse>() {
                    @Override
                    public void call(OrderCancellationResponse orderCancellationResponse) {
                        if (view != null) {
                            view.toggleProgress(EventType.SUBMIT_ORDER_CANCELLATION, false);

                            if (orderCancellationResponse.isSuccess()) {
                                view.navigateToCancellationSuccessPage();
                            } else {
                                view.showServerError(EventType.SUBMIT_ORDER_CANCELLATION, orderCancellationResponse);
                            }
                        }
                    }
                }, new Action1<Throwable>() {
                    @Override
                    public void call(Throwable throwable) {
                        if (view != null) {
                            view.toggleProgress(EventType.SUBMIT_ORDER_CANCELLATION, false);

                            if (!isConnected) {
                                view.showOfflineMessage(EventType.SUBMIT_ORDER_CANCELLATION);
                            } else if (throwable instanceof HttpException) {
                                view.showConnectionError(EventType.SUBMIT_ORDER_CANCELLATION);
                            } else {
                                view.showRetry(EventType.SUBMIT_ORDER_CANCELLATION);
                            }
                        }
                    }
                });
    }

    @Override
    public void setView(OrderCancellationView view) {
        this.view = view;
    }

    @Override
    public void destroy() {
        if (subscription != null && !subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        subscription = null;
        view = null;
    }
}
