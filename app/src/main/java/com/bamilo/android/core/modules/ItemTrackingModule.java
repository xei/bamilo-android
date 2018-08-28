package com.bamilo.android.core.modules;

import com.bamilo.android.core.interaction.ItemTrackingInteractor;
import com.bamilo.android.core.interaction.ItemTrackingInteractorImpl;
import com.bamilo.android.core.presentation.ItemTrackingPresenter;
import com.bamilo.android.core.presentation.ItemTrackingPresenterImpl;
import com.bamilo.android.core.view.ItemTrackingView;

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
    public ItemTrackingView provideItemTrackingView() {
        return itemTrackingView;
    }

    @Provides
    public ItemTrackingPresenter provideItemTrackingPresenter(ItemTrackingPresenterImpl itemTrackingPresenter) {
        itemTrackingPresenter.setView(itemTrackingView);
        return itemTrackingPresenter;
    }

    @Provides
    public ItemTrackingInteractor provideItemTrackingInteractor(ItemTrackingInteractorImpl itemTrackingInteractor) {
        return itemTrackingInteractor;
    }
}
