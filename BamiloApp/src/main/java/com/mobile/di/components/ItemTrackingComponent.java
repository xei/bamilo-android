package com.mobile.di.components;

import com.bamilo.apicore.di.modules.ItemTrackingModule;
import com.mobile.view.fragments.ItemTrackingFragment;

import dagger.Subcomponent;

/**
 * Created by mohsen on 1/27/18.
 */
@Subcomponent(modules = {
        ItemTrackingModule.class
})
public interface ItemTrackingComponent {
    void inject(ItemTrackingFragment fragment);
}
