package com.mobile.di.components;

import com.bamilo.apicore.di.modules.OrderCancellationModule;
import com.mobile.view.fragments.OrderCancellationFragment;

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
