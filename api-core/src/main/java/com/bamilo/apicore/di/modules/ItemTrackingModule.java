package com.bamilo.apicore.di.modules;

import com.bamilo.apicore.interaction.ItemTrackingInteractor;
import com.bamilo.apicore.interaction.ItemTrackingInteractorImpl;
import com.bamilo.apicore.presentation.ItemTrackingPresenter;
import com.bamilo.apicore.presentation.ItemTrackingPresenterImpl;
import com.bamilo.apicore.view.ItemTrackingView;

import dagger.Module;
import dagger.Provides;

/**
 * Created by mohsen on 1/27/18.
 */
@Module
public class ItemTrackingModule {
    private ItemTrackingView itemTrackingView;

    public ItemTrackingModule(ItemTrackingView ordersListView) {
        this.itemTrackingView = ordersListView;
    }

    @Provides
    public ItemTrackingView provideOrdersListView() {
        return itemTrackingView;
    }

    @Provides
    public ItemTrackingPresenter provideOrdersListPresentater(ItemTrackingPresenterImpl itemTrackingPresenter) {
        itemTrackingPresenter.setView(itemTrackingView);
        return itemTrackingPresenter;
    }

    @Provides
    public ItemTrackingInteractor provideOrdersListInteractor(ItemTrackingInteractorImpl itemTrackingInteractor) {
        return itemTrackingInteractor;
    }
}
