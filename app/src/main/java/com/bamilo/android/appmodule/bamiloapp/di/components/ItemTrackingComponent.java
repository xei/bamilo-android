package com.bamilo.android.appmodule.bamiloapp.di.components;

import com.bamilo.android.core.modules.ItemTrackingModule;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.ItemTrackingFragment;

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
