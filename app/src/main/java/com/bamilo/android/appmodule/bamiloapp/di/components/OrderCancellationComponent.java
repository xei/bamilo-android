package com.bamilo.android.appmodule.bamiloapp.di.components;

import com.bamilo.android.core.modules.OrderCancellationModule;
import com.bamilo.android.appmodule.bamiloapp.view.fragments.OrderCancellationFragment;

import dagger.Subcomponent;

/**
 * Created by mohsen on 1/31/18.
 */

@Subcomponent(modules = {
        OrderCancellationModule.class
})
public interface OrderCancellationComponent {
    void inject(OrderCancellationFragment fragment);
}
