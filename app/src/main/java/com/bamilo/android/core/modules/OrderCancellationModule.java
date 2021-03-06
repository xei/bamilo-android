package com.bamilo.android.core.modules;

import com.bamilo.android.core.interaction.OrderCancellationInteractor;
import com.bamilo.android.core.interaction.OrderCancellationInteractorImpl;
import com.bamilo.android.core.presentation.OrderCancellationPresenter;
import com.bamilo.android.core.presentation.OrderCancellationPresenterImpl;
import com.bamilo.android.core.view.OrderCancellationView;

import dagger.Module;
import dagger.Provides;

/**
 * Created by mohsen on 1/31/18.
 */

@Module
public class OrderCancellationModule {
    private OrderCancellationView view;

    public OrderCancellationModule(OrderCancellationView view) {
        this.view = view;
    }

    @Provides
    public OrderCancellationView provideOrderCancellationView() {
        return this.view;
    }

    @Provides
    public OrderCancellationPresenter provideOrderCancellationPresenter(OrderCancellationPresenterImpl presenter) {
        presenter.setView(this.view);
        return presenter;
    }

    @Provides
    public OrderCancellationInteractor provideOrderCancellationInteractor(OrderCancellationInteractorImpl interactor) {
        return interactor;
    }
}
